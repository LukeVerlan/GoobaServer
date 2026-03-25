package goobaserver.db


import goobaserver.model.*
import goobaserver.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UserService {
    /** Add a user to the DB */
    suspend fun addUser(user: User):User?
    /** Delete a user from the DB */
    suspend fun deleteUser(id: Int):Boolean
    /** Update a user entry on the DB */
    suspend fun updateUser(user: User):Boolean
    /** Retrieve a user from the DB */
    suspend fun getUsers():List<User>
    /** Search for users on the db */
    suspend fun searchUser(query:String):List<User>
    /** Retrieve a user by ID */
    suspend fun getUserById(id:Int):User?
    /** Clear all users on the DB */
    suspend fun clearUsers(): Int
}

class UserServiceImpl : UserService {

    /** Returns user found in the resulting row */
    private fun resultRowToUser(row: ResultRow):User {
        return User(
            id = row[Users.id],
            name = row[Users.name],
            scoopDay = row[Users.scoopDay]
        )
    }

    /** Updates users on the DB
     * Updates all users with the matching id and applies the new user information
     * @param user The information to swap with existing user
     * @return True if number of users updated is greater than 0
     */
    override suspend fun updateUser(user: User): Boolean = dbQuery {
        Users.update({Users.id eq user.id}) {
            it[name]=user.name
            it[scoopDay] = user.scoopDay
        }>0
    }

    /** Adds a user to the DB
     * Adds a user to the DB, incrementing the id based on the last stored id
     * @param user User to be added
     * @return user if added, null if failed
     */
    override suspend fun addUser(user: User): User? = dbQuery {
        val insertStmt = Users.insert {
            it[name]=user.name
            it[scoopDay]=user.scoopDay
        }
        insertStmt.resultedValues?.singleOrNull()?.let {resultRowToUser(it)}
    }

    /** Get all users on the database
     * Retrieves all users
     * @return User list
     */
    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map { resultRowToUser(it) }
    }

    /** Delete a user of a given name from the DB
     * @param User
     * @return True if number of users removed is greater than 0
     */
    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id} >0
    }

    /** Find a user on the database of given name, ignores case
     * @param query The name to search for
     * @return List of users with the given name
     */
    override suspend fun searchUser(query: String): List<User> = dbQuery {
        Users.selectAll().where { Users.name.lowerCase() like "%${query.lowercase()}%" }
            .map { resultRowToUser(it) }
    }

    /** Retrieve a user by id
     *  @param id user id
     *  @return User with id that matches the searched id
     */
    override suspend fun getUserById(id: Int): User? = dbQuery {
        Users.select(Users.id eq id ).map { resultRowToUser(it) }.singleOrNull()
    }

    /** Clears all exiting users from the DB */
    override suspend fun clearUsers(): Int = dbQuery {
        Users.deleteAll()
    }
}