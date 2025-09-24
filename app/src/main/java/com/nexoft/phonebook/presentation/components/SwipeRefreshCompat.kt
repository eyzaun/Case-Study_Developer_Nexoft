package com.nexoft.phonebook.presentation.components

import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SwipeRefreshCompat(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeRefreshState(isRefreshing = refreshing)
    SwipeRefresh(state = state, onRefresh = onRefresh) {
        content()
    }
}
