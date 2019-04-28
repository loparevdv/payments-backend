import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.call
import io.ktor.request.receive
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
import model.PaymentOption
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.paymentOption(paymentOptionService: PaymentOptionService) {
    route("/payment_option") {

        get("/") {
            val Log = Logger.getLogger("")
            val pos: List<PaymentOption> = PaymentOptionService().getAllPaymentOptions()
            call.respond(pos.map { it.toDC() })
        }

        get("/{isocode}") {
            val Log = Logger.getLogger("")
            val pos: List<PaymentOption> = PaymentOptionService().getByISOCode(call.parameters["isocode"]!!)
            Log.warning(pos.toString())
            call.respond(pos.map { it.toDC() })
        }

        post("/") {
            val Log = Logger.getLogger("")
            val mapper = jacksonObjectMapper()
            val dcpaymentOption: DCPaymentOption = mapper.readValue(call.receiveText())
            Log.warning(dcpaymentOption.name)

            transaction {
                val newPaymentOption = PaymentOption.new {
                    name = dcpaymentOption.name
                }
                Log.warning(newPaymentOption.id.toString())
            }
            call.respond(paymentOptionService.getAllPaymentOptions())
        }
    }
}