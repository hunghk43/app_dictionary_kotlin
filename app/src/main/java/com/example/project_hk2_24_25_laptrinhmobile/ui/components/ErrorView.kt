package com.example.project_hk2_24_25_laptrinhmobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Component hiển thị error state với retry button
 */
@Composable
fun ErrorView(
    errorMessage: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onRetry: (() -> Unit)? = null,
    retryButtonText: String = "Try Again"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon
        val errorIcon = icon ?: when {
            errorMessage.contains("internet", ignoreCase = true) ||
                    errorMessage.contains("network", ignoreCase = true) ||
                    errorMessage.contains("connection", ignoreCase = true) -> Icons.Default.NetworkCheck
            errorMessage.contains("not found", ignoreCase = true) -> Icons.Default.Search
            else -> Icons.Default.Error
        }

        Icon(
            imageVector = errorIcon,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Retry button
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(text = retryButtonText)
            }
        }
    }
}

/**
 * Compact error view for smaller spaces
 */
@Composable
fun CompactErrorView(
    errorMessage: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}