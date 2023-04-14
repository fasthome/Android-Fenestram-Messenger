package io.fasthome.fenestram_messenger

import io.fasthome.fenestram_messenger.presentation.base.AppFeature
import io.fasthome.fenestram_messenger.ui.main.MainActivity


class AppFeatureImpl : AppFeature {
    override val startActivityClazz = MainActivity::class
}