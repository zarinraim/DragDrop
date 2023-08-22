package com.zarinraim.composedragdrop

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class DragDropSampleViewModel : ViewModel() {
    private val _state = mutableStateOf(ScreenState())
    val state: State<ScreenState> get() = _state

    init {
        _state.value = ScreenState(
            items = listOf(
                ScreenState.Item("Alice", true),
                ScreenState.Item("Bob", true),
                ScreenState.Item("Carol", true),
                ScreenState.Item("David", true),
                ScreenState.Item("Ellen", true),
                ScreenState.Item("Zoe", true),
            )
        )
    }

    fun onDrop(sourceItem: String, targetItem: String) {
        _state.value = _state.value.copy(
            dialogMessage = "$sourceItem sent data to $targetItem",
            dropDialogVisible = true,
        )
    }

    fun closeDialog() {
        _state.value = _state.value.copy(dropDialogVisible = false)
    }

    data class ScreenState(
        val items: List<Item> = emptyList(),
        val dropDialogVisible: Boolean = false,
        val dialogMessage: String = "",
    ) {
        data class Item(val text: String, val enabled: Boolean)
    }
}