package com.nexoft.phonebook.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
import androidx.compose.ui.res.stringResource
import com.nexoft.phonebook.utils.PermissionHelper
import com.nexoft.phonebook.utils.PhoneNumberFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    onNavigateBack: (String?) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val contact = state.contact
    val errorMessage = state.error
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var bottomToastMessage by remember { mutableStateOf<String?>(null) }
    var showBottomToast by remember { mutableStateOf(false) }

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
            onNavigateBack("User is deleted!")
        }
    }

    // Show bottom toast messages
    val addedToPhoneText = stringResource(id = com.nexoft.phonebook.R.string.toast_added_to_phone)
    LaunchedEffect(state.toastMessage, state.savedToDevice) {
        state.toastMessage?.let { message ->
            bottomToastMessage = if (state.savedToDevice) addedToPhoneText else message
            showBottomToast = true
            viewModel.onEvent(ProfileEvent.ClearToast)
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
                    CircularProgressIndicator(color = Blue500)
                }
            }
            contact != null -> {
                ProfileContent(
                    contact = contact,
                    dominantColor = state.dominantColor,
                    onSaveToDeviceClick = {
                        if (contactPermissions.allPermissionsGranted) {
                            viewModel.onEvent(ProfileEvent.OnSaveToDeviceClick)
                        } else {
                            contactPermissions.launchMultiplePermissionRequest()
                        }
                    },
                    onChangePhotoClick = { state.contact?.let { onNavigateToEdit(it.id) } },
                    savedToDevice = state.savedToDevice,
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

        // Bottom toast overlay
        com.nexoft.phonebook.presentation.components.BottomToast(
            message = bottomToastMessage ?: "",
            visible = showBottomToast && !bottomToastMessage.isNullOrBlank(),
            onDismiss = { showBottomToast = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    onBackClick: (String?) -> Unit,
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMenuDismiss: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
                IconButton(onClick = { onBackClick(null) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
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
                                    tint = Blue500,
                                    modifier = Modifier.size(Dimens.iconSizeSmall)
                                )
                                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                                Text(stringResource(id = com.nexoft.phonebook.R.string.edit), color = Gray900)
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
                                Text(stringResource(id = com.nexoft.phonebook.R.string.delete), color = RedDelete)
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
    onChangePhotoClick: (() -> Unit)? = null,
    savedToDevice: Boolean = false,
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

        // Profile Image with Shadow and small '+' overlay
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

            // '+' overlay removed per request
        }

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        // Change Photo link
        Text(
            text = stringResource(id = com.nexoft.phonebook.R.string.change_photo),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Blue500,
            modifier = Modifier
                .clickable(enabled = onChangePhotoClick != null) { onChangePhotoClick?.invoke() }
                .padding(vertical = Dimens.paddingXSmall)
        )

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        // Read-only fields styled like inputs
        ReadonlyField(
            value = contact.firstName,
            placeholder = stringResource(id = com.nexoft.phonebook.R.string.first_name),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.paddingSmall))

        ReadonlyField(
            value = contact.lastName,
            placeholder = stringResource(id = com.nexoft.phonebook.R.string.last_name),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.paddingSmall))

        ReadonlyField(
            value = PhoneNumberFormatter.formatPhoneNumber(contact.phoneNumber),
            placeholder = stringResource(id = com.nexoft.phonebook.R.string.phone_number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.paddingXLarge))

        // Save to Device Button
        val alreadySaved = contact.isInDeviceContacts || savedToDevice
        if (!alreadySaved) {
            // Enabled capsule button
            Surface(
                onClick = onSaveToDeviceClick,
                shape = RoundedCornerShape(100),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                color = White,
                border = BorderStroke(Dimens.borderThin, Gray300),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = Gray700
                    )
                    Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                    Text(
                        text = stringResource(id = com.nexoft.phonebook.R.string.save_to_phone),
                        color = Gray700,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            // Disabled capsule button + info row
            Surface(
                enabled = false,
                onClick = {},
                shape = RoundedCornerShape(100),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                color = Gray100,
                border = BorderStroke(Dimens.borderThin, Gray200),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = Gray500
                    )
                    Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                    Text(
                        text = stringResource(id = com.nexoft.phonebook.R.string.save_to_phone),
                        color = Gray500,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Gray600,
                    modifier = Modifier.size(Dimens.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text(
                    text = stringResource(id = com.nexoft.phonebook.R.string.saved_in_device),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray700
                )
            }
        }
    }
}

@Composable
private fun ReadonlyField(
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(Dimens.radiusLarge),
        color = White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(Dimens.borderThin, Gray200),
        modifier = modifier
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (value.isNotBlank()) value else placeholder,
                color = if (value.isNotBlank()) Gray900 else Gray400,
                style = MaterialTheme.typography.bodyLarge
            )
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

            Text(text = stringResource(id = com.nexoft.phonebook.R.string.confirm_delete_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))

            Text(text = stringResource(id = com.nexoft.phonebook.R.string.confirm_delete_message), style = MaterialTheme.typography.bodyLarge, color = Gray700, textAlign = TextAlign.Center)

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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedDelete
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = com.nexoft.phonebook.R.string.yes))
                }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingLarge))
        }
    }
}