package goobaserver.routes

import goobaserver.db.TaskService
import goobaserver.model.Task
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.taskRoutes(taskService: TaskService) {
    route("/tasks"){

        get {
            val tasks = taskService.getAllTasks()
            call.respond(HttpStatusCode.OK, tasks)
        }

        post {
            val task = call.receive<Task>()
            try {
                val result= taskService.addTask(task)
                result?.let {
                    call.respond(HttpStatusCode.OK, result)
                } ?: call.respond(HttpStatusCode.BadRequest, message="Error adding task")
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.BadRequest, message="BAD REQUEST")
            }
        }

        delete {
            val task = call.receive<Task>()
            val result = taskService.deleteTask(task.type)
            if(result) {
                call.respond(HttpStatusCode.OK, message="Deleted task")
            } else {
                call.respond(HttpStatusCode.BadRequest, message="Failed to delete task")
            }
        }

        put {
            val task = call.receive<Task>()
            val result = taskService.updateTask(task)
            if(result) {
                call.respond(HttpStatusCode.OK, message = "Updated Task")
            } else {
                call.respond(HttpStatusCode.BadRequest, message="Failed to update Task")
            }
        }

        delete("/clear") {
            taskService.clearTasks()
            call.respond(HttpStatusCode.OK, message = "Tasks cleared")
        }

        get("/search/{type}") {
            val type = call.parameters["type"]

            if (type.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, message="Type parameter is required")
                return@get
            }

            val task = taskService.getTask(type)
            if(task != null) {
                call.respond(HttpStatusCode.OK, task)
            } else {
                call.respond(HttpStatusCode.BadRequest, message="Task not found")
            }
        }
    }
}