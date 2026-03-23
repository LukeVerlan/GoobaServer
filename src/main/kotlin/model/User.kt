package goobaserver.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import kotlinx.datetime.DayOfWeek

@Serializable
data class User (
    val name: String,
    val scoopDay: String,
    val id: Int = 0
)

object Users : Table() {

    val id = integer(name = "id").autoIncrement()
    val name= varchar(name = "name", length = 255)
    val scoopDay = varchar(name = "scoopDay", length=20)

    override val primaryKey : PrimaryKey
        get() = PrimaryKey(id)
}
