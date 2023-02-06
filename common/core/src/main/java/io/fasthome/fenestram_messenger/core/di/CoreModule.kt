package io.fasthome.fenestram_messenger.core.di

import io.fasthome.fenestram_messenger.core.time.RealTimeProvider
import io.fasthome.fenestram_messenger.core.time.TimeProvider
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.util.view_action.BottomViewAction
import org.koin.dsl.module

object CoreModule{

    operator fun invoke() = module {
        factory (::RealTimeProvider) bindSafe TimeProvider::class

        single {
            BottomViewAction<String>()
        }
    }

}
