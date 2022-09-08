package io.fasthome.fenestram_messenger.presentation.base

import android.app.Activity
import kotlin.reflect.KClass

interface AppFeature {

    /**
     * Класс стартовой [Activity] приложения.
     */
    val startActivityClazz: KClass<out Activity>
}