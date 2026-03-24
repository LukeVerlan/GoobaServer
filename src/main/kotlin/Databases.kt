package goobaserver

import com.zaxxer.hikari.*
import goobaserver.model.Tasks
import goobaserver.model.Users
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.*

private fun provideDataSource(url:String,driverClass:String):HikariDataSource{
    val hikariConfig= HikariConfig().apply {
        driverClassName=driverClass
        jdbcUrl=url
        maximumPoolSize=3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}

fun Application.configureDatabases() {
    val driverClass=environment.config.property("storage.driverClassName").getString()
    val jdbcUrl=environment.config.property("storage.jdbcURL").getString()
    val db=Database.connect(provideDataSource(jdbcUrl,driverClass))
    transaction(db){
        SchemaUtils.create(Users, Tasks)
    }
}

suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}
