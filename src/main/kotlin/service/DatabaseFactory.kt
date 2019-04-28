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
            }
            val cupCard = PaymentOption.new {
                name = "CUP/CARD"
            }
            val qr = PaymentOption.new {
                name = "QR"
            }
            val thaiCard = PaymentOption.new {
                name = "THAI CARD"
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