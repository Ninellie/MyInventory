package com.example.myinventory.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceTypeDao {
    @Query("SELECT * FROM device_types")
    fun getAll(): Flow<List<DeviceType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: DeviceType)

    @Update
    suspend fun update(type: DeviceType)

    @Delete
    suspend fun delete(type: DeviceType)
}