package dev.breda.garden

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.breda.garden.domain.*

class PreferenceSettings(context: Context) : SettingsRepository {
    private val preferences = context.getSharedPreferences("garden-settings", Context.MODE_PRIVATE)

    override fun read() =
        GardenSettings(
            preferences.getBoolean("sound", true),
            preferences.getBoolean("reduced-motion", false),
        )

    override fun save(settings: GardenSettings) {
        preferences
            .edit()
            .putBoolean("sound", settings.sound)
            .putBoolean("reduced-motion", settings.reducedMotion)
            .apply()
    }
}

class GardenFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val model = GardenViewModel(GardenGame(GardenCatalog()), PreferenceSettings(context))
        require(modelClass.isInstance(model))
        return model as T
    }
}
