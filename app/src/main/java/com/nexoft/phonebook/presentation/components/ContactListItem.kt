package com.nexoft.phonebook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.ui.theme.*
import com.nexoft.phonebook.utils.PhoneNumberFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { value ->
            when (value) {
                DismissValue.DismissedToStart -> {
                    onDeleteClick()
                    false
                }
                DismissValue.DismissedToEnd -> {
                    onEditClick()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = {
            SwipeBackground(dismissState.dismissDirection)
        },
        dismissContent = {
            ContactContent(
                contact = contact,
                onClick = onClick
            )
        }
    )
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
                    tint = Green500,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    dismissDirection: DismissDirection?
) {
    val color = when (dismissDirection) {
        DismissDirection.EndToStart -> RedDelete
        DismissDirection.StartToEnd -> Green500
        else -> Color.Transparent
    }

    val icon = when (dismissDirection) {
        DismissDirection.EndToStart -> Icons.Default.Delete
        DismissDirection.StartToEnd -> Icons.Default.Edit
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = Dimens.paddingLarge),
        contentAlignment = when (dismissDirection) {
            DismissDirection.EndToStart -> Alignment.CenterEnd
            DismissDirection.StartToEnd -> Alignment.CenterStart
            else -> Alignment.Center
        }
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(Dimens.iconSizeMedium)
            )
        }
    }
}