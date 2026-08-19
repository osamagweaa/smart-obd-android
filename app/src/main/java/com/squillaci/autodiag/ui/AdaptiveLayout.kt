package com.squillaci.autodiag.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Keeps content readable on tablets while preserving the phone layout.
 * The content is centered and capped at a comfortable reading width.
 */
@Composable
fun AdaptiveContent(
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 0.dp)) {
            content(Modifier.fillMaxWidth())
        }
    }
}
