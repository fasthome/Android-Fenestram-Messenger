/**
 * Created by Dmitry Popov on 15.09.2022.
 */
package io.fasthome.fenestram_messenger

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    private companion object {
        const val SPLASH_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            delay(SPLASH_DELAY)
            openMain()
        }
    }

    private fun openMain() {
        val mainIntent = Intent(this, MainActivity::class.java)

        if (!intent.wasLaunchedFromRecent) {
            intent.action?.let(mainIntent::setAction)
            intent.data?.let(mainIntent::setData)
            intent.extras?.let(mainIntent::putExtras)
        }

        startActivity(mainIntent)
    }

    private val Intent.wasLaunchedFromRecent: Boolean
        get() = (flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY

}