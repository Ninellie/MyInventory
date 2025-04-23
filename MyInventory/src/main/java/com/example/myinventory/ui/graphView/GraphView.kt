package com.example.myinventory.ui.graphView

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import com.example.myinventory.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GraphView(
    nodes: GraphNode,
    modifier: Modifier = Modifier
) {
    val contentColor = LocalContentColor.current

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val radiusBase = 150f // базовое расстояние от центра до первого уровня

    BoxWithConstraints(modifier = modifier) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()
        val canvasCenter = Offset(canvasWidth / 2, canvasHeight / 2)

        val gestureModifier = Modifier.pointerInput(Unit) {
            detectTransformGestures { centroid, pan, zoom, _ ->
                val prevScale = scale
                val rawNewScale = prevScale * zoom
                val newScale = rawNewScale.coerceIn(0.5f, 5f)

                val scaleChanged = newScale != prevScale

                // Центр между пальцами относительно центра экрана
                val focusToOffset = centroid - (size.center.toOffset() + offset)

                // Компенсация, только если реально увеличиваем или уменьшаем
                val zoomCompensation = if (scaleChanged) focusToOffset * (1 - zoom) else Offset.Zero

                val newOffset = offset + pan + zoomCompensation

                val visibleRadius = radiusBase * newScale * 3f
                val maxOffsetX = (canvasWidth / 2 + visibleRadius) - 50f
                val maxOffsetY = (canvasHeight / 2 + visibleRadius) - 50f

                offset = Offset(
                    x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                    y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                )

                scale = newScale
            }

        }

        val map = mapOf(
            NodeType.ROOT to stringResource(R.string.root),
            NodeType.SITE to stringResource(R.string.site),
            NodeType.LOCATION to stringResource(R.string.location),
            NodeType.RACK to stringResource(R.string.rack),
            NodeType.MODEL to stringResource(R.string.model),
            NodeType.DEVICE to stringResource(R.string.device),
        )


        Canvas(modifier = Modifier
            .fillMaxSize()
            .then(gestureModifier)) {
            val adjustedCenter = canvasCenter + offset

            drawGraph(
                node = nodes,
                center = adjustedCenter,
                startAngle = 0f,
                sweepAngle = 2 * PI.toFloat(),
                radius = radiusBase * scale,
                textColor = contentColor,
                nodeTypeStringMap = map
            )
        }
    }
}

fun DrawScope.drawGraph(
    node: GraphNode,
    center: Offset,
    startAngle: Float,
    sweepAngle: Float,
    radius: Float,
    textColor: Color,
    nodeTypeStringMap: Map<NodeType, String>
) {
    drawNode(center, node, textColor, nodeTypeStringMap)

    val childCount = node.children.size
    if (childCount == 0) return

    val angleStep = sweepAngle / childCount
    node.children.forEachIndexed { index, child ->
        val angle = startAngle + angleStep * index + angleStep / 2
        val childX = center.x + cos(angle) * radius
        val childY = center.y + sin(angle) * radius
        val childOffset = Offset(childX, childY)

        // линия от родителя к потомку
        drawLine(Color.Gray, center, childOffset, strokeWidth = 2f)

        drawGraph(
            node = child,
            center = childOffset,
            startAngle = angle - angleStep / 2,
            sweepAngle = angleStep,
            radius = radius * 0.85f,
            textColor = textColor,
            nodeTypeStringMap
        )
    }
}

fun DrawScope.drawNode(
    center: Offset,
    node: GraphNode,
    textColor: Color,
    nodeTypeStringMap: Map<NodeType, String>
) {
    val radius = 20f
    val color = when (node.type) {
        NodeType.ROOT -> Color(0xFFF44336)
        NodeType.SITE -> Color(0xFFFFC107)
        NodeType.LOCATION -> Color(0xFF4CAF50)
        NodeType.RACK -> Color(0xFFFFA500)
        NodeType.MODEL -> Color(0xFF9C27B0)
        NodeType.DEVICE -> Color(0xFF2196F3)
    }
    drawCircle(color, radius, center)

    drawIntoCanvas {
        val paint = Paint().apply {
            this.color = textColor.toArgb()
            textSize = 28f
            isAntiAlias = true
        }

        it.nativeCanvas.drawText(
            node.name + " (" + nodeTypeStringMap[node.type] + ")",
            center.x + radius + 4f,
            center.y + 10f,
            paint
        )
    }
}

