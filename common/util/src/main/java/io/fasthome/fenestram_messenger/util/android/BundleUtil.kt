package io.fasthome.fenestram_messenger.util.android

import android.os.Bundle

fun bundleOf(block: Bundle.() -> Unit): Bundle = Bundle().apply(block)