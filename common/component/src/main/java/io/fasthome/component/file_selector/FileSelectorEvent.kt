/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.component.file_selector

sealed interface FileSelectorEvent {
    object MaxImagesReached: FileSelectorEvent
    class ClearAdapterSelect(var checkedCursor: Int): FileSelectorEvent
}