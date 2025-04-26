package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Site
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.DropdownSelector
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun LocationsTab(viewModel: SettingsViewModel) {
    val allSites by viewModel.sites.collectAsState()
    allSites.sortedBy { it.name }
    val allLocations by viewModel.locations.collectAsState()

    val locationName = remember { mutableStateOf("") }

    var selectedSite by remember { mutableStateOf<Site?>(null) }

    val filtered = allLocations.filter { location ->
        val matchName = location.name.contains(locationName.value, ignoreCase = true)
        val matchSite = selectedSite?.id?.let { it == location.siteId } ?: true
        matchName && matchSite
    }.sortedBy { it.name }

    var editing by remember { mutableStateOf<Location?>(null) }
    var deleting by remember { mutableStateOf<Location?>(null) }

    Column(Modifier.padding(16.dp)) {
        DropdownSelector(
            label = stringResource(R.string.site),
            items = allSites.sortedBy { it.name },
            selectedItem = selectedSite,
            onItemSelected = { selectedSite = it },
            itemToString = { it.name }
        )

        Spacer(modifier = Modifier.height(8.dp))

        AddItemField(
            label = stringResource(R.string.new_location),
            text = locationName,
            onAdd = { viewModel.addLocation(it, selectedSite!!.id) },
            onValueChange = { },
            isAddEnabled = locationName.value != "" && selectedSite != null
        )

        if (selectedSite != null || locationName.value != "") {
            Spacer(modifier = Modifier.height(8.dp))
            ClearFiltersButton(
                onReset = {
                    selectedSite = null
                    locationName.value = ""
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemList(
            items = filtered,
            getTitle = { it.name },
            getSubtitle = { siteNameFor(it.siteId, allSites) },
            onEdit = { editing = it },
            onDelete = { deleting = it },
            emptyMessage = getEmptyMessage(allLocations, filtered)
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

@Composable
private fun getEmptyMessage(all: List<Location>, filtered: List<Location>) : String
{
    if (all.isEmpty()){
        return stringResource(R.string.add_first_location)
    }
    if (filtered.isEmpty()){
        return stringResource(R.string.nothing_was_found)
    }
    return ""
}

