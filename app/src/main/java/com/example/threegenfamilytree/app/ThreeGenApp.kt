package com.example.threegenfamilytree.app

import android.app.Application
import com.example.threegenfamilytree.data.ThreeGenDatabase
import com.example.threegenfamilytree.repository.ThreeGenRepository

/**
 * Application class to initialize Room database and repository.
 */
class ThreeGenApp : Application() {

    // Lazy initialization of database
    val database: ThreeGenDatabase by lazy { ThreeGenDatabase.getDatabase(this) }

    // Initialize repository with DAO from database
    val repository: ThreeGenRepository by lazy { ThreeGenRepository(database.threeGenDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
