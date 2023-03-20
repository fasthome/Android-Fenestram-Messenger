package io.fasthome.component.theme

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.data.KeyValueStorage
import io.fasthome.fenestram_messenger.data.stored
import io.fasthome.fenestram_messenger.uikit.theme.DarkTheme
import io.fasthome.fenestram_messenger.uikit.theme.LightTheme
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import kotlinx.coroutines.withContext

class ThemeStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) {
    private val preferencesStorage = persistentStorageFactory.create("theme.prefs")

    private var theme: String? by preferencesStorage.stored("KEY_THEME")

    suspend fun getTheme(): Theme =
        withContext(DispatchersProvider.IO) {
            val themeInstance = when (theme) {
                "LightTheme" -> {
                    LightTheme()
                }
                "DarkTheme" -> {
                    DarkTheme()
                }
                else -> {
                    LightTheme()
                }
            }
            return@withContext themeInstance
        }

    suspend fun setTheme(themeInstance : Theme) =
        withContext(DispatchersProvider.IO) {
            theme = themeInstance.javaClass.simpleName
        }
}