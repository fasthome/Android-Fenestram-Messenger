/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.component.file_selector

class FileSelectorBottomFragment : BottomSheetFragmentWithButton(
    FileSelectorFragment::class, config = Config(
        scale = Config.Scale.Percent(0.6f),
        canceledOnTouchOutside = true
    )
) {
}