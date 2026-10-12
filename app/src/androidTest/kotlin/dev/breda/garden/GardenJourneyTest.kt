package dev.breda.garden

import android.os.ParcelFileDescriptor
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GardenJourneyTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    @Before
    fun stabilize() {
        ui.runOnIdle {
            ViewModelProvider(ui.activity)[GardenViewModel::class.java].settings(false, true)
        }
    }

    private fun capture(name: String) {
        ui.waitForIdle()
        val shell = InstrumentationRegistry.getInstrumentation().uiAutomation
        fun run(command: String) {
            ParcelFileDescriptor.AutoCloseInputStream(shell.executeShellCommand(command)).use {
                it.readBytes()
            }
        }
        run("mkdir -p /data/local/tmp/garden-preview")
        run("screencap -p /data/local/tmp/garden-preview/$name.png")
    }

    private fun click(text: String) {
        val node = ui.onNode(hasText(text) and hasClickAction())
        runCatching { node.assertIsDisplayed() }.onFailure { node.performScrollTo() }
        node.performClick()
    }

    @Test
    fun childCanCompleteColorsAndParentGateRequiresAnswer() {
        capture("01-welcome")
        click("Vamos brincar!")
        capture("02-home")
        click("Flores coloridas")
        capture("03-colors")
        repeat(3) { index ->
            val prompt =
                ui.onNode(hasText("Encontre a flor", substring = true))
                    .fetchSemanticsNode()
                    .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
                    .first()
                    .text
            val target =
                when {
                    prompt.contains("azul") -> "Azul"
                    prompt.contains("amarel") -> "Amarelo"
                    else -> "Verde"
                }
            ui.onNodeWithContentDescription("Escolher $target").performClick()
            ui.onNodeWithText("Você encontrou!").assertExists()
            click(if (index == 2) "Ver nosso jardim" else "Outra descoberta")
        }
        ui.onNodeWithText("Olha nosso jardim!").assertExists()
        capture("04-celebration")
        click("Escolher outra brincadeira")
        ui.onNodeWithContentDescription("Área de responsáveis").performClick()
        ui.onNodeWithText("Abrir configurações").assertIsNotEnabled()
        ui.onNodeWithText("Resposta").performTextInput("15")
        click("Abrir configurações")
        ui.onNodeWithText("Para quem cuida").assertExists()
    }

    @Test
    fun alphabetScrollsToZAndStartsAPlayableLetterRound() {
        click("Vamos brincar!")
        click("Alfabeto")
        ui.onNodeWithContentDescription("Alfabeto completo")
            .performScrollToNode(hasContentDescription("Explorar letra Z"))
        ui.onNodeWithContentDescription("Explorar letra Z").performClick()
        ui.onNodeWithContentDescription("Z de zebra").assertExists()
        capture("05-alphabet")
        click("Encontrar letras")
        val model = ViewModelProvider(ui.activity)[GardenViewModel::class.java]
        val target = model.ui.value.session!!.challenge.target.label()
        ui.onNodeWithContentDescription("Escolher $target").performClick()
        ui.onNodeWithText("Você encontrou!").assertExists()
        capture("06-letters")
    }

    @Test
    fun listeningNeedsSoundAndKeepsAnswerHiddenUntilFound() {
        click("Vamos brincar!")
        click("Sons dos bichinhos")
        ui.onNodeWithText("Essa brincadeira precisa de som").assertExists()
        val model = ViewModelProvider(ui.activity)[GardenViewModel::class.java]
        ui.runOnIdle { model.settings(true, true) }
        val target = model.ui.value.session!!.challenge.target.label()
        ui.onNodeWithContentDescription("Ouvir som do bichinho").assertExists()
        ui.onNodeWithContentDescription("Modelo: $target").assertDoesNotExist()
        capture("07-listening")
        ui.onNodeWithContentDescription("Escolher $target").performClick()
        ui.onNodeWithText("Você encontrou!").assertExists()
        ui.onNodeWithContentDescription("Modelo: $target").assertExists()
        capture("08-animal-found")
        ui.onNode(hasText("Outra descoberta") and hasClickAction())
            .assertIsDisplayed()
            .performClick()
        ui.onNodeWithContentDescription("Ouvir som do bichinho").assertExists()
        ui.onNode(hasContentDescription("Modelo:", substring = true)).assertDoesNotExist()
    }

    @Test
    fun shapesCanBeDraggedToTheModel() {
        click("Vamos brincar!")
        click("Formas divertidas")
        val prompt =
            ui.onNode(hasText("Vamos encaixar", substring = true))
                .fetchSemanticsNode()
                .config[androidx.compose.ui.semantics.SemanticsProperties.Text]
                .first()
                .text
        val target =
            when {
                prompt.contains("círculo") -> "Círculo"
                prompt.contains("triângulo") -> "Triângulo"
                else -> "Quadrado"
            }
        val piece = ui.onNodeWithContentDescription("Escolher $target")
        val source = piece.fetchSemanticsNode().boundsInRoot
        val destination =
            ui.onNodeWithContentDescription("Modelo: $target").fetchSemanticsNode().boundsInRoot
        piece.performTouchInput { swipe(center, center + destination.center - source.center, 700) }
        ui.onNodeWithText("Você encontrou!").assertExists()
    }
}
