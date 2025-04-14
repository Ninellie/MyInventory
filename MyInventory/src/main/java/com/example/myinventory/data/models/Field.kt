package com.example.myinventory.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "fields",
    foreignKeys = [
        ForeignKey(
            entity = FieldType::class,
            parentColumns = ["id"],
            childColumns = ["fieldTypeId"]
        ),
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"]
        ),
    ],
    indices = [Index("fieldTypeId"), Index("deviceId")])
data class Field(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fieldTypeId: Int,
    val deviceId: Int,
    val value: String
)
