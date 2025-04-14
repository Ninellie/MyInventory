package com.example.myinventory.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myinventory.data.models.DeviceModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceModelDao {
    @Query("SELECT * FROM device_models")
    fun getAll(): Flow<List<DeviceModel>>

    @Query("SELECT * FROM device_models WHERE vendorId = :vendorId AND deviceTypeId = :typeId")
    fun getByVendorAndType(vendorId: Int, typeId: Int): Flow<List<DeviceModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: DeviceModel)

    @Update
    suspend fun update(model: DeviceModel)

    @Delete
    suspend fun delete(model: DeviceModel)
}

