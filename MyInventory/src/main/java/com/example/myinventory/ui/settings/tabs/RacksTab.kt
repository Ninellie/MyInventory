package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Location
import com.example.myinventory.data.models.Site
import com.example.myinventory.data.models.Rack
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.DropdownSelector
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.EntityCard
import com.example.myinventory.ui.settings.ItemAddField
import com.example.myinventory.ui.settings.ClearFiltersButton
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun RacksTab(viewModel: SettingsViewModel) {
    val allSites by viewModel.sites.collectAsState()
    val allLocations by viewModel.locations.collectAsState()
    val allRacks by viewModel.racks.collectAsState()

    var selectedSite by remember { mutableStateOf<Site?>(null) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }

    val filteredRacks = allRacks.filter { rack ->
        val matchLocation = selectedLocation?.id?.let { it == rack.locationId } ?: true
        val location = allLocations.find { it.id == rack.locationId }
        val matchSite = selectedSite?.id?.let { it == location?.siteId } ?: true
        matchLocation && matchSite
    }.sortedBy { it.name }

    val filteredLocations = remember(selectedSite, allLocations) {
        if (selectedSite != null) {
            allLocations.filter { it.siteId == selectedSite!!.id }
        } else emptyList()
    }

    RacksTabContent(
        allSites = allSites,
        allLocations = allLocations,

        selectedSite = selectedSite,
        selectedLocation = selectedLocation,

        onSiteSelected = { selectedSite = it; selectedLocation = null},
        onLocationSelected = { selectedLocation = it },

        filteredLocations = filteredLocations,
        filteredRacks = filteredRacks,

        onAdd = { name -> viewModel.addRack(name, selectedLocation!!.id) },
        onEdit = { rack, newName -> viewModel.updateRack(rack.copy(name = newName)) },
        onDelete = { viewModel.deleteRack(it) }
    )
}

@Composable
fun RacksTabContent(
    allSites: List<Site>,
    allLocations : List<Location>,

    selectedSite: Site?,
    selectedLocation: Location?,

    onSiteSelected: (Site?) -> Unit,
    onLocationSelected: (Location?) -> Unit,

    filteredLocations: List<Location>,
    filteredRacks: List<Rack>,

    onAdd: (String) -> Unit,
    onEdit: (Rack, String) -> Unit,
    onDelete: (Rack) -> Unit
){
    var editing by remember { mutableStateOf<Rack?>(null) }
    var deleting by remember { mutableStateOf<Rack?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // 🔹 Site filter
        DropdownSelector(
            label = stringResource(R.string.site),
            items = allSites,
            selectedItem = selectedSite,
            onItemSelected = { onSiteSelected(it) },
            itemToString = { it.name }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Location filter (only if site selected)
        if (selectedSite != null) {
            DropdownSelector(
                label = stringResource(R.string.location),
                items = filteredLocations,
                selectedItem = selectedLocation,
                onItemSelected = { onLocationSelected(it) },
                itemToString = { it.name }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Reset filters button
        if (selectedSite != null || selectedLocation != null) {
            ClearFiltersButton(
                onReset = { 
                    onSiteSelected(null)
                    onLocationSelected(null)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Add rack (only if location selected)
        if (selectedLocation != null) {
            ItemAddField(label = stringResource(R.string.new_rack)) { name -> onAdd(name) }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🔹 Rack list
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredRacks.sortedBy { it.name }) { rack ->
                val loc = allLocations.find { it.id == rack.locationId }
                val site = loc?.let { allSites.find { s -> s.id == it.siteId } }
                val subtitle = listOfNotNull(loc?.name, site?.name).joinToString(", ")

                EntityCard (
                    title = rack.name,
                    subtitle = subtitle,
                    onEdit = { editing = rack },
                    onDelete = { deleting = rack }
                )
            }
        }
    }

    // 🔹 Edit dialog
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

    // 🔹 Delete dialog
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