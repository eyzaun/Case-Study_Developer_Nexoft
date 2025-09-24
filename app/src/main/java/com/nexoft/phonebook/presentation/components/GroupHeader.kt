package com.nexoft.phonebook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.nexoft.phonebook.ui.theme.*

@Composable
fun GroupHeader(
    letter: Char,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.groupHeaderHeight)
            .background(Gray100)
            .padding(horizontal = Dimens.paddingMedium),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Gray700
        )
    }
}