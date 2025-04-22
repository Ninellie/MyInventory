package com.example.myinventory.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R

@Composable
fun ClearFiltersButton(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onReset,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = stringResource(R.string.clear_filters)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.clear_filters))
    }
}