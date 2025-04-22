package com.example.myinventory.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myinventory.data.models.FieldType
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldTypeDao {
    @Query("SELECT * FROM field_types")
    fun getAll(): Flow<List<FieldType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fieldType: FieldType)

    @Update
    suspend fun update(fieldType: FieldType)

    @Delete
    suspend fun delete(fieldType: FieldType)
}


