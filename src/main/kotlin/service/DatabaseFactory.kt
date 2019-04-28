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

fun initialMigration() {
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
        logoUrl = "https://content.heropay.com/wp-content/uploads/2016/11/China-UnionPay-e1486167123241-768x492.jpg"
    }
    val cupCard = PaymentOption.new {
        name = "CUP/CARD"
        description = "China Union Pay Debit Card"
        url = "https://store.raywenderlich.com/products/kotlin-apprentice"
        logoUrl = "http://meihuey.weebly.com/uploads/1/3/9/4/13944488/5413420_orig.jpg"
    }
    val qr = PaymentOption.new {
        name = "QR"
        description = "Thai QR Royal service"
        url = "https://store.raywenderlich.com/products/swift-apprentice"
        logoUrl = "https://d2lp05f39ek59n.cloudfront.net/uploads/SiamPay_product_img_241794887_siampay.png"
    }
    val thaiCard = PaymentOption.new {
        name = "THAI CARD"
        description = "Thai national card system"
        url = "https://store.raywenderlich.com/products/machine-learning-by-tutorials"
        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/a/ab/TMBOfficialLogo2015.jpg"
    }
    thai.paymentOptions = SizedCollection(listOf(qr, thaiCard))
    china.paymentOptions = SizedCollection(listOf(cup, cupCard))
}

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
        transaction {
            create(Countries)
            create(PaymentOptions)
            create(CountryOptions)
            initialMigration()
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