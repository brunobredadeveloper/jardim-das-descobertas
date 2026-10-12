package dev.breda.garden

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.*
import dev.breda.garden.domain.*

fun Token.label() =
    when (this) {
        Token.CAT -> "Gato"
        Token.BIRD -> "Passarinho"
        Token.BUNNY -> "Coelhinho"
        Token.DOG -> "Cachorro"
        Token.COW -> "Vaca"
        Token.BLUE -> "Azul"
        Token.YELLOW -> "Amarelo"
        Token.GREEN -> "Verde"
        Token.CIRCLE -> "Círculo"
        Token.TRIANGLE -> "Triângulo"
        Token.SQUARE -> "Quadrado"
        else -> requireNotNull(letter).toString()
    }

fun Challenge.prompt() =
    when (activity) {
        Activity.ANIMALS -> "Vamos encontrar o ${target.label().lowercase()}?"
        Activity.ANIMAL_SOUNDS -> "De quem é esse som?"
        Activity.LETTERS -> "Vamos encontrar a letra ${target.label()}?"
        Activity.COLORS ->
            "Encontre a flor ${if (target == Token.YELLOW) "amarela" else target.label().lowercase()}."
        Activity.SHAPES -> "Vamos encaixar o ${target.label().lowercase()}?"
    }

@Composable
fun GardenApp(model: GardenViewModel) {
    val state by model.ui.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val speech = remember(context) { SpeechGuide(context) }
    val animalAudio = remember(context) { AnimalAudioPlayer(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(speech, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                speech.stop()
                animalAudio.stop()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            speech.close()
            animalAudio.close()
        }
    }
    LaunchedEffect(
        state.screen,
        state.session?.index,
        state.session?.feedback,
        state.settings.sound,
        state.session?.challenge?.target,
        state.selectedLetter,
    ) {
        animalAudio.stop()
        speech.stop()
        if (!state.settings.sound) {
            speech.stop()
            return@LaunchedEffect
        }
        val text =
            when (state.screen) {
                GardenScreen.WELCOME -> "Olá! Vamos brincar no Jardim das Descobertas?"
                GardenScreen.HOME ->
                    "Escolha uma brincadeira. Letras, sons de animais, bichinhos, flores ou formas."
                GardenScreen.PARENTS -> null
                GardenScreen.ALPHABET -> Alphabet.entry(state.selectedLetter).narration
                GardenScreen.PLAY ->
                    when (state.session?.feedback) {
                        Feedback.FOUND ->
                            if (state.session?.challenge?.activity == Activity.ANIMAL_SOUNDS)
                                "Você encontrou! É ${if(state.session?.challenge?.target == Token.COW) "a" else "o"} ${state.session?.challenge?.target?.label()?.lowercase()}."
                            else "Você encontrou! Que descoberta bonita!"
                        Feedback.TRY_ANOTHER ->
                            if (state.session?.challenge?.activity == Activity.ANIMAL_SOUNDS) null
                            else "Vamos olhar de novo? Você pode tentar outro."
                        Feedback.FINISHED -> "Nosso jardim ganhou três flores! Vamos brincar mais?"
                        else ->
                            state.session?.challenge?.let {
                                if (it.activity == Activity.ANIMAL_SOUNDS) null
                                else if (it.activity == Activity.LETTERS)
                                    "Encontre a letra ${Alphabet.entry(it.target).spokenName}."
                                else it.prompt()
                            }
                    }
            }
        text?.let(speech::speak)
        val session = state.session
        if (
            state.screen == GardenScreen.PLAY &&
                session?.challenge?.activity == Activity.ANIMAL_SOUNDS &&
                session.feedback != Feedback.FOUND &&
                session.feedback != Feedback.FINISHED
        )
            animalAudio.play(session.challenge.target)
    }
    var gate by rememberSaveable { mutableStateOf(false) }
    var leaving by rememberSaveable { mutableStateOf(false) }
    BackHandler(
        state.screen == GardenScreen.PLAY ||
            state.screen == GardenScreen.PARENTS ||
            state.screen == GardenScreen.ALPHABET
    ) {
        if (state.screen == GardenScreen.PLAY) leaving = true else model.enter()
    }
    MaterialTheme(
        colorScheme =
            lightColorScheme(
                primary = Forest,
                onPrimary = Cream,
                secondary = Color(0xFFCB7853),
                background = Meadow,
                surface = Cream,
                onSurface = Forest,
            )
    ) {
        Box(Modifier.fillMaxSize().background(Meadow).safeDrawingPadding()) {
            GardenScenery(Modifier.fillMaxSize())
            AnimatedContent(
                state.screen,
                transitionSpec = {
                    fadeIn(tween(if (state.settings.reducedMotion) 0 else 250)) togetherWith
                        fadeOut(tween(if (state.settings.reducedMotion) 0 else 180))
                },
                label = "garden-route",
            ) { screen ->
                when (screen) {
                    GardenScreen.WELCOME -> Welcome(state.settings.reducedMotion, model::enter)
                    GardenScreen.HOME ->
                        Home(
                            state.settings.reducedMotion,
                            model::start,
                            model::alphabet,
                            { gate = true },
                        )
                    GardenScreen.PLAY ->
                        state.session?.let {
                            Play(
                                it,
                                state.settings.reducedMotion,
                                state.settings.sound &&
                                    (it.challenge.activity == Activity.ANIMAL_SOUNDS ||
                                        speech.available),
                                {
                                    if (it.challenge.activity == Activity.ANIMAL_SOUNDS) {
                                        speech.stop()
                                        animalAudio.play(it.challenge.target)
                                    } else
                                        speech.speak(
                                            if (it.challenge.activity == Activity.LETTERS)
                                                "Encontre a letra ${Alphabet.entry(it.challenge.target).spokenName}."
                                            else it.challenge.prompt()
                                        )
                                },
                                { token ->
                                    if (
                                        it.challenge.activity != Activity.ANIMAL_SOUNDS ||
                                            state.settings.sound
                                    )
                                        model.choose(token)
                                },
                                model::next,
                                {
                                    if (state.session?.feedback == Feedback.FINISHED) model.enter()
                                    else leaving = true
                                },
                                animalAudio.status,
                            )
                        }
                    GardenScreen.ALPHABET ->
                        AlphabetScreen(
                            state.selectedLetter,
                            state.settings.reducedMotion,
                            speech.available && state.settings.sound,
                            { token ->
                                if (token == state.selectedLetter && state.settings.sound)
                                    speech.speak(Alphabet.entry(token).narration)
                                else model.selectLetter(token)
                            },
                            { speech.speak(Alphabet.entry(state.selectedLetter).narration) },
                            { model.start(Activity.LETTERS) },
                            model::enter,
                        )
                    GardenScreen.PARENTS ->
                        Parents(state.settings, speech.available, model::settings, model::enter)
                }
            }
        }
        if (gate)
            ParentGate(
                onDismiss = { gate = false },
                onUnlocked = {
                    gate = false
                    model.parents()
                },
            )
        if (leaving)
            AlertDialog(
                onDismissRequest = { leaving = false },
                title = { Text("Vamos voltar ao jardim?") },
                text = { Text("Você pode escolher outra brincadeira.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            leaving = false
                            model.enter()
                        }
                    ) {
                        Text("Voltar ao jardim")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { leaving = false }) { Text("Continuar brincando") }
                },
            )
    }
}
