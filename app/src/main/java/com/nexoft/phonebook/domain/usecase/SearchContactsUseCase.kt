package com.nexoft.phonebook.domain.usecase

import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    operator fun invoke(query: String): Flow<Map<Char, List<Contact>>> {
        return repository.searchContacts(query).map { contacts ->
            contacts
                .sortedBy { it.fullName.uppercase() }
                .groupBy { contact ->
                    contact.fullName.firstOrNull()?.uppercaseChar() ?: '#'
                }
        }
    }

    suspend fun saveQuery(query: String) {
        if (query.isNotBlank()) {
            repository.saveSearchQuery(query)
        }
    }

    suspend fun getSearchHistory(): List<String> {
        return repository.getSearchHistory()
    }

    suspend fun clearAllHistory() {
        repository.clearSearchHistory()
    }
}