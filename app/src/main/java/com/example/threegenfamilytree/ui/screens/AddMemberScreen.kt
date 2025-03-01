package com.example.threegenfamilytree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.threegenfamilytree.data.ThreeGen
import com.example.threegenfamilytree.viewmodel.ThreeGenViewModel
import java.util.UUID

@Composable
fun AddMemberScreen(navController: NavController, viewModel: ThreeGenViewModel) {
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var town by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Middle Name") }, // ✅ Fixed label
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = town,
            onValueChange = { town = it },
            label = { Text("Town") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Call ViewModel function with required parameters
                viewModel.addMember(
                    firstName,
                    middleName,
                    lastName,
                    town,
                    parentID = null, // Update with actual parent selection logic
                    spouseID = null  // Update with actual spouse selection logic
                )
                navController.popBackStack() // Navigate back
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Member")
        }
    }
}