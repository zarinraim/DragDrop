package com.zarinraim.dragdrop

/**
 * State of the draggable item
 *
 * Initiated - default state, when dragged over general content
 * Enabled - state over enabled drop target
 * Disabled - state over disabled drop target
 */
enum class DraggableItemState {
    Initiated,
    Enabled,
    Disabled,
}