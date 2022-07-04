package io.fasthome.fenestram_messenger

import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.androidx.AppNavigator
import io.fasthome.fenestram_messenger.navigation.ModalWindow
import io.fasthome.fenestram_messenger.navigation.model.CloseModalScreens
import io.fasthome.fenestram_messenger.util.hideKeyboard

class CustomAppNavigator(
    activity: FragmentActivity,
    containerId: Int,
    fragmentManager: FragmentManager = activity.supportFragmentManager,
    fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory
) : AppNavigator(activity, containerId, fragmentManager, fragmentFactory) {

    override fun applyCommands(commands: Array<out Command>) {
        activity.hideKeyboard(clearFocus = true)
        super.applyCommands(commands)
    }

    override fun applyCommand(command: Command) {
        when (command) {
            CloseModalScreens -> {
                fragmentManager.fragments
                    .reversed()
                    .takeWhile { it is ModalWindow }
                    .forEach { (it as ModalWindow).back() }
            }
            else -> super.applyCommand(command)
        }

        /**
         * Вызываем тут "executePendingTransactions" после применения каждой команды навигации,
         * чтобы экраны не мелькали, если последовательно открывается несколько экранов.
         * Например, чтобы при старте МП не мелькал главный экран перед показом экрана проверки ПИН.
         */
        fragmentManager.executePendingTransactions()
    }

    override fun back() {
        when (val fragment = fragmentManager.fragments.lastOrNull()) {
            is ModalWindow -> fragment.back()
            else -> super.back()
        }
    }

    private fun ModalWindow.back() {
        this@CustomAppNavigator.activity.lifecycleScope.launchWhenStarted {
            runCatching {
                dismiss()
                super.back()
            }
        }
    }
}