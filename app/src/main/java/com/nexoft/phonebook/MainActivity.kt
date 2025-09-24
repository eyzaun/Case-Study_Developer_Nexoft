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
import com.nexoft.phonebook.presentation.screens.addcontact.AddSuccessScreen
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
                                },
                                navController = navController
                            )
                        }

                        composable(
                            route = "add_contact/{contactId}",
                            arguments = listOf(
                                navArgument("contactId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val contactIdArg = backStackEntry.arguments?.getString("contactId")
                            val isAddMode = contactIdArg == "new"
                            AddEditContactScreen(
                                onNavigateBack = { message ->
                                    // If message empty -> treat as cancel/back
                                    if (message.isBlank()) {
                                        navController.navigateUp()
                                    } else {
                                        if (isAddMode) {
                                            // For add success, go to success screen first (no bottom toast for add)
                                            navController.navigate("add_success") {
                                                launchSingleTop = true
                                            }
                                            // Do NOT set toast for add flow
                                        } else {
                                            // For edit success, set toast on Contacts entry, then pop back to it
                                            val contactsEntry = try {
                                                navController.getBackStackEntry(com.nexoft.phonebook.presentation.navigation.Screen.Contacts.route)
                                            } catch (_: Exception) { null }
                                            contactsEntry?.savedStateHandle?.set("toast_message", message)
                                            // Pop back stack to Contacts (removes Edit and Profile if present)
                                            navController.popBackStack(route = com.nexoft.phonebook.presentation.navigation.Screen.Contacts.route, inclusive = false)
                                        }
                                    }
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
                                onNavigateBack = { message ->
                                    if (!message.isNullOrBlank()) {
                                        navController.previousBackStackEntry?.savedStateHandle?.set("toast_message", message)
                                    }
                                    navController.navigateUp()
                                },
                                onNavigateToEdit = { id ->
                                    navController.navigate(Screen.EditContact.createRoute(id))
                                }
                            )
                        }

                        // Add success full-screen confirmation
                        composable(route = "add_success") {
                            AddSuccessScreen(
                                onFinish = {
                                    // Pop success and add screen, land on contacts
                                    navController.popBackStack()
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}