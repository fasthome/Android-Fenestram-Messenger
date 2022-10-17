/**
 * Created by Dmitry Popov on 15.09.2022.
 */
package io.fasthome.fenestram_messenger.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.fasthome.fenestram_messenger.R
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    private companion object {
        const val SPLASH_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            delay(SPLASH_DELAY)
            finish()
        }
    }
}