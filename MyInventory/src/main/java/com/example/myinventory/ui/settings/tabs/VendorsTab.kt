package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Vendor
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun VendorsTab(viewModel: SettingsViewModel) {
    val allVendors by viewModel.vendors.collectAsState()

    var text by remember { mutableStateOf("") }

    val filteredVendors = allVendors.filter { vendor ->
        text.let {vendor.name.contains(it, true)}
    }.sortedBy { it.name }

    var editing by remember { mutableStateOf<Vendor?>(null) }
    var deleting by remember { mutableStateOf<Vendor?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        AddItemField(
            stringResource(R.string.new_vendor),
            onAdd = { viewModel.addVendor(it); text = "" },
            onValueChange = {text = it}
        )

        Spacer(modifier = Modifier.height(16.dp))

        ItemList(
            Modifier,
            filteredVendors,
            getTitle = { it.name },
            onEdit = { item -> editing = item },
            onDelete = { item -> deleting = item },
            emptyMessage = getEmptyMessage(allVendors, filteredVendors)
        )
    }

    editing?.let {
        EditItemDialog(
            currentName = it.name,
            onDismiss = { editing = null },
            onConfirm = { newName ->
                viewModel.updateVendor(it.copy(name = newName))
                editing = null
            }
        )
    }

    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = {
                viewModel.deleteVendor(it)
                deleting = null
            }
        )
    }
}

@Composable
fun getEmptyMessage(allVendors: List<Vendor>, filteredVendors: List<Vendor>) : String
{
    if (allVendors.isEmpty()){
        return stringResource(R.string.add_first_vendor)
    }
    if (filteredVendors.isEmpty()){
        return stringResource(R.string.nothing_was_found)
    }
    return ""
}

