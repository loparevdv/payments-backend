package service

import model.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import service.DatabaseFactory.dbQuery

class PaymentOptionService {

    fun getAllPaymentOptions(): List<PaymentOption> =
        transaction {
            PaymentOption.all().toList()
        }

    fun getByISOCode(isocode: String): List<PaymentOption> =
        // TODO: makes to queries, to be refactored
        transaction {
            val country = Country.find {
                Countries.isocode eq isocode
            }.firstOrNull()
            country!!.paymentOptions.toList()
        }

}