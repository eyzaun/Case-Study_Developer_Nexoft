package com.nexoft.phonebook.presentation.screens.addcontact

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.nexoft.phonebook.R
import com.nexoft.phonebook.presentation.viewmodel.AddEditContactEvent
import com.nexoft.phonebook.presentation.viewmodel.AddEditContactViewModel
import com.nexoft.phonebook.ui.theme.*
import com.nexoft.phonebook.utils.ImageHelper
import com.nexoft.phonebook.utils.PermissionHelper
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditContactScreen(
    onNavigateBack: (String) -> Unit,
    viewModel: AddEditContactViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val addedText = stringResource(id = R.string.toast_added)
    val updatedText = stringResource(id = R.string.toast_updated)

    val cameraPermissions = rememberMultiplePermissionsState(
        permissions = PermissionHelper.CAMERA_PERMISSION.toList()
    )

    val storagePermissions = rememberMultiplePermissionsState(
        permissions = PermissionHelper.STORAGE_PERMISSION.toList()
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                val file = ImageHelper.compressImage(context, it)
                file?.let { imageFile ->
                    viewModel.onEvent(AddEditContactEvent.OnImageSelected(imageFile))
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            scope.launch {
                val file = ImageHelper.createTempImageFile(context)
                val saved = ImageHelper.saveBitmapToFile(it, file)
                if (saved) {
                    viewModel.onEvent(AddEditContactEvent.OnImageSelected(file))
                }
            }
        }
    }

    // Handle save success -> briefly show toast and then navigate back
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            val message = if (state.isEditMode) updatedText else addedText
            onNavigateBack(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (state.isEditMode) stringResource(id = R.string.edit_contact) else stringResource(id = R.string.new_contact), style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Gray900
                )
            )
        },
        floatingActionButton = {
            if (canSave(state)) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(AddEditContactEvent.OnSaveClick) },
                    containerColor = Green500,
                    contentColor = White
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            }
        },
        containerColor = White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.paddingMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(Dimens.avatarSizeLarge)
                        .clip(CircleShape)
                        .background(Gray200)
                        .clickable { viewModel.onEvent(AddEditContactEvent.OnImagePickerClick) },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        state.profileImageFile != null -> {
                            AsyncImage(
                                model = state.profileImageFile,
                                contentDescription = stringResource(id = R.string.select_photo),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        state.profileImageUrl != null -> {
                            AsyncImage(
                                model = state.profileImageUrl,
                                contentDescription = stringResource(id = R.string.select_photo),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = stringResource(id = R.string.select_photo),
                                tint = Gray500,
                                modifier = Modifier.size(Dimens.iconSizeLarge)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.paddingXLarge))

                // First Name Field
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = { viewModel.onEvent(AddEditContactEvent.OnFirstNameChange(it)) },
                    label = { Text(stringResource(id = R.string.first_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Gray500
                        )
                    },
                    isError = state.firstNameError != null,
                    supportingText = state.firstNameError?.let { { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green500,
                        unfocusedBorderColor = Gray300,
                        errorBorderColor = RedDelete
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.paddingMedium))

                // Last Name Field
                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = { viewModel.onEvent(AddEditContactEvent.OnLastNameChange(it)) },
                    label = { Text(stringResource(id = R.string.last_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Gray500
                        )
                    },
                    isError = state.lastNameError != null,
                    supportingText = state.lastNameError?.let { { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green500,
                        unfocusedBorderColor = Gray300,
                        errorBorderColor = RedDelete
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.paddingMedium))

                // Phone Number Field
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = { viewModel.onEvent(AddEditContactEvent.OnPhoneNumberChange(it)) },
                    label = { Text(stringResource(id = R.string.phone_number)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Gray500
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = state.phoneNumberError != null,
                    supportingText = state.phoneNumberError?.let { { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green500,
                        unfocusedBorderColor = Gray300,
                        errorBorderColor = RedDelete
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Image Picker Bottom Sheet
            if (state.showImagePicker) {
                ImagePickerBottomSheet(
                    onCameraClick = {
                        if (cameraPermissions.allPermissionsGranted) {
                            cameraLauncher.launch()
                            viewModel.onEvent(AddEditContactEvent.OnImagePickerDismiss)
                        } else {
                            cameraPermissions.launchMultiplePermissionRequest()
                        }
                    },
                    onGalleryClick = {
                        galleryLauncher.launch("image/*")
                        viewModel.onEvent(AddEditContactEvent.OnImagePickerDismiss)
                    },
                    onDismiss = { viewModel.onEvent(AddEditContactEvent.OnImagePickerDismiss) }
                )
            }

            // Removed inline Lottie dialog to avoid double animations.
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImagePickerBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
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
                .padding(Dimens.paddingMedium)
        ) {
            Text(
                text = stringResource(id = R.string.select_photo),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Dimens.paddingMedium)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCameraClick() }
                    .padding(vertical = Dimens.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Gray700
                )
                Spacer(modifier = Modifier.width(Dimens.paddingMedium))
                Text(text = stringResource(id = R.string.take_photo), style = MaterialTheme.typography.bodyLarge)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGalleryClick() }
                    .padding(vertical = Dimens.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = Gray700
                )
                Spacer(modifier = Modifier.width(Dimens.paddingMedium))
                Text(text = stringResource(id = R.string.choose_from_gallery), style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(Dimens.paddingLarge))
        }
    }
}

// Removed LottieAnimationDialog, success animation is shown on the dedicated screen now.

private fun canSave(state: com.nexoft.phonebook.presentation.viewmodel.AddEditContactState): Boolean {
    return state.firstName.isNotBlank() &&
            state.lastName.isNotBlank() &&
            state.phoneNumber.isNotBlank() &&
            !state.isSaving
}