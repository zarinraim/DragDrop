package com.zarinraim.dragdrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Drop target composable
 *
 * @param targetEntity          info used to compare with source data
 * @param enabled               whether target is allowed to receive dropped data
 * @param onFocusBackground     highlight target when draggable item is over it
 */
@Composable
fun <T> DropTarget(
    targetEntity: T?,
    modifier: Modifier = Modifier,
    onFocusBackground: Color = Color.Transparent,
    enabled: Boolean = true,
    content: @Composable (BoxScope.(T?) -> Unit),
) {
    val state = LocalDraggableState.current
    val dragPosition = state.dragPosition

    // draggable item currently in bounds of this drop target
    var isItemInDropTarget by remember { mutableStateOf(false) }
    // draggable item still in bounds of source target
    val isSourceMatchTarget = state.sourceEntity == targetEntity

    // draggable item is over valid drop target
    val isCurrentDropTarget = isItemInDropTarget && !isSourceMatchTarget
    // draggable item is over enabled drop target
    val isEnabledDropTarget = isCurrentDropTarget && enabled

    Box(
        modifier = modifier
            .onGloballyPositioned {
                it.boundsInRoot().let { rect ->
                    isItemInDropTarget = rect.contains(dragPosition) && state.isDragging
                }
            }
            .background(if (isEnabledDropTarget) onFocusBackground else Color.Transparent),
    ) {
        @Suppress("UNCHECKED_CAST")
        val sourceEntity = if (isEnabledDropTarget && !state.isDragging) state.sourceEntity as? T? else null
        content(sourceEntity)
    }
}