package com.bizpulse.app.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.bizpulse.app.data.db.entity.AuditLogEntity
@Dao interface AuditLogDao {
    @Insert suspend fun insert(row: AuditLogEntity): Long
    @Update suspend fun update(row: AuditLogEntity)
    @Delete suspend fun delete(row: AuditLogEntity)
    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC") fun getAll(): Flow<List<AuditLogEntity>>
    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC") suspend fun snapshot(): List<AuditLogEntity>
    @Query("SELECT * FROM audit_log WHERE id = :id") suspend fun getById(id: Long): AuditLogEntity?
    @Query("DELETE FROM audit_log") suspend fun clear()
    @Query("SELECT * FROM audit_log WHERE action LIKE '%' || :term || '%' OR details LIKE '%' || :term || '%' OR userName LIKE '%' || :term || '%' ORDER BY timestamp DESC") fun search(term: String): Flow<List<AuditLogEntity>>
    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC LIMIT :limit") fun getRecent(limit: Int): Flow<List<AuditLogEntity>>
}
