package com.example.myinventory.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myinventory.data.models.Rack
import kotlinx.coroutines.flow.Flow

@Dao
interface RackDao {
    @Query("SELECT * FROM racks")
    fun getAll(): Flow<List<Rack>>

    @Query("SELECT * FROM racks WHERE locationId = :locationId")
    fun getByLocation(locationId: Int): Flow<List<Rack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rack: Rack)

    @Update
    suspend fun update(rack: Rack)

    @Delete
    suspend fun delete(rack: Rack)
}