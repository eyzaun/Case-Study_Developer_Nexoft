package com.nexoft.phonebook.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.ui.theme.*
import com.nexoft.phonebook.utils.PhoneNumberFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Left-swipe to reveal Edit and Delete actions; tap to execute instead of auto-dismiss.
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val actionButtonWidth = 80.dp
    val maxRevealPx = with(density) { (actionButtonWidth * 2).toPx() }
    var revealPx by remember { mutableFloatStateOf(0f) } // 0..maxRevealPx

    val draggableState = rememberDraggableState { delta ->
        // Dragging left gives negative delta; increase reveal with -delta
        val newValue = (revealPx + (-delta)).coerceIn(0f, maxRevealPx)
        revealPx = newValue
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.listItemHeight)
    ) {
        // Actions background (revealed on left swipe)
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                background = Blue500,
                icon = Icons.Default.Edit,
                contentDescription = "Edit",
                buttonWidth = actionButtonWidth
            ) {
                // Trigger edit and close
                onEditClick()
                scope.launch { revealPx = 0f }
            }
            ActionButton(
                background = RedDelete,
                icon = Icons.Default.Delete,
                contentDescription = "Delete",
                buttonWidth = actionButtonWidth
            ) {
                onDeleteClick()
                scope.launch { revealPx = 0f }
            }
        }

        // Foreground content that slides left to reveal actions
        Box(
            modifier = Modifier
                .offset { IntOffset(-revealPx.toInt(), 0) }
                .fillMaxSize()
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        // Settle either open (full reveal) or closed
                        val threshold = maxRevealPx * 0.35f
                        val target = if (revealPx > threshold) maxRevealPx else 0f
                        scope.launch { revealPx = target }
                    }
                )
        ) {
            ContactContent(
                contact = contact,
                onClick = {
                    if (revealPx == 0f) {
                        onClick()
                    } else {
                        // Close if currently revealed
                        scope.launch { revealPx = 0f }
                    }
                }
            )
        }
    }
}

@Composable
private fun ContactContent(
    contact: Contact,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.listItemHeight)
                .padding(horizontal = Dimens.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image or Initials
            Box(
                modifier = Modifier
                    .size(Dimens.avatarSizeSmall)
                    .clip(CircleShape)
                    .background(if (contact.profileImageUrl != null) Color.Transparent else Gray300),
                contentAlignment = Alignment.Center
            ) {
                if (contact.profileImageUrl != null) {
                    AsyncImage(
                        model = contact.profileImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = contact.initials,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Gray700
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.paddingMedium))

            // Contact Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = PhoneNumberFormatter.formatPhoneNumber(contact.phoneNumber),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Device Contact Indicator
            if (contact.isInDeviceContacts) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "In device contacts",
                    tint = Blue500,
                    modifier = Modifier.size(Dimens.iconSizeSmall)
                )
            }
        }

        Divider(
            modifier = Modifier.padding(start = 72.dp),
            color = Gray300,
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun ActionButton(
    background: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    buttonWidth: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(buttonWidth)
            .fillMaxHeight()
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = White,
            modifier = Modifier.size(Dimens.iconSizeMedium)
        )
    }
}