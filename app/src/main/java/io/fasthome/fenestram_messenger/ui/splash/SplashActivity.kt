/**
 * Created by Dmitry Popov on 15.09.2022.
 */
package io.fasthome.fenestram_messenger.ui.splash

import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import io.fasthome.fenestram_messenger.databinding.ActivitySplashBinding
import io.fasthome.fenestram_messenger.uikit.theme.LightTheme
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import kotlinx.coroutines.delay

class SplashActivity : ThemeActivity() {

    private companion object {
        const val SPLASH_DELAY = 2000L
    }

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycleScope.launchWhenStarted {
            delay(SPLASH_DELAY)
            finish()
        }
    }

    override fun getStartTheme(): AppTheme {
        return LightTheme()
    }
    override fun syncTheme(appTheme: AppTheme) = with(binding) {
        appTheme as Theme
        appTheme.context = applicationContext
        root.background = appTheme.bg1Color().toDrawable()
        logo.setImageResource(appTheme.textImageLogoResource())
    }
}