package com.nexoft.phonebook.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nexoft.phonebook.ui.theme.*

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchHistory: List<String>,
    onSearchClick: () -> Unit,
    onSearchDismiss: () -> Unit,
    onHistoryItemClick: (String) -> Unit,
    onClearAll: () -> Unit,
    onSearchConfirm: () -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.searchBarHeight)
                .background(
                    color = SearchBarGray,
                    shape = com.nexoft.phonebook.ui.theme.Shapes.searchBarShape
                )
                .clickable {
                    onSearchClick()
                    focusRequester.requestFocus()
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(Dimens.iconSizeMedium)
                )

                Spacer(modifier = Modifier.width(Dimens.paddingSmall))

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            isFocused = it.isFocused
                            if (it.isFocused && !isActive) {
                                onSearchClick()
                            }
                        },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Gray900
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onSearchConfirm()
                        }
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(Blue500),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = androidx.compose.ui.res.stringResource(id = com.nexoft.phonebook.R.string.search_placeholder),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Gray500
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                            if (!isActive) {
                                onSearchDismiss()
                            }
                        },
                        modifier = Modifier.size(Dimens.iconSizeMedium)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = Gray500
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = isActive && searchHistory.isNotEmpty() && query.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.paddingXSmall),
                shape = RoundedCornerShape(Dimens.radiusMedium),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevationSmall)
            ) {
                // Header row: SEARCH HISTORY  |  Clear All
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.paddingMedium, vertical = Dimens.paddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SEARCH HISTORY",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.bodySmall,
                        color = Blue500,
                        modifier = Modifier.clickable { onClearAll() }
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = Dimens.paddingSmall)
                ) {
                    items(searchHistory.take(5)) { item ->
                        SearchHistoryItem(
                            query = item,
                            onClick = { onHistoryItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHistoryItem(
    query: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = Dimens.paddingMedium,
                vertical = Dimens.paddingSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = Gray500,
            modifier = Modifier.size(Dimens.iconSizeSmall)
        )
        Spacer(modifier = Modifier.width(Dimens.paddingSmall))
        Text(
            text = query,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray700
        )
    }
}