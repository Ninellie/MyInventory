package com.example.myinventory.ui.settings.tabs

import com.example.myinventory.data.models.DeviceModel
import com.example.myinventory.data.models.DeviceType
import com.example.myinventory.data.models.Vendor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.DropdownSelector
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.EntityCard
import com.example.myinventory.ui.settings.FilterRow
import com.example.myinventory.ui.settings.ItemAddField
import com.example.myinventory.ui.settings.ClearFiltersButton

@Composable
fun DeviceModelsTabContent(
    models: List<DeviceModel>,
    vendors: List<Vendor>,
    types: List<DeviceType>,
    selectedVendor: Vendor?,
    onVendorSelected: (Vendor?) -> Unit,
    selectedType: DeviceType?,
    onTypeSelected: (DeviceType?) -> Unit,
    onAdd: (String) -> Unit,
    onEdit: (DeviceModel, String) -> Unit,
    onDelete: (DeviceModel) -> Unit
) {
    var editing by remember { mutableStateOf<DeviceModel?>(null) }
    var deleting by remember { mutableStateOf<DeviceModel?>(null) }

    Column(Modifier.padding(16.dp)) {
        FilterRow {
            DropdownSelector(
                label = stringResource(R.string.vendor),
                items = vendors,
                selectedItem = selectedVendor,
                onItemSelected = { onVendorSelected(it) },
                itemToString = { it.name },
                modifier = Modifier.weight(1f)
            )
            DropdownSelector(
                label = stringResource(R.string.device_type),
                items = types,
                selectedItem = selectedType,
                onItemSelected = { onTypeSelected(it) },
                itemToString = { it.name },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedVendor != null || selectedType != null) {
            ClearFiltersButton(
                onReset = { 
                    onVendorSelected(null)
                    onTypeSelected(null)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedVendor != null && selectedType != null) {
            ItemAddField(label = stringResource(R.string.new_model)) { name ->
                onAdd(name)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(models) { model ->
                val vendor = vendors.find { it.id == model.vendorId }
                val type = types.find { it.id == model.deviceTypeId }
                val subtitle = listOfNotNull(vendor?.name, type?.name).joinToString(", ")

                EntityCard(
                    title = model.name,
                    subtitle = subtitle,
                    onEdit = { editing = model },
                    onDelete = { deleting = model }
                )
            }
        }
    }

    editing?.let {
        EditItemDialog(
            currentName = it.name,
            onDismiss = { editing = null },
            onConfirm = { newName ->
                onEdit(it, newName)
                editing = null
            }
        )
    }

    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = {
                onDelete(it)
                deleting = null
            }
        )
    }
}