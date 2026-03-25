package goobaserver.model

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Task (
    val type: String,
    val userID:Int,
    val date:String,
    val time:String
)

object Tasks : Table() {
    val userID = reference("userID", Users.id)
    val date = varchar("date", length = 255)
    val time = varchar("time", length = 255)
    // Task type
    val type = varchar("type", 50)

    override val primaryKey = PrimaryKey( date, type)
}