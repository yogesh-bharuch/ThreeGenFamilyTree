package com.example.threegenfamilytree

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.threegenfamilytree.data.ThreeGenDatabase
import com.example.threegenfamilytree.navigation.ThreeGenNavGraph
import com.example.threegenfamilytree.repository.ThreeGenFirestoreRepository
import com.example.threegenfamilytree.repository.ThreeGenRepository
import com.example.threegenfamilytree.ui.theme.ThreeGenFamilyTreeTheme
import com.example.threegenfamilytree.viewmodel.ThreeGenViewModel
import com.example.threegenfamilytree.viewmodel.ThreeGenViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the Room database
        val database = ThreeGenDatabase.getDatabase(application)
        val localRepository = ThreeGenRepository(database.threeGenDao()) // Now 'database' is resolved
        val firestoreRepository = ThreeGenFirestoreRepository() // Initialize Firestore repository

        // Create ViewModel using ViewModelProvider (NOT viewModel())
        val viewModel = ViewModelProvider(
            this,
            ThreeGenViewModelFactory(application, localRepository, firestoreRepository)
        )[ThreeGenViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            ThreeGenFamilyTreeTheme {
                val navController = rememberNavController()
                ThreeGenNavGraph(navController, viewModel)
            }
        }
    }
}
