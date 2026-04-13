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

        delete("/{date}/{type}") {
            val type = call.parameters["type"]
            val date = call.parameters["date"]

            if(type.isNullOrBlank() || date.isNullOrBlank()){
                call.respond(HttpStatusCode.BadRequest, message="Invalid parameter: $type")
                return@delete
            }

            val result = taskService.deleteTask(type, date)
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

        get("/search/type/{type}") {
            val type = call.parameters["type"]

            if (type.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, message="Type parameter is required")
                return@get
            }

            val tasks = taskService.getTaskName(type)
            call.respond(HttpStatusCode.OK, tasks)
        }

        get("/search/date/{date}"){
            val date = call.parameters["date"]

            if (date.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, message="Date parameter is required")
                return@get
            }

            val tasks = taskService.getTaskDate(date)
            call.respond(HttpStatusCode.OK, tasks)
        }

        get("/search/{date}/{type}") {
            val date = call.parameters["date"]
            val type = call.parameters["type"]

            if (type.isNullOrEmpty() || date.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, message="Type and Date parameter is required")
                return@get
            }

            val task = taskService.getTaskNameAndDate(type, date)
            if (task != null) {
                call.respond(HttpStatusCode.OK, task)
            } else {
                call.respond(HttpStatusCode.BadRequest, message="Task not found")
            }
        }

        get("/search/{year}/{month}") {
            val year = call.parameters["year"]
            val month = call.parameters["month"]

            if(year.isNullOrEmpty() || month.isNullOrEmpty()){
                call.respond(HttpStatusCode.BadRequest, message="Year and Month parameter is required")
                return@get
            }

            if(year.length != 4 || month.length != 2){
                call.respond(HttpStatusCode.BadRequest, message="Year and Month must be of proper length")
                return@get
            }

            val tasks = taskService.getMonthOfTasks(year, month)
            call.respond(HttpStatusCode.OK, tasks)

        }
    }
}