package com.example.myinventory.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "device_models",
    foreignKeys = [
        ForeignKey(
            entity = Vendor::class,
            parentColumns = ["id"],
            childColumns = ["vendorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DeviceType::class,
            parentColumns = ["id"],
            childColumns = ["deviceTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vendorId"), Index("deviceTypeId")]
)
data class DeviceModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val vendorId: Int,
    val deviceTypeId: Int
)


