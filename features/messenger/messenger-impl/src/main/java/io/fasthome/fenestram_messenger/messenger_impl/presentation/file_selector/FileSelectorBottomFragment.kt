/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

class FileSelectorBottomFragment : BottomSheetFragmentWithButton(
    FileSelectorFragment::class, config = Config(
        scale = Config.Scale.Percent(0.6f),
        canceledOnTouchOutside = true
    )
) {
}