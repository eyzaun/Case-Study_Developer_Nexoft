package com.nexoft.phonebook.data.local.dao

import androidx.room.*
import com.nexoft.phonebook.data.local.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentSearches(): List<SearchHistoryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)
    
    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearch(query: String)
    
    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
    
    @Query("SELECT DISTINCT query FROM search_history ORDER BY timestamp DESC LIMIT 10")
    suspend fun getUniqueSearchQueries(): List<String>
}
