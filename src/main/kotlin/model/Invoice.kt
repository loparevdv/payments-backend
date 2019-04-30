package model

import org.jetbrains.exposed.dao.IntIdTable


object Invoices: IntIdTable() {
    val payload = varchar("payload", 1024)
    val paymentOption = reference("paymentOption", PaymentOptions).primaryKey(0)
}


data class DCInvoice(
    val payload: String,
    val paymentOption: String
)