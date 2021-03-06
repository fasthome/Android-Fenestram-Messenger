package io.fasthome.fenestram_messenger

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.terrakok.cicerone.NavigatorHolder
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.databinding.ActivityMainBinding
import io.fasthome.fenestram_messenger.mvi.ScreenLauncherImpl
import io.fasthome.fenestram_messenger.navigation.BackPressConsumer
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.util.doOnStartStop
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.viewModel

class MainActivity : AppCompatActivity() {
    private val fragmentContainerId = R.id.container

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val navigator by lazy { CustomAppNavigator(this, fragmentContainerId) }
    private val navigatorHolder: NavigatorHolder by inject()
    private val router: ContractRouter by inject()
    private val authFeature: AuthFeature by inject()
    private val vm: MainActivityViewModel by getKoin().viewModel(
        owner = { ViewModelOwner.from(this, this) },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            vm.onAppStarted()
//            handleIntent(intent)
        }

        lifecycle.doOnStartStop(onStart = vm::onViewActive, onStop = vm::onViewInactive)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(fragmentContainerId)
        if (fragment is BackPressConsumer && fragment.onBackPressed()) {
            return
        }
        router.exit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {

    }
}