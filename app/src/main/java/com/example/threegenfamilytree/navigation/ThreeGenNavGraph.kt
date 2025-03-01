package com.example.threegenfamilytree.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.threegenfamilytree.ui.screens.AddMemberScreen
import com.example.threegenfamilytree.ui.screens.FamilyTreeScreen
import com.example.threegenfamilytree.ui.screens.MemberDetailScreen
import com.example.threegenfamilytree.viewmodel.ThreeGenViewModel

sealed class Screen(val route: String) {
    object FamilyTree : Screen("family_tree")
    object MemberDetail : Screen("member_detail/{memberId}") {
        fun createRoute(memberId: String) = "member_detail/$memberId"
    }
    object AddMember : Screen("add_member") // <-- Add this
}

@Composable
fun ThreeGenNavGraph(navController: NavHostController, viewModel: ThreeGenViewModel) {
    NavHost(navController = navController, startDestination = Screen.FamilyTree.route) {
        composable(Screen.FamilyTree.route) {
            FamilyTreeScreen(navController, viewModel)
        }
        composable(Screen.MemberDetail.route) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId") ?: return@composable
            MemberDetailScreen(viewModel, navController, memberId)
        }
        composable(Screen.AddMember.route) { // <-- Add this
            AddMemberScreen(navController, viewModel)
        }
    }
}