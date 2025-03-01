package com.example.threegenfamilytree.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.threegenfamilytree.data.ThreeGen
import com.example.threegenfamilytree.navigation.Screen
import com.example.threegenfamilytree.viewmodel.ThreeGenViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun FamilyTreeScreen(navController: NavController, viewModel: ThreeGenViewModel) {
    val members by viewModel.allMembersLiveData.observeAsState(emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddMember.route) },
                modifier = Modifier.padding(16.dp),
                containerColor = Color.Blue,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Family Tree", style = MaterialTheme.typography.headlineMedium)

            if (members.isEmpty()) {
                Text("No members added yet", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(members) { member ->
                        FamilyTreeNode(member, navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyTreeNode(member: ThreeGen, navController: NavController, viewModel: ThreeGenViewModel) {
    var selectedImageUri by remember { mutableStateOf(member.imageUri ?: "") }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it.toString()
            Log.d("ImagePicker", "Selected URI: $it")
            viewModel.uploadImageAndSaveUri(context, member, it) // Upload and update DB
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Expand or navigate */ },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = selectedImageUri.ifEmpty { "https://via.placeholder.com/50" }
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { launcher.launch("image/*") } // Tap to pick an image
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "${member.firstName} ${member.lastName}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Town: ${member.town}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Child Number: ${member.childNumber}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
