package com.zarinraim.dragdrop

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A state object to control and observe dragging action.
 */
internal class DraggableState(private val scrollState: ScrollableState) {
    /**
     * Whether is draggable item currently dragging or not.
     */
    var isDragging: Boolean by mutableStateOf(false)

    /**
     * Current drag touch position
     */
    var dragPosition by mutableStateOf(Offset.Zero)

    /**
     * Draggable item state holder
     */
    var draggableContent by mutableStateOf<(@Composable (@Composable ((DraggableItemState) -> Unit)) -> Unit)?>(null)

    /**
     * Data that must receive [DropTarget]
     */
    var sourceEntity by mutableStateOf<Any?>(null)

    var scrollDirection: ScrollDirection by mutableStateOf(ScrollDirection.None)

    fun onDragStart(position: Offset, offset: Offset, data: Any?) {
        isDragging = true
        dragPosition = position + offset
        draggableContent = { content -> content(draggableItemState) }
        sourceEntity = data
    }

    fun onDrag(change: PointerInputChange, dragAmount: Offset) {
        change.consume()
        dragPosition += Offset(dragAmount.x, dragAmount.y)
    }

    fun onDragEndOrCancel() {
        isDragging = false
        dragPosition = Offset.Zero
    }

    fun autoOverscrollWhenRequired(scope: CoroutineScope): Job = scope.launch {
        var speed = 1f
        while (scrollDirection != ScrollDirection.None) {
            val scrollBy = (if (scrollDirection == ScrollDirection.Up) -1 else 1) * AutoScrollStep
            scrollState.animateScrollBy(
                value = scrollBy * speed,
                animationSpec = tween(durationMillis = AnimationDuration, easing = LinearEasing),
            )
            speed += Acceleration
        }
    }

    private val draggableItemState: DraggableItemState
        get() {
            return if (activeTargetItems.size == 1) {
                activeTargetItems[activeTargetItems.keys.first()]!!
            } else {
                DraggableItemState.Initiated
            }
        }

    private var activeTargetItems by mutableStateOf(emptyMap<String, DraggableItemState>())

    fun onHoveredTarget(id: String, enabled: Boolean) {
        val draggableItemState = if (enabled) DraggableItemState.Enabled else DraggableItemState.Disabled
        activeTargetItems += (id to draggableItemState)
    }

    fun onUnHoveredTarget(id: String) {
        activeTargetItems = activeTargetItems.toMutableMap().also { it.remove(id) }
    }

    enum class ScrollDirection { Up, Down, None }

    companion object {
        const val AutoScrollStep = 2f
        const val AnimationDuration = 5
        const val Acceleration = .5f
        const val ScreenEdgeOffset = 200f
    }
}