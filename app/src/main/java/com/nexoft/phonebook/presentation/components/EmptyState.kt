package com.nexoft.phonebook.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.nexoft.phonebook.ui.theme.*

@Composable
fun EmptyState(
    type: EmptyStateType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.paddingXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = when (type) {
                EmptyStateType.NO_CONTACTS -> Icons.Outlined.PersonAdd
                EmptyStateType.NO_SEARCH_RESULTS -> Icons.Outlined.SearchOff
            },
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(Dimens.iconSizeXLarge)
        )

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        Text(
            text = when (type) {
                EmptyStateType.NO_CONTACTS -> androidx.compose.ui.res.stringResource(id = com.nexoft.phonebook.R.string.no_contacts)
                EmptyStateType.NO_SEARCH_RESULTS -> androidx.compose.ui.res.stringResource(id = com.nexoft.phonebook.R.string.no_results)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = Gray700,
            textAlign = TextAlign.Center
        )

        if (type == EmptyStateType.NO_CONTACTS) {
            Spacer(modifier = Modifier.height(Dimens.paddingSmall))
            Text(
                text = androidx.compose.ui.res.stringResource(id = com.nexoft.phonebook.R.string.no_contacts_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500,
                textAlign = TextAlign.Center
            )
        } else if (type == EmptyStateType.NO_SEARCH_RESULTS) {
            Spacer(modifier = Modifier.height(Dimens.paddingSmall))
            Text(
                text = androidx.compose.ui.res.stringResource(id = com.nexoft.phonebook.R.string.no_results_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500,
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class EmptyStateType {
    NO_CONTACTS,
    NO_SEARCH_RESULTS
}