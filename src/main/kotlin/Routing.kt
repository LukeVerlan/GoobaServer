package goobaserver

import goobaserver.db.UserService
import goobaserver.routes.userRoute
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.slf4j.event.*
import org.koin.ktor.ext.get

fun Application.configureRouting(userService: UserService=get()) {
    routing {
        userRoute(userService)
    }
}
