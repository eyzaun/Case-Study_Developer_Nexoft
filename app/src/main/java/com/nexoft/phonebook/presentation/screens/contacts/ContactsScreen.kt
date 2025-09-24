package com.nexoft.phonebook.presentation.screens.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexoft.phonebook.presentation.components.SwipeRefreshCompat
import com.nexoft.phonebook.presentation.components.*
import com.nexoft.phonebook.presentation.viewmodel.ContactsEvent
import com.nexoft.phonebook.presentation.viewmodel.ContactsViewModel
import com.nexoft.phonebook.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onNavigateToAddContact: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToEditContact: (String) -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show toast messages
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                viewModel.onEvent(ContactsEvent.ClearToast)
            }
        }
    }

    // Show error messages
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
                viewModel.onEvent(ContactsEvent.ClearError)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contacts",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Gray900
                ),
                modifier = Modifier.background(White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddContact,
                containerColor = Green500,
                contentColor = White,
                modifier = Modifier.size(Dimens.fabSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Contact"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(White)
        ) {
            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(ContactsEvent.OnSearchQueryChange(it)) },
                searchHistory = state.searchHistory,
                onSearchClick = { viewModel.onEvent(ContactsEvent.OnSearchClick) },
                onSearchDismiss = { viewModel.onEvent(ContactsEvent.OnSearchDismiss) },
                onHistoryItemClick = { viewModel.onEvent(ContactsEvent.OnSearchHistoryClick(it)) },
                isActive = state.isSearchActive,
                modifier = Modifier.padding(
                    horizontal = Dimens.paddingMedium,
                    vertical = Dimens.paddingSmall
                )
            )

            // Content
            SwipeRefreshCompat(
                refreshing = state.isRefreshing,
                onRefresh = { viewModel.onEvent(ContactsEvent.RefreshContacts) }
            ) {
                when {
                    state.isLoading && state.contacts.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Green500)
                        }
                    }
                    state.contacts.isEmpty() -> {
                        EmptyState(
                            type = if (state.searchQuery.isNotEmpty()) {
                                EmptyStateType.NO_SEARCH_RESULTS
                            } else {
                                EmptyStateType.NO_CONTACTS
                            }
                        )
                    }
                    else -> {
                        ContactsList(
                            groupedContacts = state.contacts,
                            onContactClick = { contact ->
                                onNavigateToProfile(contact.id)
                            },
                            onEditClick = { contact ->
                                onNavigateToEditContact(contact.id)
                            },
                            onDeleteClick = { contact ->
                                viewModel.onEvent(ContactsEvent.DeleteContact(contact))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactsList(
    groupedContacts: Map<Char, List<com.nexoft.phonebook.domain.model.Contact>>,
    onContactClick: (com.nexoft.phonebook.domain.model.Contact) -> Unit,
    onEditClick: (com.nexoft.phonebook.domain.model.Contact) -> Unit,
    onDeleteClick: (com.nexoft.phonebook.domain.model.Contact) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dimens.paddingXXLarge)
    ) {
        groupedContacts.forEach { (letter, contacts) ->
            item(key = letter) {
                GroupHeader(letter = letter)
            }

            items(
                items = contacts,
                key = { it.id }
            ) { contact ->
                ContactListItem(
                    contact = contact,
                    onClick = { onContactClick(contact) },
                    onEditClick = { onEditClick(contact) },
                    onDeleteClick = { onDeleteClick(contact) }
                )
            }
        }
    }
}