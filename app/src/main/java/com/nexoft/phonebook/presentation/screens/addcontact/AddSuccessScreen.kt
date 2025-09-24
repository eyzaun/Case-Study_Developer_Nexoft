package com.nexoft.phonebook.presentation.screens.addcontact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.nexoft.phonebook.R
import com.nexoft.phonebook.ui.theme.Dimens
import com.nexoft.phonebook.ui.theme.Gray700
import com.nexoft.phonebook.ui.theme.Green500
import com.nexoft.phonebook.ui.theme.White

@Composable
fun AddSuccessScreen(onFinish: () -> Unit) {
    // Prepare Lottie composition and progress
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 0.8f // a bit slower for a longer, smoother feel
    )

    // Navigate when animation completes (with a small buffer)
    LaunchedEffect(composition, progress) {
        if (composition != null && progress >= 1f) {
            // Add a tiny buffer so users can perceive the final state
            kotlinx.coroutines.delay(250)
            onFinish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.paddingLarge))

            Text(
                text = androidx.compose.ui.res.stringResource(id = R.string.add_success_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Green500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))

            Text(
                text = androidx.compose.ui.res.stringResource(id = R.string.add_success_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = Gray700,
                textAlign = TextAlign.Center
            )
        }
    }
}
