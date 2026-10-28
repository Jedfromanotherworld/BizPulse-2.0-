package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName = "audit_log", indices = [Index("timestamp"), Index("type")])
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(), val userName: String,
    val action: String, val details: String, val type: String
)
