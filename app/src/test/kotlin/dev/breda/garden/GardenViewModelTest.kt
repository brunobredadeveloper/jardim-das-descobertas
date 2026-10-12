package dev.breda.garden

import dev.breda.garden.domain.*
import org.junit.Assert.*
import org.junit.Test

class GardenViewModelTest {
    private class MemorySettings : SettingsRepository {
        var saved = GardenSettings()

        override fun read() = saved

        override fun save(settings: GardenSettings) {
            saved = settings
        }
    }

    @Test
    fun settingsPersistAndReturningHomeClearsTheCurrentSession() {
        val settings = MemorySettings()
        val model = GardenViewModel(GardenGame(GardenCatalog()), settings)
        model.enter()
        model.start(Activity.COLORS)
        assertEquals(GardenScreen.PLAY, model.ui.value.screen)
        model.settings(false, true)
        assertEquals(GardenSettings(false, true), settings.saved)
        model.enter()
        assertEquals(GardenScreen.HOME, model.ui.value.screen)
        assertNull(model.ui.value.session)
        val restored = GardenViewModel(GardenGame(GardenCatalog()), settings)
        assertEquals(settings.saved, restored.ui.value.settings)
    }
}
