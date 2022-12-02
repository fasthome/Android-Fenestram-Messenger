package io.fasthome.fenestram_messenger.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.terrakok.cicerone.NavigatorHolder
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import io.fasthome.fenestram_messenger.CustomAppNavigator
import io.fasthome.fenestram_messenger.R
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.databinding.ActivityMainBinding
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.navigation.BackPressConsumer
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.push_api.PushFeature
import io.fasthome.fenestram_messenger.ui.splash.SplashActivity
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.doOnStartStop
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.viewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val UPDATE_REQUEST_CODE = 1
    }

    private val fragmentContainerId = R.id.container

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val navigator by lazy { CustomAppNavigator(this, fragmentContainerId) }
    private val navigatorHolder: NavigatorHolder by inject()
    private val router: ContractRouter by inject()
    private val authFeature: AuthFeature by inject()
    private val pushFeature: PushFeature by inject()

    private val vm: MainActivityViewModel by getKoin().viewModel(
        owner = { ViewModelOwner.from(this, this) },
    )

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        navigatorHolder.removeNavigator()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (savedInstanceState == null) {
            handleIntent(intent)
        }

        lifecycle.doOnStartStop(onStart = vm::onViewActive, onStop = vm::onViewInactive)

        vm.viewEvent.collectWhenStarted(this) { event->
            when(event) {
                is BaseViewEvent.ScreenEvent -> {
                    handleEvent(event.event)
                }
                is BaseViewEvent.ShowDialog -> Unit
                is BaseViewEvent.ShowMessage -> Unit
            }
        }
        checkUpdate()
    }

    private fun handleEvent(event: MainActivityEvent) {
        when(event){
            is MainActivityEvent.StartSplashEvent-> {
                startActivity(Intent(this, SplashActivity::class.java))
            }
        }
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

    private fun checkUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    UPDATE_REQUEST_CODE
                )
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        val pushClickResult = pushFeature.handlePushClick(intent)
        if (pushClickResult != null)
            vm.onAppStarted(true, pushClickResult)
        else
            vm.onAppStarted(false, null)
    }
}