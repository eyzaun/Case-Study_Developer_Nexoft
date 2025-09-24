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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddEditContactScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditContactViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    // Handle save success
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditMode) "Edit Contact" else "New Contact",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back"
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
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save"
                    )
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
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        state.profileImageUrl != null -> {
                            AsyncImage(
                                model = state.profileImageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Add Photo",
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
                    label = { Text("First Name") },
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
                    label = { Text("Last Name") },
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
                    label = { Text("Phone Number") },
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

            // Lottie Animation
            AnimatedVisibility(visible = state.showLottieAnimation) {
                LottieAnimationDialog()
            }
        }
    }
}

@Composable
private fun ImagePickerBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = Shapes.bottomSheetShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.paddingMedium)
        ) {
            Text(
                text = "Select Photo",
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
                    contentDescription = "Camera",
                    tint = Gray700
                )
                Spacer(modifier = Modifier.width(Dimens.paddingMedium))
                Text(
                    text = "Take Photo",
                    style = MaterialTheme.typography.bodyLarge
                )
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
                    contentDescription = "Gallery",
                    tint = Gray700
                )
                Spacer(modifier = Modifier.width(Dimens.paddingMedium))
                Text(
                    text = "Choose from Gallery",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(Dimens.paddingLarge))
        }
    }
}

@Composable
private fun LottieAnimationDialog() {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(White, shape = RoundedCornerShape(Dimens.radiusLarge)),
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.success_animation)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(150.dp)
            )
        }
    }
}

private fun canSave(state: com.nexoft.phonebook.presentation.viewmodel.AddEditContactState): Boolean {
    return state.firstName.isNotBlank() &&
            state.lastName.isNotBlank() &&
            state.phoneNumber.isNotBlank() &&
            !state.isSaving
}