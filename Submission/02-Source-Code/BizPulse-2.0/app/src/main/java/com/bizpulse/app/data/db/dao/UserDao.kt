package com.bizpulse.app.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.bizpulse.app.data.db.entity.UserEntity
@Dao interface UserDao {
    @Insert suspend fun insert(row: UserEntity): Long
    @Update suspend fun update(row: UserEntity)
    @Delete suspend fun delete(row: UserEntity)
    @Query("SELECT * FROM users ORDER BY id") fun getAll(): Flow<List<UserEntity>>
    @Query("SELECT * FROM users ORDER BY id") suspend fun snapshot(): List<UserEntity>
    @Query("SELECT * FROM users WHERE id = :id") suspend fun getById(id: Long): UserEntity?
    @Query("DELETE FROM users") suspend fun clear()
    @Query("SELECT * FROM users WHERE username = :username COLLATE NOCASE LIMIT 1") suspend fun getByUsername(username: String): UserEntity?
}
