package dev.breda.garden

import androidx.lifecycle.ViewModel
import dev.breda.garden.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GardenScreen {
    WELCOME,
    HOME,
    PLAY,
    ALPHABET,
    PARENTS,
}

data class GardenUiState(
    val screen: GardenScreen = GardenScreen.WELCOME,
    val session: PlaySession? = null,
    val settings: GardenSettings = GardenSettings(),
    val selectedLetter: Token = Token.A,
)

class GardenViewModel(private val game: GardenGame, private val preferences: SettingsRepository) :
    ViewModel() {
    private val state = MutableStateFlow(GardenUiState(settings = preferences.read()))
    val ui = state.asStateFlow()

    fun enter() {
        state.value = state.value.copy(screen = GardenScreen.HOME, session = null)
    }

    fun start(activity: Activity) {
        state.value = state.value.copy(screen = GardenScreen.PLAY, session = game.start(activity))
    }

    fun alphabet() {
        state.value = state.value.copy(screen = GardenScreen.ALPHABET, session = null)
    }

    fun selectLetter(token: Token) {
        Alphabet.entry(token)
        state.value = state.value.copy(selectedLetter = token)
    }

    fun choose(token: Token) {
        state.value.session?.let {
            state.value = state.value.copy(session = game.choose(it, token))
        }
    }

    fun next() {
        state.value.session?.let { state.value = state.value.copy(session = game.next(it)) }
    }

    fun parents() {
        state.value = state.value.copy(screen = GardenScreen.PARENTS, session = null)
    }

    fun settings(
        sound: Boolean = state.value.settings.sound,
        reducedMotion: Boolean = state.value.settings.reducedMotion,
    ) {
        val settings = GardenSettings(sound, reducedMotion)
        preferences.save(settings)
        state.value = state.value.copy(settings = settings)
    }
}
