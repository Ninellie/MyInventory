package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.ItemAddField
import com.example.myinventory.ui.settings.SettingsViewModel
import com.example.myinventory.ui.settings.SimpleItemList

@Composable
fun SitesTab(viewModel: SettingsViewModel) {
    val types by viewModel.sites.collectAsState()
    var editIndex by remember { mutableStateOf<Int?>(null) }
    var deleteIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        ItemAddField(label = stringResource(R.string.new_site)) { name -> viewModel.addSite(name) }

        Spacer(modifier = Modifier.height(16.dp))

        SimpleItemList(
            items = types.sortedBy { it.name }.map { it.name },
            onEditClick = { index -> editIndex = index },
            onDeleteClick = { index -> deleteIndex = index }
        )
    }

    // Диалог редактирования
    editIndex?.let { index ->
        val item = types.sortedBy { it.name }[index]
        EditItemDialog(
            currentName = item.name,
            onDismiss = { editIndex = null },
            onConfirm = { newName ->
                viewModel.updateSite(item.copy(name = newName))
                editIndex = null
            }
        )
    }

    // Диалог удаления
    deleteIndex?.let { index ->
        val item = types.sortedBy { it.name }[index]
        ConfirmDeleteDialog(
            itemName = item.name,
            onDismiss = { deleteIndex = null },
            onConfirm = {
                viewModel.deleteSite(item)
                deleteIndex = null
            }
        )
    }
}
