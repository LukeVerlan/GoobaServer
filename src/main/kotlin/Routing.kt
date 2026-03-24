package goobaserver

import goobaserver.db.TaskService
import goobaserver.db.UserService
import goobaserver.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(userService: UserService=get(),
                                 taskService: TaskService =get()) {
    routing {
        userRoute(userService)
        taskRoutes(taskService)
    }
}
