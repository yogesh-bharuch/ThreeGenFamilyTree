package com.example.threegenfamilytree.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database for storing family tree members.
 */
@Database(entities = [ThreeGen::class], version = 1, exportSchema = false)
abstract class ThreeGenDatabase : RoomDatabase() {

    abstract fun threeGenDao(): ThreeGenDao

    companion object {
        @Volatile
        private var INSTANCE: ThreeGenDatabase? = null

        fun getDatabase(context: Context): ThreeGenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ThreeGenDatabase::class.java,
                    "three_gen_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
