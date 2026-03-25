package goobaserver.routes

import goobaserver.model.*
import goobaserver.db.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.userRoute(userService: UserService) {
    route("/users") {
        get {
            val users=userService.getUsers()
            call.respond(HttpStatusCode.OK, users)
        }

        post {
            val user=call.receive<User>()
            try {
                val result=userService.addUser(user)
                result?.let {
                    call.respond(HttpStatusCode.Created, it)
                } ?: call.respond(HttpStatusCode.NotImplemented, "Error adding user")
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "SQL Exception!!")
            }
        }

        put {
            try {
                val user=call.receive<User>()
                val result=userService.updateUser(user)
                if (result) {
                    call.respond(HttpStatusCode.OK, "Update Successful")
                } else {
                    call.respond(HttpStatusCode.NotImplemented, "Update Failed")
                }
            } catch (e: ExposedSQLException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "SQL Exception!!")
            }
        }

        delete("/{id}"){
            val id = call.parameters["id"]?.toInt()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID required")
                return@delete
            }

            val result=userService.deleteUser(id)

            if (result){
                call.respond(HttpStatusCode.OK,"Delete successful")
            }else{
                call.respond(HttpStatusCode.NotFound,"Delete not done")
            }
        }

        get("/search/id/{id}") {
            try {
                val id = call.parameters["id"]?.toInt()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                    return@get
                }

                val result = userService.getUserById(id)

                if (result != null) {
                    call.respond(HttpStatusCode.OK, result)
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } catch (e: Exception) {
                // This will print the actual error to your console
                application.log.error("Failed to fetch user", e)
                call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
            }
        }

        get("/search/name/{name}") {
            val name=call.parameters["name"] // Get the name from the search

            if (name.isNullOrBlank()){ // Check if the name is viable, if not exit early
                call.respond(HttpStatusCode.BadRequest,"Name is required")
                return@get
            }

            val users = userService.searchUser(name) // Search for users with given name
            if(users.isNotEmpty()){
                call.respond(HttpStatusCode.OK, users)
            } else {
                call.respond(HttpStatusCode.NotFound,"No Users Found")
            }
        }

        get("/clear") {
            userService.clearUsers()
        }

    }
}