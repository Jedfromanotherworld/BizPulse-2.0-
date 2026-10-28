package com.bizpulse.app.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.bizpulse.app.data.db.entity.TransactionEntity
@Dao interface TransactionDao {
    @Insert suspend fun insert(row: TransactionEntity): Long
    @Update suspend fun update(row: TransactionEntity)
    @Delete suspend fun delete(row: TransactionEntity)
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC") fun getAll(): Flow<List<TransactionEntity>>
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC") suspend fun snapshot(): List<TransactionEntity>
    @Query("SELECT * FROM transactions WHERE id = :id") suspend fun getById(id: Long): TransactionEntity?
    @Query("DELETE FROM transactions") suspend fun clear()
    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC, time DESC") fun getByDateRange(start: String, end: String): Flow<List<TransactionEntity>>
    @Query("SELECT * FROM transactions WHERE cashierId = :id ORDER BY createdAt DESC") fun getByCashier(id: Long): Flow<List<TransactionEntity>>
    @Transaction @Query("SELECT * FROM transactions ORDER BY date DESC, time DESC") fun withItems(): Flow<List<com.bizpulse.app.data.db.relation.TransactionWithItems>>
}
