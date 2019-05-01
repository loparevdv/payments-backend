package model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable


object Invoices: IntIdTable() {
    val payload = varchar("payload", 1024)
    val paymentOption = reference("paymentOption", PaymentOptions).primaryKey(0)
}


data class DCInvoice(
    val payload: String,
    val paymentOption: String
)

class Invoice(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Invoice>(Invoices)

    var payload by Invoices.payload
    var paymentOption by PaymentOption referencedOn Invoices.paymentOption

    fun toDC(): DCInvoice =
        DCInvoice(
            payload = payload,
            paymentOption = paymentOption.codename
        )
}
