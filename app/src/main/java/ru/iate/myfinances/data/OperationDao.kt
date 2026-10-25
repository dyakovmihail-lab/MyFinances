package ru.iate.myfinances.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {

    @Query("SELECT * FROM operations ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<Operation>>

    @Query("SELECT * FROM operations WHERE id = :id")
    fun observeById(id: Long): Flow<Operation?>

    @Query("SELECT * FROM operations WHERE id = :id")
    suspend fun getById(id: Long): Operation?

    @Insert
    suspend fun insert(operation: Operation): Long

    @Update
    suspend fun update(operation: Operation)

    @Delete
    suspend fun delete(operation: Operation)
}
