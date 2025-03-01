package com.example.threegenfamilytree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.threegenfamilytree.viewmodel.ThreeGenViewModel
import kotlinx.coroutines.launch
import com.example.threegenfamilytree.data.ThreeGen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(viewModel: ThreeGenViewModel, navController: NavHostController, memberId: String) {
    val coroutineScope = rememberCoroutineScope()
    var member by remember { mutableStateOf<ThreeGen?>(null) }

    LaunchedEffect(memberId) {
        coroutineScope.launch {
            viewModel.getMemberById(memberId).collect { fetchedMember ->
                member = fetchedMember
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Member Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            member?.let {
                Text(text = "Name: ${it.firstName} ${it.middleName} ${it.lastName}")
                Text(text = "Town: ${it.town}")
            } ?: Text(text = "Loading...")
        }
    }
}
