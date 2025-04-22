package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Site
import com.example.myinventory.data.models.Rack
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.DropdownSelector
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun RacksTab(viewModel: SettingsViewModel) {
    val allSites by viewModel.sites.collectAsState()
    allSites.sortedBy { it.name }
    val allLocations by viewModel.locations.collectAsState()
    allLocations.sortedBy { it.name }
    val allRacks by viewModel.racks.collectAsState()

    var text by remember { mutableStateOf("") }

    var selectedSite by remember { mutableStateOf<Site?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }

    val filteredRacks = allRacks.filter { rack ->
        val location = allLocations.find { it.id == rack.locationId }

        val matchName = rack.name.contains(text, ignoreCase = true)
        val matchLocation = selectedLocation?.id?.let { it == rack.locationId } ?: true
        val matchSite = selectedSite?.id?.let { it == location?.siteId } ?: true

        matchLocation && matchSite && matchName
    }.sortedBy { it.name }

    val filteredLocations = remember(selectedSite, allLocations) {
        if (selectedSite != null) {
            allLocations.filter { it.siteId == selectedSite!!.id }
        } else emptyList()
    }

    var editing by remember { mutableStateOf<Rack?>(null) }
    var deleting by remember { mutableStateOf<Rack?>(null) }

    var resetToken by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Site filter
        DropdownSelector(
            label = stringResource(R.string.site),
            items = allSites,
            selectedItem = selectedSite,
            onItemSelected = { selectedLocation = null; selectedSite = it },
            itemToString = { it.name }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location filter (only if site selected)
        if (selectedSite != null) {
            DropdownSelector(
                label = stringResource(R.string.location),
                items = filteredLocations,
                selectedItem = selectedLocation,
                onItemSelected = { selectedLocation = it },
                itemToString = { it.name }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reset filters button
        if (selectedSite != null || selectedLocation != null) {
            ClearFiltersButton(
                onReset = { selectedSite = null; selectedLocation = null; resetToken++ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add rack (only if location selected)
        if (selectedLocation != null) {

            AddItemField(
                label = stringResource(R.string.new_rack),
                onAdd = { name -> viewModel.addRack(name, selectedLocation!!.id) },
                onValueChange = {text = it}
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Rack list
        ItemList(
            items = filteredRacks,
            getTitle = { it.name },
            getSubtitle = { getRackSubtitle(locations = allLocations, sites = allSites, it) },
            onEdit = { editing = it },
            onDelete = { deleting = it },
            emptyMessage = getEmptyMessage(allRacks, filteredRacks)
        )
    }

    editing?.let {
        EditItemDialog(
            currentName = it.name,
            onDismiss = { editing = null },
            onConfirm = { newName -> viewModel.updateRack(it.copy(name = newName)); editing = null }
        )
    }

    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = { viewModel.deleteRack(it); deleting = null }
        )
    }

}

private fun getRackSubtitle(locations: List<Location>, sites: List<Site>, rack: Rack): String {
    val location = locations.find { it.id == rack.locationId }
    val site = location?.let { sites.find { s -> s.id == it.siteId } }
    val subtitle = listOfNotNull(location?.name, site?.name).joinToString(", ")
    return subtitle
}


@Composable
private fun getEmptyMessage(all: List<Rack>, filtered: List<Rack>) : String
{
    if (all.isEmpty()){
        return stringResource(R.string.add_first_rack)
    }
    if (filtered.isEmpty()){
        return stringResource(R.string.nothing_was_found)
    }
    return ""
}