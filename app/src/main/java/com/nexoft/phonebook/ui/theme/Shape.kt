package com.nexoft.phonebook.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape

object Shapes {
    val extraSmall: Shape = RoundedCornerShape(Dimens.radiusXSmall)
    val small: Shape = RoundedCornerShape(Dimens.radiusSmall)
    val medium: Shape = RoundedCornerShape(Dimens.radiusMedium)
    val large: Shape = RoundedCornerShape(Dimens.radiusLarge)
    val extraLarge: Shape = RoundedCornerShape(Dimens.radiusXLarge)
    val circle: Shape = RoundedCornerShape(Dimens.radiusCircle)

    // Custom shapes
    val bottomSheetShape: Shape = RoundedCornerShape(
        topStart = Dimens.radiusXLarge,
        topEnd = Dimens.radiusXLarge
    )

    val searchBarShape: Shape = RoundedCornerShape(Dimens.radiusLarge)
}