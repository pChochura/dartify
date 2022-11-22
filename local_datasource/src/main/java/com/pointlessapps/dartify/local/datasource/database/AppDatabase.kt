package com.pointlessapps.dartify.local.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pointlessapps.dartify.local.datasource.database.players.daos.PlayersDao
import com.pointlessapps.dartify.local.datasource.database.players.models.PlayerEntity

@Database(
    entities = [PlayerEntity::class],
    version = 1,
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun playersDao(): PlayersDao

    companion object {
        fun get(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db",
        ).fallbackToDestructiveMigration().build()
    }
}