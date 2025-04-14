package com.example.myinventory.data

import androidx.room.TypeConverter
import java.util.stream.Collectors

class Converters {
    @TypeConverter
    fun fromIdList(ids: List<Int?>): String {
        return ids.stream()
            .map { it?.toString() ?: "" }
            .collect(Collectors.joining(","))
    }

    @TypeConverter
    fun toIdList(data: String): List<Int> {
        return data.split(",".toRegex())
            .dropLastWhile { it.isEmpty() }
            .mapNotNull { it.toIntOrNull() }
    }
}