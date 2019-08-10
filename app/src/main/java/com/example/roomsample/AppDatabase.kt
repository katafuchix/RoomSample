package com.example.roomsample

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // DAOを取得する。
    abstract fun userDao(): UserDao
}