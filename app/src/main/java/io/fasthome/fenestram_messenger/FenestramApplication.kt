/**
 * Created by Dmitry Popov on 01.07.2022.
 */
package io.fasthome.fenestram_messenger

import android.app.Application
import android.os.StrictMode
import io.fasthome.fenestram_messenger.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FenestramApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        builder.detectFileUriExposure()
        StrictMode.setVmPolicy(builder.build())
        startKoin {
            allowOverride(false)
            androidContext(this@FenestramApplication)
            modules(AppModule())
        }
    }

}