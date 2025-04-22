package com.example.myinventory.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "devices",
    foreignKeys = [
        ForeignKey(
            entity = DeviceModel::class,
            parentColumns = ["id"],
            childColumns = ["modelId"]
        ),
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["locationId"]
        ),
        ForeignKey(
            entity = Rack::class,
            parentColumns = ["id"],
            childColumns = ["rackId"]
        ),
    ],
    indices = [Index("modelId"), Index("locationId"), Index("rackId")]
)
data class Device(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,

    @ColumnInfo(defaultValue = "")
    val modelId: Int,

    @ColumnInfo(defaultValue = "")
    val locationId: Int? = null,

    @ColumnInfo(defaultValue = "")
    val rackId: Int? = null,

    val createdAt: String,
    val updatedAt: String
)