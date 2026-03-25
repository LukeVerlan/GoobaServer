package goobaserver.db

import goobaserver.dbQuery
import goobaserver.model.Task
import goobaserver.model.Tasks
import goobaserver.model.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface TaskService {

    // Add a task to DB
    suspend fun addTask(task: Task):Task?

    // Delete task from DB
    suspend fun deleteTask(type : String):Boolean

    // Update a task in the DB
    suspend fun updateTask(task: Task):Boolean

    // Get all Tasks
    suspend fun getAllTasks():List<Task>

    // Clear all Tasks
    suspend fun clearTasks():Int

    // Search for task by type
    suspend fun getTaskNameAndDate(type: String, date: String):Task?

    suspend fun getTaskName(type: String) : List<Task>
    suspend fun getTaskDate(date: String): List<Task>

}

class TaskServiceImpl : TaskService {

    // Converts a database row to a task
    private fun resultRowToTask(row: ResultRow): Task {
        return Task(
            type = row[Tasks.type],
            time = row[Tasks.time],
            date = row[Tasks.date],
            userID = row[Tasks.userID],
        )
    }

    /** Adds a task to the DB
     * @param task Task information
     * @return True if added, false if failed
     */
    override suspend fun addTask(task: Task): Task? = dbQuery {
        val insertStmt = Tasks.insert {
            it[type]=task.type
            it[userID]=task.userID
            it[date]=task.date
            it[time]=task.time
        }
        insertStmt.resultedValues?.singleOrNull()?.let {resultRowToTask(it)}
    }

    /** Deletes a task from the DB
     * @param Task Task with Primary id for the desired task
     * @return True if deleted, false if failed
     */
    override suspend fun deleteTask(type : String): Boolean = dbQuery {
        Tasks.deleteWhere { Tasks.type.upperCase() eq type.uppercase() } > 0
    }

    /** Updates a task on the DB
     * @param task Primary id for the task
     * @return True if update was successful, false if failed
     */
    override suspend fun updateTask(task: Task): Boolean = dbQuery {
        Tasks.update({Tasks.type.upperCase() eq task.type.uppercase()}) {
            it[userID]=task.userID
            it[date]=task.date
            it[time]=task.time
        } > 0
    }

    /** Gets all tasks on the DB
     * @return the list of tasks on the DB
     */
    override suspend fun getAllTasks(): List<Task> = dbQuery {
        Tasks.selectAll().map { resultRowToTask(it)}
    }

    /** Clears all tasks on the DB
     * @return Number of tasks deleted
     */
    override suspend fun clearTasks(): Int = dbQuery {
        Tasks.deleteAll()

    }

    /** Gets a given task of the DB
     * @param type Primary ID of the task
     * @return The task, null if fails
     */
    override suspend fun getTaskName(type: String): List<Task> = dbQuery {
        Tasks.selectAll().where { Tasks.type.upperCase() like "%${type.uppercase()}%" }
            .map { resultRowToTask(it) }
    }

    /** Retrieves a Task from the database by date and time
     * @param type Task Type
     * @param date Task Date
     * @return Task that matches both query's
      */
    override suspend fun getTaskNameAndDate(type: String, date: String): Task? = dbQuery {
        Tasks.selectAll().where { (Tasks.type.upperCase() eq "%${type.uppercase()}%") and (Tasks.date.upperCase() like "%${date.uppercase()}%") }
            .map { resultRowToTask(it) }.firstOrNull()
    }

    /** Returns a list of tasks that match the given date
     * @param date Date of tasks
     * @return List of tasks
     */
    override suspend fun getTaskDate(date: String) = dbQuery {
        Tasks.selectAll().where { (Tasks.date.upperCase() eq "%${date.uppercase()}%") }
            .map { resultRowToTask(it) }
    }

}