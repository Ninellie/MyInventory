package com.example.myinventory.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.myinventory.data.Converters

@TypeConverters(Converters::class)
@Entity(tableName = "device_types")
data class DeviceType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,

    @ColumnInfo (defaultValue = "")
    val fieldTypeIdList: List<Int?>
)
