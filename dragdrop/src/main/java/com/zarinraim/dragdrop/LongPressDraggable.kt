package com.zarinraim.dragdrop

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.zarinraim.dragdrop.DraggableState.Companion.ScreenEdgeOffset
import com.zarinraim.dragdrop.DraggableState.ScrollDirection
import kotlin.math.abs

internal val LocalDraggableState = compositionLocalOf { DraggableState(ScrollState(0)) }

/**
 * LongPressDraggable composable is a wrapper for a draggable area. Holds [DraggableState].
 * Responsible for positioning of draggable item during dragging action.
 */
@Composable
fun LongPressDraggable(
    draggableItem: @Composable (DraggableItemState) -> Unit,
    scrollState: ScrollableState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = remember { DraggableState(scrollState) }
    CompositionLocalProvider(
        LocalDraggableState provides state,
    ) {
        val dragPosition = state.dragPosition
        var containerSize by remember { mutableStateOf(IntSize.Zero) }

        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    containerSize = it.size
                    it.boundsInRoot().let { rect ->
                        state.scrollDirection = rect.checkOverscroll(dragPosition)
                    }
                },
        ) {
            content()
            if (state.isDragging) {
                var draggableItemSize by remember { mutableStateOf(IntSize.Zero) }

                Box(
                    modifier = Modifier
                        .onGloballyPositioned { draggableItemSize = it.size }
                        .graphicsLayer {
                            alpha = if (draggableItemSize == IntSize.Zero) 0f else .9f

                            calculateTranslation(
                                currentDragPosition = dragPosition,
                                containerSize = containerSize,
                                draggableItemSize = draggableItemSize,
                            )
                        },
                ) {
                    state.draggableContent?.invoke(draggableItem)
                }
            }
        }
    }
}

private fun GraphicsLayerScope.calculateTranslation(
    currentDragPosition: Offset,
    containerSize: IntSize,
    draggableItemSize: IntSize,
) {
    val horizontalPosition = draggableItemSize.width / 2
    val verticalPosition = draggableItemSize.height + 20.dp.toPx()

    translationX = currentDragPosition.x
        .minus(horizontalPosition)
        .coerceIn(0f, (containerSize.width - draggableItemSize.width).toFloat())

    translationY = currentDragPosition.y
        .minus(verticalPosition)
        .coerceIn(0f, (containerSize.height - draggableItemSize.height).toFloat())
}

private fun Rect.checkOverscroll(offset: Offset): ScrollDirection = when {
    offset.y < center.y && abs(top - offset.y) in 0f..ScreenEdgeOffset -> ScrollDirection.Up
    offset.y > center.y && (bottom - offset.y) < ScreenEdgeOffset -> ScrollDirection.Down
    else -> ScrollDirection.None
}

internal const val Tag = "DRAGDROP"