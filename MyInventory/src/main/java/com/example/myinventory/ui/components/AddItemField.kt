package com.example.myinventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R

@Composable
fun AddItemField(
    label: String,
    text: MutableState<String> = remember { mutableStateOf("") },
    onAdd: (String) -> Unit,
    onValueChange: (String) -> Unit,
    minLength: Int = 2,
    maxLength: Int = 50,
    isAddEnabled: Boolean = true
) {
    //var text by remember { mutableStateOf("") }

    val isButtonEnabled = ((text.value.length in (minLength..maxLength)) && isAddEnabled)

    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text.value,
            onValueChange = { onValueChange.invoke(it); text.value = it },
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                if (isButtonEnabled) {
                    onAdd(text.value)
                    text.value = ""
                    focusManager.clearFocus()
                }
            },
            enabled = isButtonEnabled
        ) { Text(stringResource(R.string.add)) }
    }
} 