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
fun VendorsTab(viewModel: SettingsViewModel) {
    val types by viewModel.vendors.collectAsState()

    var editIndex by remember { mutableStateOf<Int?>(null) }
    var deleteIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        ItemAddField(label = stringResource(R.string.new_vendor)) { name ->
            viewModel.addVendor(name)
        }

        Spacer(modifier = Modifier.height(16.dp))

        VendorsList(
            viewModel,
            onEditClick = { index -> editIndex = index },
            onDeleteClick = { index -> deleteIndex = index })
    }

    // Диалог редактирования
    editIndex?.let { index ->
        val item = types.sortedBy { it.name }[index]
        EditItemDialog(
            currentName = item.name,
            onDismiss = { editIndex = null },
            onConfirm = { newName ->
                viewModel.updateVendor(item.copy(name = newName))
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
                viewModel.deleteVendor(item)
                deleteIndex = null
            }
        )
    }
}


@Composable
fun VendorsList(
    viewModel: SettingsViewModel,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit)
{
    val types by viewModel.vendors.collectAsState()

    SimpleItemList(
        items = types.sortedBy { it.name }.map { it.name },
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    )
}