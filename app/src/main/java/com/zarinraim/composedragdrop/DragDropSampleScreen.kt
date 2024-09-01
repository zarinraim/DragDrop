package com.zarinraim.composedragdrop

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zarinraim.composedragdrop.DragDropSampleViewModel.ScreenState
import com.zarinraim.composedragdrop.ui.theme.ComposeDragDropTheme
import com.zarinraim.dragdrop.DraggableItemState
import com.zarinraim.dragdrop.DropTarget
import com.zarinraim.dragdrop.LongPressDraggable
import com.zarinraim.dragdrop.dragOnLongPress

@Composable
fun DragDropSampleScreen(viewModel: DragDropSampleViewModel) {
    Content(
        state = viewModel.state.value,
        onDrop = viewModel::onDrop,
        closeDialog = viewModel::closeDialog,
    )
}

@Composable
private fun Content(
    state: ScreenState,
    onDrop: (String, String) -> Unit,
    closeDialog: () -> Unit,
) {
    val scrollState = rememberLazyListState()
//    val scrollState = rememberScrollState()

    LongPressDraggable(
        draggableItem = { DraggableItem(state = it) },
        scrollState = scrollState,
    ) {
        ContentLazyColumn(
            scrollState = scrollState,
            state = state,
            onDrop = onDrop,
        )
    }

    if (state.dropDialogVisible) {
        AlertDialog(
            onDismissRequest = closeDialog,
            confirmButton = { TextButton(onClick = closeDialog) { Text(text = "Close") } },
            text = {
                Text(text = state.dialogMessage)
            }
        )
    }
}

@Composable
private fun ContentColumn(
    scrollState: ScrollState,
    state: ScreenState,
    onDrop: (String, String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        state.items.forEach() { item ->
            SourceTargetItem(
                item = item,
                onDrop = onDrop
            )
        }
    }
}

@Composable
private fun ContentLazyColumn(
    scrollState: LazyListState,
    state: ScreenState,
    onDrop: (String, String) -> Unit,
) {
    LazyColumn(state = scrollState, modifier = Modifier.fillMaxSize()) {
//        item { Spacer(modifier = Modifier.height(16.dp)) }
        items(state.items) { item ->
            SourceTargetItem(
                item = item,
                onDrop = onDrop
            )
        }
//        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun SourceTargetItem(
    item: ScreenState.Item,
    onDrop: (String, String) -> Unit,
) {
    DropTarget(
        targetEntity = item.text,
        enabled = item.enabled,
        onFocusBackground = MaterialTheme.colorScheme.primaryContainer,
    ) { sourceData ->
        if (sourceData != null) {
            onDrop(sourceData, item.text)
        }

        Item(
            text = item.text,
            modifier = if (item.enabled) Modifier.dragOnLongPress(item.text) else Modifier,
        )
    }
}

@Composable
private fun Item(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, MaterialTheme.colorScheme.secondaryContainer)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = text)
    }
}

@Composable
private fun DraggableItem(state: DraggableItemState) {
    val shape = MaterialTheme.shapes.small
    val background = when (state) {
        DraggableItemState.Enabled -> Color(0xffacf2c6)
        DraggableItemState.Disabled -> Color(0xfff2baac)
        DraggableItemState.Initiated -> Color(0xffccdbd2)
    }
    Column(
        modifier = Modifier
            .clip(shape = shape)
            .border(width = .15.dp, color = MaterialTheme.colorScheme.primary, shape = shape)
            .background(color = background, shape = shape)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(text = "Drag me")
    }
}

@Preview(showBackground = true)
@Composable
fun DraggableItemPreview() {
    ComposeDragDropTheme {
        Column {
            DraggableItem(state = DraggableItemState.Initiated)
            Spacer(modifier = Modifier.height(16.dp))
            DraggableItem(state = DraggableItemState.Enabled)
            Spacer(modifier = Modifier.height(16.dp))
            DraggableItem(state = DraggableItemState.Disabled)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DragDropSamplePreview() {
    ComposeDragDropTheme {
        Content(
            state = ScreenState(
                items = listOf(
                    ScreenState.Item(text = "Alice", enabled = true),
                    ScreenState.Item(text = "Bob", enabled = true)
                ),
                dropDialogVisible = false,
                dialogMessage = "",
            ),
            onDrop = { _, _ -> },
            closeDialog = {},
        )
    }
}