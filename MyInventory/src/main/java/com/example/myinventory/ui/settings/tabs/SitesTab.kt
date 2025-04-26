package com.example.myinventory.ui.settings.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.data.models.Site
import com.example.myinventory.ui.components.AddItemField
import com.example.myinventory.ui.components.ClearFiltersButton
import com.example.myinventory.ui.components.ItemList
import com.example.myinventory.ui.settings.ConfirmDeleteDialog
import com.example.myinventory.ui.settings.EditItemDialog
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun SitesTab(viewModel: SettingsViewModel) {
    val allSites by viewModel.sites.collectAsState()

    var siteName = remember { mutableStateOf("") }

    val filteredSites = allSites.filter { site ->
        siteName.value.let {site.name.contains(it, true)}
    }.sortedBy { it.name }

    var editing by remember { mutableStateOf<Site?>(null) }
    var deleting by remember { mutableStateOf<Site?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        AddItemField(
            stringResource(R.string.new_site),
            text = siteName,
            onAdd = { viewModel.addSite(it) },
            onValueChange = { }
        )

        if (siteName.value != "") {
            Spacer(modifier = Modifier.height(8.dp))
            ClearFiltersButton(
                onReset = { siteName.value = ""}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemList(
            Modifier,
            filteredSites,
            getTitle = { it.name },
            onEdit = { item -> editing = item },
            onDelete = { item -> deleting = item },
            emptyMessage = getEmptyMessage(allSites, filteredSites)
        )
    }

    editing?.let {
        EditItemDialog(
            currentName = it.name,
            onDismiss = { editing = null },
            onConfirm = { newName ->
                viewModel.updateSite(it.copy(name = newName))
                editing = null
            }
        )
    }

    deleting?.let {
        ConfirmDeleteDialog(
            itemName = it.name,
            onDismiss = { deleting = null },
            onConfirm = {
                viewModel.deleteSite(it)
                deleting = null
            }
        )
    }
}

@Composable
fun getEmptyMessage(all: List<Site>, filtered: List<Site>) : String
{
    if (all.isEmpty()){
        return stringResource(R.string.add_first_site)
    }
    if (filtered.isEmpty()){
        return stringResource(R.string.nothing_was_found)
    }
    return ""
}

