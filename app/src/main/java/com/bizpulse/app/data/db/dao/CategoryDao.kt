package com.bizpulse.app.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.bizpulse.app.data.db.entity.CategoryEntity
@Dao interface CategoryDao {
    @Insert suspend fun insert(row: CategoryEntity): Long
    @Update suspend fun update(row: CategoryEntity)
    @Delete suspend fun delete(row: CategoryEntity)
    @Query("SELECT * FROM categories ORDER BY id") fun getAll(): Flow<List<CategoryEntity>>
    @Query("SELECT * FROM categories ORDER BY id") suspend fun snapshot(): List<CategoryEntity>
    @Query("SELECT * FROM categories WHERE id = :id") suspend fun getById(id: Long): CategoryEntity?
    @Query("DELETE FROM categories") suspend fun clear()
    
}
