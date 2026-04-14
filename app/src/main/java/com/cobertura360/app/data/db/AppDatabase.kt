package com.cobertura360.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cobertura360.app.data.dao.BaseConfigDao
import com.cobertura360.app.data.dao.SessionDao
import com.cobertura360.app.data.dao.UserDao
import com.cobertura360.app.data.entity.BaseConfigEntity
import com.cobertura360.app.data.entity.SessionEntity
import com.cobertura360.app.data.entity.UserEntity

@Database(
    entities = [UserEntity::class, BaseConfigEntity::class, SessionEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun baseConfigDao(): BaseConfigDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cobertura360.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Clear function para testing
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
