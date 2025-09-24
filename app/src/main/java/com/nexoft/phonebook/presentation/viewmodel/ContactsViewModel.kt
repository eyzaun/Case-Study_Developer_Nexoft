package com.nexoft.phonebook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsState(
    val contacts: Map<Char, List<Contact>> = emptyMap(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchHistory: List<String> = emptyList(),
    val isSearchActive: Boolean = false,
    val error: String? = null,
    val toastMessage: String? = null,
    val isRefreshing: Boolean = false
)

sealed class ContactsEvent {
    object LoadContacts : ContactsEvent()
    object RefreshContacts : ContactsEvent()
    data class OnSearchQueryChange(val query: String) : ContactsEvent()
    object OnSearchClick : ContactsEvent()
    object OnSearchDismiss : ContactsEvent()
    data class OnSearchHistoryClick(val query: String) : ContactsEvent()
    data class DeleteContact(val contact: Contact) : ContactsEvent()
    data class OnContactClick(val contact: Contact) : ContactsEvent()
    object OnAddContactClick : ContactsEvent()
    object ClearError : ContactsEvent()
    object ClearToast : ContactsEvent()
}

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val searchContactsUseCase: SearchContactsUseCase,
    private val checkDeviceContactsUseCase: CheckDeviceContactsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ContactsState())
    val state: StateFlow<ContactsState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadContacts()
        checkDeviceContacts()
    }

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.LoadContacts -> loadContacts()
            is ContactsEvent.RefreshContacts -> refreshContacts()
            is ContactsEvent.OnSearchQueryChange -> onSearchQueryChange(event.query)
            is ContactsEvent.OnSearchClick -> onSearchClick()
            is ContactsEvent.OnSearchDismiss -> onSearchDismiss()
            is ContactsEvent.OnSearchHistoryClick -> onSearchHistoryClick(event.query)
            is ContactsEvent.DeleteContact -> deleteContact(event.contact)
            is ContactsEvent.OnContactClick -> { /* Handled by navigation */ }
            is ContactsEvent.OnAddContactClick -> { /* Handled by navigation */ }
            is ContactsEvent.ClearError -> clearError()
            is ContactsEvent.ClearToast -> clearToast()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getAllContactsUseCase().fold(
                onSuccess = { groupedContacts ->
                    _state.update {
                        it.copy(
                            contacts = groupedContacts,
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

    private fun refreshContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            getAllContactsUseCase().fold(
                onSuccess = { groupedContacts ->
                    _state.update {
                        it.copy(
                            contacts = groupedContacts,
                            isRefreshing = false
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = exception.message
                        )
                    }
                }
            )
        }
    }

    private fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                loadContacts()
            } else {
                delay(300) // Debounce
                searchContactsUseCase(query).collect { groupedContacts ->
                    _state.update { it.copy(contacts = groupedContacts) }
                }
            }
        }
    }

    private fun onSearchClick() {
        viewModelScope.launch {
            val searchHistory = searchContactsUseCase.getSearchHistory()
            _state.update {
                it.copy(
                    isSearchActive = true,
                    searchHistory = searchHistory
                )
            }
        }
    }

    private fun onSearchDismiss() {
        viewModelScope.launch {
            if (_state.value.searchQuery.isNotBlank()) {
                searchContactsUseCase.saveQuery(_state.value.searchQuery)
            }
            _state.update {
                it.copy(
                    isSearchActive = false,
                    searchQuery = "",
                    searchHistory = emptyList()
                )
            }
            loadContacts()
        }
    }

    private fun onSearchHistoryClick(query: String) {
        _state.update { it.copy(searchQuery = query) }
        onSearchQueryChange(query)
    }

    private fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            deleteContactUseCase(contact.id).fold(
                onSuccess = {
                    _state.update {
                        it.copy(toastMessage = "${contact.fullName} silindi")
                    }
                    loadContacts()
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(error = exception.message)
                    }
                }
            )
        }
    }

    private fun checkDeviceContacts() {
        viewModelScope.launch {
            checkDeviceContactsUseCase()
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun clearToast() {
        _state.update { it.copy(toastMessage = null) }
    }
}