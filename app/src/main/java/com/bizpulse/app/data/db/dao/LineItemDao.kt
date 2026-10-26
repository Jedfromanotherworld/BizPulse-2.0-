package com.bizpulse.app.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.bizpulse.app.data.db.entity.LineItemEntity
@Dao interface LineItemDao {
    @Insert suspend fun insert(row: LineItemEntity): Long
    @Update suspend fun update(row: LineItemEntity)
    @Delete suspend fun delete(row: LineItemEntity)
    @Query("SELECT * FROM line_items ORDER BY id") fun getAll(): Flow<List<LineItemEntity>>
    @Query("SELECT * FROM line_items ORDER BY id") suspend fun snapshot(): List<LineItemEntity>
    @Query("SELECT * FROM line_items WHERE id = :id") suspend fun getById(id: Long): LineItemEntity?
    @Query("DELETE FROM line_items") suspend fun clear()
    @Insert suspend fun insert(rows: List<LineItemEntity>)
    @Query("SELECT * FROM line_items WHERE transactionId = :id") suspend fun getByTransaction(id: Long): List<LineItemEntity>
}
