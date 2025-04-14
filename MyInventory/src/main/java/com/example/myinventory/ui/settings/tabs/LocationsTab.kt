package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Site
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.DropdownSelector
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.EntityListSectionWithFilter
import com.example.myinventory.ui.settings.ItemAddField
import com.example.myinventory.ui.settings.ClearFiltersButton
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun LocationsTab(viewModel: SettingsViewModel) {
    val allSites by viewModel.sites.collectAsState()
    val allLocations by viewModel.locations.collectAsState()

    var selectedSite by remember { mutableStateOf<Site?>(null) }

    val filtered = allLocations.filter { selectedSite?.id == null || it.siteId == selectedSite!!.id }

    var editing by remember { mutableStateOf<Location?>(null) }
    var deleting by remember { mutableStateOf<Location?>(null) }

    Column(Modifier.padding(16.dp)) {
        DropdownSelector(
            label = stringResource(R.string.site),
            items = allSites,
            selectedItem = selectedSite,
            onItemSelected = { selectedSite = it },
            itemToString = { it.name }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedSite != null) {
            ClearFiltersButton(
                onReset = { selectedSite = null }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedSite != null) {
            ItemAddField(label = stringResource(R.string.new_location)) {
                viewModel.addLocation(it, selectedSite!!.id)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        EntityListSectionWithFilter(
            items = filtered.sortedBy { it.name },
            getTitle = { it.name },
            getSubtitle = { siteNameFor(it.siteId, allSites) },
            onEdit = { editing = it },
            onDelete = { deleting = it }
        )
    }

    editing?.let {
        EditItemDialog(
            currentName = it.name,
            onDismiss = { editing = null },
            onConfirm = { newName ->
                viewModel.updateLocation(editing!!.copy(name = newName))
                editing = null
            }
        )
    }

    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = {
                viewModel.deleteLocation(it)
                deleting = null}
        )
    }
}

private fun siteNameFor(siteId: Int, sites: List<Site>) =
    sites.find { it.id == siteId }?.name ?: "unknown"