package com.zarinraim.dragdrop

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.Job

/**
 * Drag source modifier that handles drag gestures.
 *
 * @param sourceEntity  data that must receive [DropTarget]
 */
fun Modifier.dragOnLongPress(
    sourceEntity: Any?,
): Modifier = composed {
    val state = LocalDraggableState.current
    var position by remember { mutableStateOf(Offset.Zero) }

    val scope = rememberCoroutineScope()
    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    onGloballyPositioned {
        position = it.localToRoot(Offset.Zero)
    }.pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDragStart = {
                Log.v(Tag, "Drag gesture started")
                state.onDragStart(position, it, sourceEntity)
            },
            onDrag = { change, dragAmount ->
                state.onDrag(change, dragAmount)

                if (overscrollJob?.isActive == true) return@detectDragGesturesAfterLongPress
                overscrollJob = state.autoOverscrollWhenRequired(scope)
            },
            onDragEnd = {
                Log.v(Tag, "Drag gesture ended")
                state.onDragEndOrCancel()
                overscrollJob?.cancel()
            },
            onDragCancel = {
                Log.v(Tag, "Drag gesture interrupted")
                state.onDragEndOrCancel()
                overscrollJob?.cancel()
            },
        )
    }
}