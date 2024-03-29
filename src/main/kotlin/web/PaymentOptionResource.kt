import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.call
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import model.DCPaymentOption
import service.PaymentOptionService
import java.util.logging.Logger
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.HttpStatusCode
import model.Invoice
import model.PaymentOption
import model.PaymentOptions
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.paymentOption(paymentOptionService: PaymentOptionService) {
    route("/payment_option") {

        get("/") {
            val Log = Logger.getLogger("")
            val pos: List<PaymentOption> = PaymentOptionService().getAllPaymentOptions()
            call.respond(pos.map { it.toDC() })
        }

        get("{codename}") {
            val Log = Logger.getLogger("")
            val pos: PaymentOption = PaymentOptionService().getByCodename(call.parameters["codename"]!!)
            call.respond(pos.toDC())
        }

        post("{codename}") {
            val Log = Logger.getLogger("")

            val requestText = call.receiveText()
            val mapper = jacksonObjectMapper()
            val requestMap: Map<String, String> = mapper.readValue(requestText)
            val isValid = requestMap.all { it.component2() != "" }
            var invoice: Invoice? = null
            var errors: Map<String, String>? = null

            if (isValid) {
                transaction {
                    invoice = Invoice.new {
                        payload = requestText
                        paymentOption = PaymentOption.find {
                            PaymentOptions.codename eq call.parameters["codename"]!!
                        }.firstOrNull()!!
                    }
                }
                call.respond("""{"invoice_id": ${invoice!!.id}}""")
            } else {
                errors = requestMap.filter {
                    it.component2() == ""
                }.toMap().map {
                    it.component1() to "Not empty required"
                }.toMap()
            }
            val jsonErrors = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors)
            call.respond(HttpStatusCode.BadRequest, jsonErrors!!)
        }

//        get("/{isocode}") {
//            val Log = Logger.getLogger("")
//            val pos: List<PaymentOption> = PaymentOptionService().getByISOCode(call.parameters["isocode"]!!)
//            Log.warning(pos.toString())
//            call.respond(pos.map { it.toDC() })
//        }

        post("/") {
            val Log = Logger.getLogger("")
            val mapper = jacksonObjectMapper()
            val dcpaymentOption: DCPaymentOption = mapper.readValue(call.receiveText())
            Log.warning(dcpaymentOption.name)

            transaction {
                // TODO: refactor for fromDCPaymentOption
                val newPaymentOption = PaymentOption.new {
                    name = dcpaymentOption.name
                }
                Log.warning(newPaymentOption.id.toString())
            }
            call.respond(paymentOptionService.getAllPaymentOptions())
        }
    }
}