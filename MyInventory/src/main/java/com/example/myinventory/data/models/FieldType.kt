package com.example.myinventory.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_types")
data class FieldType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val valueType: String
)


