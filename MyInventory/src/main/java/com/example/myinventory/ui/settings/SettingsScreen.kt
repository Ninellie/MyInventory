package com.example.myinventory.ui.settings

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myinventory.R
import com.example.myinventory.ui.settings.tabs.DeviceModelsTab
import com.example.myinventory.ui.settings.tabs.DeviceTypeTab
import com.example.myinventory.ui.settings.tabs.FieldTypesTab
import com.example.myinventory.ui.settings.tabs.LocationsTab
import com.example.myinventory.ui.settings.tabs.RacksTab
import com.example.myinventory.ui.settings.tabs.SitesTab
import com.example.myinventory.ui.settings.tabs.VendorsTab
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val tabs = listOf(
        stringResource(R.string.device_types),
        stringResource(R.string.field_types),
        stringResource(R.string.models),
        stringResource(R.string.racks),
        stringResource(R.string.locations),
        stringResource(R.string.sites),
        stringResource(R.string.vendors),
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    PreloadTabPages(viewModel)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val pagerState = rememberPagerState { tabs.size }

        LaunchedEffect(selectedTab) {
            pagerState.animateScrollToPage(selectedTab)
        }
        LaunchedEffect(pagerState.currentPage) {
            selectedTab = pagerState.currentPage
        }

        val coroutineScope = rememberCoroutineScope()

        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            Spacer(modifier = Modifier.height(24.dp))
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title -> Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            coroutineScope.launch { pagerState.scrollToPage(index) }
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    0 -> DeviceTypeTab(viewModel)
                    1 -> FieldTypesTab(viewModel)
                    2 -> DeviceModelsTab(viewModel)
                    3 -> RacksTab(viewModel)
                    4 -> LocationsTab(viewModel)
                    5 -> SitesTab(viewModel)
                    6 -> VendorsTab(viewModel)
                }
            }
        }
    }
}

@Composable
private fun PreloadTabPages(viewModel: SettingsViewModel) {
    // Невидимый LazyRow, просто чтобы вызвать все табы хотя бы один раз
    LazyRow(modifier = Modifier.size(0.dp)) {
        items(7) { index ->
            when (index) {
                0 -> DeviceTypeTab(viewModel)
                1 -> FieldTypesTab(viewModel)
                2 -> DeviceModelsTab(viewModel)
                3 -> RacksTab(viewModel)
                4 -> LocationsTab(viewModel)
                5 -> SitesTab(viewModel)
                6 -> VendorsTab(viewModel)
            }
        }
    }
}
