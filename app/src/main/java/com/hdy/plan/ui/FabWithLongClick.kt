package com.hdy.plan.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FabWithLongClick(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        shape = FloatingActionButtonDefaults.smallShape,
        shadowElevation = 6.dp,
        modifier = Modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // or use rememberRipple(bounded = false)
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}
