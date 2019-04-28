package model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object PaymentOptions : IntIdTable() {
    val name = varchar("name", 255)
}

data class DCPaymentOption(
    val name: String
)

class PaymentOption(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PaymentOption>(PaymentOptions)

    var name by PaymentOptions.name

    fun toDC(): DCPaymentOption =
        DCPaymentOption(
            name = name
        )
}

object Countries : IntIdTable() {
    val name = varchar("name", 255)
    val isocode = varchar("isocode", 2)
}


class Country(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Country>(Countries)

    var name by Countries.name
    var isocode by Countries.isocode
    var paymentOptions by PaymentOption via CountryOptions
}


object CountryOptions : Table() {
    val paymentOption = reference("paymentOption", PaymentOptions).primaryKey(0)
    val country = reference("country", Countries).primaryKey(1)
}