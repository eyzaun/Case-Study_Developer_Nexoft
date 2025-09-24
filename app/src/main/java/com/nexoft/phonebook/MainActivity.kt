package com.nexoft.phonebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.nexoft.phonebook.presentation.navigation.Screen
import com.nexoft.phonebook.presentation.screens.addcontact.AddEditContactScreen
import com.nexoft.phonebook.presentation.screens.contacts.ContactsScreen
import com.nexoft.phonebook.presentation.screens.profile.ProfileScreen
import com.nexoft.phonebook.ui.theme.PhoneBookTheme
import com.nexoft.phonebook.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhoneBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Contacts.route
                    ) {
                        composable(route = Screen.Contacts.route) {
                            ContactsScreen(
                                onNavigateToAddContact = {
                                    navController.navigate("add_contact/new")
                                },
                                onNavigateToProfile = { contactId ->
                                    navController.navigate(Screen.Profile.createRoute(contactId))
                                },
                                onNavigateToEditContact = { contactId ->
                                    navController.navigate(Screen.EditContact.createRoute(contactId))
                                }
                            )
                        }

                        composable(
                            route = "add_contact/{contactId}",
                            arguments = listOf(
                                navArgument("contactId") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            AddEditContactScreen(
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable(
                            route = Screen.Profile.route,
                            arguments = listOf(
                                navArgument("contactId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val contactId = backStackEntry.arguments?.getString("contactId") ?: ""
                            ProfileScreen(
                                onNavigateBack = {
                                    navController.navigateUp()
                                },
                                onNavigateToEdit = { id ->
                                    navController.navigate(Screen.EditContact.createRoute(id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}