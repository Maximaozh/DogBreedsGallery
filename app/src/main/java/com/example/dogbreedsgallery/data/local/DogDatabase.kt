package com.example.dogbreedsgallery.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BreedEntity::class, BreedImageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DogDatabase : RoomDatabase() {

    abstract fun breedDao(): BreedDao

    companion object {

        @Volatile
        private var INSTANCE: DogDatabase? = null

        fun getInstance(context: Context): DogDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DogDatabase::class.java,
                    "dog_breeds.db")
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}