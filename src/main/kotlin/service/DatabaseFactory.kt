package service

import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.HikariConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
        transaction {
            create(Countries)
            create(PaymentOptions)
            create(CountryOptions)

            val china = Country.new {
                name = "China"
                isocode = "CN"
            }
            val thai = Country.new {
                name = "Thailand"
                isocode = "TH"
            }
            val cup = PaymentOption.new {
                name = "CUP"
                description = "China Union Pay"
                url = "https://store.raywenderlich.com/products/android-apprentice"
                logoUrl = "https://files.kerching.raywenderlich.com/mockups/71eb5ec4-4553-4d06-ac57-be2e2ef38e45.png"
            }
            val cupCard = PaymentOption.new {
                name = "CUP/CARD"
                description = "China Union Pay Debit Card"
                url = "https://store.raywenderlich.com/products/kotlin-apprentice"
                logoUrl = "https://files.kerching.raywenderlich.com/mockups/7d9154f0-41da-459c-b2a2-cb01869a9ee5.png"
            }
            val qr = PaymentOption.new {
                name = "QR"
                description = "Thai QR Royal service"
                url = "https://store.raywenderlich.com/products/swift-apprentice"
                logoUrl = "https://files.kerching.raywenderlich.com/mockups/53ca338b-0797-4027-8df0-f4e06cac6463.png"
            }
            val thaiCard = PaymentOption.new {
                name = "THAI CARD"
                description = "Thai national card system"
                url = "https://store.raywenderlich.com/products/machine-learning-by-tutorials"
                logoUrl = "https://files.kerching.raywenderlich.com/mockups/2e38ffeb-be15-4864-8899-d1f060b93291.png"
            }
            thai.paymentOptions = SizedCollection(listOf(qr, thaiCard))
            china.paymentOptions = SizedCollection(listOf(cup, cupCard))
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

}