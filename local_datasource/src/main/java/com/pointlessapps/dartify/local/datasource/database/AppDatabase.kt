package com.pointlessapps.dartify.local.datasource.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pointlessapps.dartify.local.datasource.database.game.x01.daos.GameX01Dao
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01Entity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01InputEntity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01PlayersEntity
import com.pointlessapps.dartify.local.datasource.database.players.daos.PlayersDao
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

@Database(
    entities = [
        PlayerEntity::class,
        GameX01Entity::class,
        GameX01InputEntity::class,
        GameX01PlayersEntity::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    version = 2,
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun playersDao(): PlayersDao
    abstract fun gameX01Dao(): GameX01Dao

    companion object {
        fun get(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db",
        ).fallbackToDestructiveMigration()
            .build()
    }
}