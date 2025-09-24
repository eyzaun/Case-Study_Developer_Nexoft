package com.nexoft.phonebook.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Contacts : Screen("contacts")
    object AddContact : Screen("add_contact/new")
    object EditContact : Screen("add_contact/{contactId}") {
        fun createRoute(contactId: String) = "add_contact/$contactId"
    }
    object Profile : Screen("profile/{contactId}") {
        fun createRoute(contactId: String) = "profile/$contactId"
    }
}

@Composable
fun PhoneBookNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Contacts.route
    ) {
        composable(route = Screen.Contacts.route) {
            // ContactsScreen will be implemented
        }

        composable(
            route = "add_contact/{contactId}",
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.StringType
                }
            )
        ) {
            // AddEditContactScreen will be implemented
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.StringType
                }
            )
        ) {
            // ProfileScreen will be implemented
        }
    }
}