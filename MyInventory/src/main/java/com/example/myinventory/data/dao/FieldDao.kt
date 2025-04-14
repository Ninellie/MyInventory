package com.example.myinventory.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myinventory.data.models.Field
import com.example.myinventory.data.models.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldDao {
    @Query("SELECT * FROM fields")
    fun getAll(): Flow<List<Field>>

    @Query("SELECT * FROM fields WHERE deviceId = :deviceId")
    fun getByDevice(deviceId: Int): Flow<List<Field>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(field: Field)

    @Update
    suspend fun update(field: Field)

    @Delete
    suspend fun delete(field: Field)
}