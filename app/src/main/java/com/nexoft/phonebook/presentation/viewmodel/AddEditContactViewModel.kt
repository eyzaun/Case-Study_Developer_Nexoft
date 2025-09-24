package com.nexoft.phonebook.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.usecase.AddContactUseCase
import com.nexoft.phonebook.domain.usecase.UpdateContactUseCase
import com.nexoft.phonebook.domain.usecase.UploadImageUseCase
import com.nexoft.phonebook.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class AddEditContactState(
    val contactId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String? = null,
    val profileImageFile: File? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val showLottieAnimation: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val showImagePicker: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null
)

sealed class AddEditContactEvent {
    data class OnFirstNameChange(val firstName: String) : AddEditContactEvent()
    data class OnLastNameChange(val lastName: String) : AddEditContactEvent()
    data class OnPhoneNumberChange(val phoneNumber: String) : AddEditContactEvent()
    data class OnImageSelected(val file: File) : AddEditContactEvent()
    object OnImagePickerClick : AddEditContactEvent()
    object OnImagePickerDismiss : AddEditContactEvent()
    object OnSaveClick : AddEditContactEvent()
    object OnBackClick : AddEditContactEvent()
    object ClearError : AddEditContactEvent()
}

@HiltViewModel
class AddEditContactViewModel @Inject constructor(
    private val addContactUseCase: AddContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val repository: ContactRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditContactState())
    val state: StateFlow<AddEditContactState> = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("contactId")?.let { contactId ->
            if (contactId != "new") {
                loadContact(contactId)
            }
        }
    }

    fun onEvent(event: AddEditContactEvent) {
        when (event) {
            is AddEditContactEvent.OnFirstNameChange -> onFirstNameChange(event.firstName)
            is AddEditContactEvent.OnLastNameChange -> onLastNameChange(event.lastName)
            is AddEditContactEvent.OnPhoneNumberChange -> onPhoneNumberChange(event.phoneNumber)
            is AddEditContactEvent.OnImageSelected -> onImageSelected(event.file)
            is AddEditContactEvent.OnImagePickerClick -> showImagePicker()
            is AddEditContactEvent.OnImagePickerDismiss -> hideImagePicker()
            is AddEditContactEvent.OnSaveClick -> saveContact()
            is AddEditContactEvent.OnBackClick -> { /* Handled by navigation */ }
            is AddEditContactEvent.ClearError -> clearError()
        }
    }

    private fun loadContact(contactId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.getContact(contactId).fold(
                onSuccess = { contact ->
                    _state.update {
                        it.copy(
                            contactId = contact.id,
                            firstName = contact.firstName,
                            lastName = contact.lastName,
                            phoneNumber = contact.phoneNumber,
                            profileImageUrl = contact.profileImageUrl,
                            isEditMode = true,
                            isLoading = false
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
            )
        }
    }

    private fun onFirstNameChange(firstName: String) {
        _state.update {
            it.copy(
                firstName = firstName,
                firstNameError = null
            )
        }
    }

    private fun onLastNameChange(lastName: String) {
        _state.update {
            it.copy(
                lastName = lastName,
                lastNameError = null
            )
        }
    }

    private fun onPhoneNumberChange(phoneNumber: String) {
        // Only allow digits
        val filtered = phoneNumber.filter { it.isDigit() }
        _state.update {
            it.copy(
                phoneNumber = filtered,
                phoneNumberError = null
            )
        }
    }

    private fun onImageSelected(file: File) {
        _state.update {
            it.copy(
                profileImageFile = file,
                showImagePicker = false
            )
        }
    }

    private fun showImagePicker() {
        _state.update { it.copy(showImagePicker = true) }
    }

    private fun hideImagePicker() {
        _state.update { it.copy(showImagePicker = false) }
    }

    private fun validateInput(): Boolean {
        var hasError = false

        if (_state.value.firstName.isBlank()) {
            _state.update { it.copy(firstNameError = "İsim zorunludur") }
            hasError = true
        }

        if (_state.value.lastName.isBlank()) {
            _state.update { it.copy(lastNameError = "Soyisim zorunludur") }
            hasError = true
        }

        if (_state.value.phoneNumber.isBlank()) {
            _state.update { it.copy(phoneNumberError = "Telefon numarası zorunludur") }
            hasError = true
        } else if (_state.value.phoneNumber.length < 10) {
            _state.update { it.copy(phoneNumberError = "Geçerli bir telefon numarası giriniz") }
            hasError = true
        }

        return !hasError
    }

    private fun saveContact() {
        if (!validateInput()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, showLottieAnimation = true) }

            // Upload image if selected
            val imageUrl = _state.value.profileImageFile?.let { file ->
                uploadImageUseCase(file).getOrNull()
            } ?: _state.value.profileImageUrl

            val result = if (_state.value.isEditMode) {
                val contact = Contact(
                    id = _state.value.contactId!!,
                    firstName = _state.value.firstName,
                    lastName = _state.value.lastName,
                    phoneNumber = _state.value.phoneNumber,
                    profileImageUrl = imageUrl
                )
                updateContactUseCase(contact)
            } else {
                addContactUseCase(
                    firstName = _state.value.firstName,
                    lastName = _state.value.lastName,
                    phoneNumber = _state.value.phoneNumber,
                    profileImageUrl = imageUrl
                )
            }

            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            showLottieAnimation = false,
                            saveSuccess = true
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            showLottieAnimation = false,
                            error = exception.message
                        )
                    }
                }
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}