package io.fasthome.component.personality_data

sealed interface PersonalityEvent {
    class VisibleName(val isVisible: Boolean) : PersonalityEvent
    class RunEdit(val edit: Boolean) : PersonalityEvent
}