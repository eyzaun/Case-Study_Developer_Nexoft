package com.nexoft.phonebook.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.repository.ContactRepository
import com.nexoft.phonebook.domain.usecase.DeleteContactUseCase
import com.nexoft.phonebook.domain.usecase.SaveToDeviceContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val contact: Contact? = null,
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showEditMenu: Boolean = false,
    val deleteSuccess: Boolean = false,
    val savedToDevice: Boolean = false,
    val toastMessage: String? = null,
    val error: String? = null,
    val dominantColor: androidx.compose.ui.graphics.Color? = null
)

sealed class ProfileEvent {
    object LoadContact : ProfileEvent()
    object OnEditMenuClick : ProfileEvent()
    object OnEditMenuDismiss : ProfileEvent()
    object OnEditClick : ProfileEvent()
    object OnDeleteClick : ProfileEvent()
    object OnDeleteConfirm : ProfileEvent()
    object OnDeleteDismiss : ProfileEvent()
    object OnSaveToDeviceClick : ProfileEvent()
    object OnBackClick : ProfileEvent()
    object ClearToast : ProfileEvent()
    object ClearError : ProfileEvent()
    data class OnDominantColorExtracted(val color: androidx.compose.ui.graphics.Color) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val saveToDeviceContactsUseCase: SaveToDeviceContactsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId: String = checkNotNull(savedStateHandle["contactId"])

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadContact()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadContact -> loadContact()
            is ProfileEvent.OnEditMenuClick -> showEditMenu()
            is ProfileEvent.OnEditMenuDismiss -> hideEditMenu()
            is ProfileEvent.OnEditClick -> { /* Handled by navigation */ }
            is ProfileEvent.OnDeleteClick -> showDeleteDialog()
            is ProfileEvent.OnDeleteConfirm -> deleteContact()
            is ProfileEvent.OnDeleteDismiss -> hideDeleteDialog()
            is ProfileEvent.OnSaveToDeviceClick -> saveToDevice()
            is ProfileEvent.OnBackClick -> { /* Handled by navigation */ }
            is ProfileEvent.ClearToast -> clearToast()
            is ProfileEvent.ClearError -> clearError()
            is ProfileEvent.OnDominantColorExtracted -> updateDominantColor(event.color)
        }
    }

    private fun loadContact() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // First, sync local cache with current device contacts to ensure flags are up-to-date
            runCatching { repository.syncWithDevice() }

            repository.getContact(contactId).fold(
                onSuccess = { contact ->
                    _state.update { current ->
                        val effective = if (current.savedToDevice) {
                            contact.copy(isInDeviceContacts = true)
                        } else contact
                        current.copy(
                            contact = effective,
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

    private fun showEditMenu() {
        _state.update { it.copy(showEditMenu = true) }
    }

    private fun hideEditMenu() {
        _state.update { it.copy(showEditMenu = false) }
    }

    private fun showDeleteDialog() {
        _state.update {
            it.copy(
                showDeleteDialog = true,
                showEditMenu = false
            )
        }
    }

    private fun hideDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }

    private fun deleteContact() {
        viewModelScope.launch {
            _state.value.contact?.let { contact ->
                deleteContactUseCase(contact.id).fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                showDeleteDialog = false,
                                deleteSuccess = true,
                                toastMessage = "${contact.fullName} deleted"
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                showDeleteDialog = false,
                                error = exception.message
                            )
                        }
                    }
                )
            }
        }
    }

    private fun saveToDevice() {
        viewModelScope.launch {
            _state.value.contact?.let { contact ->
                // Guard: if already saved, do nothing
                if (contact.isInDeviceContacts || _state.value.savedToDevice) {
                    // Optionally, surface the informational toast again
                    _state.update {
                        it.copy(
                            toastMessage = null // no new toast; UI already shows disabled state + info row
                        )
                    }
                    return@launch
                }
                saveToDeviceContactsUseCase(contact).fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                savedToDevice = true,
                                toastMessage = "${contact.fullName} saved to device",
                                contact = it.contact?.copy(isInDeviceContacts = true)
                            )
                        }
                        // No immediate reload; local state already reflects saved status.
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(error = exception.message)
                        }
                    }
                )
            }
        }
    }

    private fun updateDominantColor(color: androidx.compose.ui.graphics.Color) {
        _state.update { it.copy(dominantColor = color) }
    }

    private fun clearToast() {
        _state.update { it.copy(toastMessage = null) }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}