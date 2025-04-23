package com.example.myinventory.ui.graphView

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun GraphScreen(
    viewModel: GraphViewModel,
){
    val vendors by viewModel.vendors.collectAsState()
    val deviceTypes by viewModel.deviceTypes.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val racks by viewModel.racks.collectAsState()
    val deviceModels = viewModel.deviceModels.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val sites by viewModel.sites.collectAsState()

    fun buildDeviceInventoryGraph(): GraphNode {
        val locationMap = locations.groupBy { it.siteId }
        val rackMap = racks.groupBy { it.locationId }
        val modelMap = deviceModels.value.associateBy { it.id }

        fun buildModelNodes(rackId: Int?, locationId: Int): List<GraphNode> {
            val filteredDevices = devices.filter {
                it.rackId == rackId && it.locationId == locationId
            }

            val groupedByModel = filteredDevices.groupBy { it.modelId }

            return groupedByModel.mapNotNull { (modelId, deviceList) ->
                val model = modelMap[modelId] ?: return@mapNotNull null
                GraphNode(
                    id = "model_${model.id}",
                    name = model.name,
                    type = NodeType.MODEL,
                    children = deviceList.map { device ->
                        GraphNode(
                            id = "device_${device.id}",
                            name = device.name,
                            type = NodeType.DEVICE
                        )
                    }
                )
            }
        }

        fun buildRackNodes(locationId: Int): List<GraphNode> {
            return rackMap[locationId].orEmpty().map { rack ->
                GraphNode(
                    id = "rack_${rack.id}",
                    name = rack.name,
                    type = NodeType.RACK,
                    children = buildModelNodes(rack.id, locationId)
                )
            }
        }

        fun buildLocationNodes(siteId: Int): List<GraphNode> {
            return locationMap[siteId].orEmpty().map { location ->
                val rackChildren = buildRackNodes(location.id)
                val modelNodesDirectlyInLocation = buildModelNodes(null, location.id)

                GraphNode(
                    id = "location_${location.id}",
                    name = location.name,
                    type = NodeType.LOCATION,
                    children = rackChildren + modelNodesDirectlyInLocation
                )
            }
        }

        // Создаём один корневой узел (например, "Все объекты")
        return GraphNode(
            id = "root",
            name = "root",
            type = NodeType.ROOT,
            children = sites.map { site ->
                GraphNode(
                    id = "site_${site.id}",
                    name = site.name,
                    type = NodeType.SITE,
                    children = buildLocationNodes(site.id)
                )
            }
        )
    }

    GraphView(nodes = buildDeviceInventoryGraph(), modifier = Modifier.fillMaxSize())
}
