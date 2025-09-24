package com.nexoft.phonebook.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.nexoft.phonebook.presentation.viewmodel.ProfileEvent
import com.nexoft.phonebook.presentation.viewmodel.ProfileViewModel
import com.nexoft.phonebook.ui.theme.*
import com.nexoft.phonebook.utils.ColorExtractor
import com.nexoft.phonebook.utils.DeviceContactsHelper
import com.nexoft.phonebook.utils.shadowWithColor
import androidx.compose.ui.unit.dp
import com.nexoft.phonebook.utils.PermissionHelper
import com.nexoft.phonebook.utils.PhoneNumberFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val contact = state.contact
    val errorMessage = state.error
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val contactPermissions = rememberMultiplePermissionsState(
        permissions = PermissionHelper.CONTACT_PERMISSIONS.toList()
    )

    // Extract dominant color from image
    LaunchedEffect(contact?.profileImageUrl) {
        contact?.profileImageUrl?.let { imageUrl ->
            val dominantColor = ColorExtractor.extractDominantColor(context, imageUrl)
            viewModel.onEvent(ProfileEvent.OnDominantColorExtracted(dominantColor))
        }
    }

    // Handle delete success
    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            onNavigateBack()
        }
    }

    // Show toast messages
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                    actionLabel = if (state.savedToDevice) "OK" else null
                )
                viewModel.onEvent(ProfileEvent.ClearToast)
            }
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(
                onBackClick = onNavigateBack,
                onMenuClick = { viewModel.onEvent(ProfileEvent.OnEditMenuClick) },
                showMenu = state.showEditMenu,
                onEditClick = {
                    state.contact?.let { onNavigateToEdit(it.id) }
                    viewModel.onEvent(ProfileEvent.OnEditMenuDismiss)
                },
                onDeleteClick = {
                    viewModel.onEvent(ProfileEvent.OnDeleteClick)
                },
                onMenuDismiss = {
                    viewModel.onEvent(ProfileEvent.OnEditMenuDismiss)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = White
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green500)
                }
            }
            contact != null -> {
                ProfileContent(
                    contact = contact,
                    dominantColor = state.dominantColor,
                    onSaveToDeviceClick = {
                        if (contactPermissions.allPermissionsGranted) {
                            scope.launch {
                                DeviceContactsHelper.saveContactToDevice(context, contact)
                                viewModel.onEvent(ProfileEvent.OnSaveToDeviceClick)
                            }
                        } else {
                            contactPermissions.launchMultiplePermissionRequest()
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = RedDelete
                    )
                }
            }
        }

        // Delete Confirmation Bottom Sheet
        if (state.showDeleteDialog) {
            DeleteConfirmationBottomSheet(
                contactName = contact?.fullName ?: "",
                onConfirm = { viewModel.onEvent(ProfileEvent.OnDeleteConfirm) },
                onDismiss = { viewModel.onEvent(ProfileEvent.OnDeleteDismiss) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMenuDismiss: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        title = {},
        actions = {
            Box {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onMenuDismiss
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Green500,
                                    modifier = Modifier.size(Dimens.iconSizeSmall)
                                )
                                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                                Text("Edit", color = Gray900)
                            }
                        },
                        onClick = onEditClick
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = RedDelete,
                                    modifier = Modifier.size(Dimens.iconSizeSmall)
                                )
                                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                                Text("Delete", color = RedDelete)
                            }
                        },
                        onClick = onDeleteClick
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White,
            navigationIconContentColor = Gray900,
            actionIconContentColor = Gray900
        )
    )
}

@Composable
private fun ProfileContent(
    contact: com.nexoft.phonebook.domain.model.Contact,
    dominantColor: Color?,
    onSaveToDeviceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        // Profile Image with Shadow
        Box(
            modifier = Modifier
                .size(Dimens.avatarSizeLarge)
                .then(
                    if (dominantColor != null && contact.profileImageUrl != null) {
                        Modifier.shadowWithColor(
                            color = dominantColor,
                            alpha = 0.4f,
                            borderRadius = Dimens.radiusCircle,
                            shadowRadius = 20.dp,
                            offsetY = 10.dp
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(CircleShape)
                .background(if (contact.profileImageUrl != null) Color.Transparent else Gray300),
            contentAlignment = Alignment.Center
        ) {
            if (contact.profileImageUrl != null) {
                AsyncImage(
                    model = contact.profileImageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = contact.initials,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Gray700
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        // Contact Name
        Text(
            text = contact.fullName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = Gray900
        )

        Spacer(modifier = Modifier.height(Dimens.paddingSmall))

        // Phone Number
        Text(
            text = PhoneNumberFormatter.formatPhoneNumber(contact.phoneNumber),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = Gray700
        )

        Spacer(modifier = Modifier.height(Dimens.paddingXLarge))

        // Save to Device Button
        if (!contact.isInDeviceContacts) {
            OutlinedButton(
                onClick = onSaveToDeviceClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Green500
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Green500)
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Default.ContactPhone,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text(text = "Save to Phone")
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(Dimens.paddingMedium)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Green500,
                    modifier = Modifier.size(Dimens.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text(
                    text = "Saved in device contacts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Green500
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteConfirmationBottomSheet(
    contactName: String,
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
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = RedDelete,
                modifier = Modifier.size(Dimens.iconSizeLarge)
            )

            Spacer(modifier = Modifier.height(Dimens.paddingMedium))

            Text(
                text = "Delete Contact",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))

            Text(
                text = "Are you sure you want to delete $contactName?",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray700,
                textAlign = TextAlign.Center
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
                    Text("Cancel")
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedDelete
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingLarge))
        }
    }
}