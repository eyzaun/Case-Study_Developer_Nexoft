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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Delete
import com.nexoft.phonebook.presentation.components.SwipeRefreshCompat
import com.nexoft.phonebook.presentation.components.*
import com.nexoft.phonebook.presentation.viewmodel.ContactsEvent
import com.nexoft.phonebook.presentation.viewmodel.ContactsViewModel
import com.nexoft.phonebook.ui.theme.*
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onNavigateToAddContact: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToEditContact: (String) -> Unit,
    viewModel: ContactsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var bottomToastMessage by remember { mutableStateOf<String?>(null) }
    var showBottomToast by remember { mutableStateOf(false) }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val crossScreenToastFlow = remember(savedStateHandle) {
        savedStateHandle?.getStateFlow("toast_message", "")
    }
    val crossScreenToast = crossScreenToastFlow?.collectAsStateWithLifecycle(initialValue = "")?.value ?: ""
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh when screen resumes (returning from add/edit/profile)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(ContactsEvent.RefreshContacts)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Auto refresh on enter
    LaunchedEffect(Unit) {
        viewModel.onEvent(ContactsEvent.RefreshContacts)
    }

    // Show bottom toast messages (success like delete)
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            bottomToastMessage = message
            showBottomToast = true
            viewModel.onEvent(ContactsEvent.ClearToast)
        }
    }

    // Handle cross-screen toasts coming from Add/Edit or Profile
    LaunchedEffect(crossScreenToast) {
        if (crossScreenToast.isNotBlank()) {
            bottomToastMessage = crossScreenToast
            showBottomToast = true
            savedStateHandle?.set("toast_message", "")
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
                    Text(text = stringResource(id = com.nexoft.phonebook.R.string.contacts_title), style = MaterialTheme.typography.titleLarge)
                },
                actions = {
                    // Small circular '+' in top-right
                    androidx.compose.material3.FilledIconButton(
                        onClick = onNavigateToAddContact,
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Blue500,
                            contentColor = White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = com.nexoft.phonebook.R.string.add_contact))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Gray900
                ),
                modifier = Modifier.background(White)
            )
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
                onClearAll = { viewModel.onEvent(ContactsEvent.OnClearAllHistory) },
                onSearchConfirm = { viewModel.onEvent(ContactsEvent.OnSearchConfirm) },
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
                            CircularProgressIndicator(color = Blue500)
                        }
                    }
                    state.contacts.isEmpty() -> {
                        EmptyState(type = if (state.searchQuery.isNotEmpty()) EmptyStateType.NO_SEARCH_RESULTS else EmptyStateType.NO_CONTACTS)
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

        if (state.showDeleteConfirm) {
            DeleteConfirmSheet(
                onConfirm = { viewModel.onEvent(ContactsEvent.ConfirmDelete(true)) },
                onDismiss = { viewModel.onEvent(ContactsEvent.ConfirmDelete(false)) }
            )
        }

        // Bottom toast overlay
        com.nexoft.phonebook.presentation.components.BottomToast(
            message = bottomToastMessage ?: "",
            visible = showBottomToast && !bottomToastMessage.isNullOrBlank(),
            onDismiss = { showBottomToast = false }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteConfirmSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = com.nexoft.phonebook.ui.theme.Shapes.bottomSheetShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = com.nexoft.phonebook.R.string.confirm_delete_title),
                style = MaterialTheme.typography.titleLarge,
                color = Gray900
            )
            Spacer(modifier = Modifier.height(Dimens.paddingSmall))
            Text(
                text = stringResource(id = com.nexoft.phonebook.R.string.confirm_delete_message),
                style = MaterialTheme.typography.bodyLarge,
                color = Gray700,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.paddingLarge))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = com.nexoft.phonebook.R.string.no))
                }
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Gray900, contentColor = White),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = com.nexoft.phonebook.R.string.yes))
                }
            }
            Spacer(modifier = Modifier.height(Dimens.paddingLarge))
        }
    }
}