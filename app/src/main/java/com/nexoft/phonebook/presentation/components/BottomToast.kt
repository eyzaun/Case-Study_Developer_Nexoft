package com.nexoft.phonebook.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexoft.phonebook.ui.theme.Dimens
import com.nexoft.phonebook.ui.theme.Green500
import com.nexoft.phonebook.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun BottomToast(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000,
    bottomPadding: Dp = Dimens.paddingLarge
){
    // Auto-dismiss timer
    LaunchedEffect(visible, message) {
        if (visible) {
            delay(durationMillis)
            onDismiss()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
        ) {
            Surface(
                color = White,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .padding(horizontal = Dimens.paddingLarge, vertical = bottomPadding)
                    .shadow(16.dp, RoundedCornerShape(28.dp), clip = false)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = Dimens.paddingLarge, vertical = Dimens.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
                ) {
                    // Green circular icon
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Green500, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Green500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
