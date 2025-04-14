package com.example.myinventory.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    lateinit var db: AppDatabase
        private set

    fun init(context: Context) {
        db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "myInventory.db"
        ).fallbackToDestructiveMigration(false)
            .build()
    }
}

