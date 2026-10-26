package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName = "users", indices = [Index(value = ["username"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val username: String, val passwordHash: String, val role: String,
    val createdAt: Long = System.currentTimeMillis()
)
