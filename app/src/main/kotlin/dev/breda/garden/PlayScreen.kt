package dev.breda.garden

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import dev.breda.garden.domain.*
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Play(
    session: PlaySession,
    reducedMotion: Boolean,
    sound: Boolean,
    onRepeat: () -> Unit,
    onChoose: (Token) -> Unit,
    onNext: () -> Unit,
    onHome: () -> Unit,
    audioStatus: AudioStatus = AudioStatus.IDLE,
) {
    if (session.feedback == Feedback.FINISHED) {
        Celebration(reducedMotion, onHome)
        return
    }
    val listening = session.challenge.activity == Activity.ANIMAL_SOUNDS
    val compact = listening || session.challenge.activity == Activity.LETTERS
    if (listening && !sound) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Rounded.VolumeOff, null, Modifier.size(96.dp))
            Text(
                "Essa brincadeira precisa de som",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                "Peça ajuda a quem cuida de você para ativar o som na área de responsáveis.",
                Modifier.padding(vertical = 24.dp),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            BigButton("Voltar ao jardim", Icons.Rounded.Home, onHome)
        }
        return
    }
    val foundScale by
        animateFloatAsState(
            if (session.feedback == Feedback.FOUND && !reducedMotion) 1.08f else 1f,
            spring(dampingRatio = .55f, stiffness = 180f),
            label = "discovery-pop",
        )
    var targetBounds by
        remember(session.index, session.challenge.activity) { mutableStateOf(Rect.Zero) }
    var dragging by remember(session.index) { mutableStateOf<Token?>(null) }
    var dragOffset by remember(session.index) { mutableStateOf(Offset.Zero) }
    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(if (compact) 14.dp else 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onHome,
                modifier = Modifier.size(56.dp).background(Cream, CircleShape),
            ) {
                Icon(Icons.Rounded.Home, contentDescription = "Voltar ao jardim")
            }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                repeat(3) { i ->
                    TokenArt(
                        if (i < session.flowers) Token.YELLOW else Token.GREEN,
                        Modifier.size(34.dp).graphicsLayer {
                            alpha = if (i < session.flowers) 1f else .3f
                        },
                        motion = false,
                    )
                }
            }
            IconButton(
                onClick = onRepeat,
                enabled = sound,
                modifier = Modifier.size(56.dp).background(Cream, CircleShape),
            ) {
                Icon(Icons.Rounded.VolumeUp, contentDescription = "Escutar novamente")
            }
        }
        Text(
            session.challenge.prompt(),
            fontSize = 26.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        if (listening && session.feedback != Feedback.FOUND) {
            Surface(
                onClick = onRepeat,
                shape = RoundedCornerShape(36.dp),
                color = Cream,
                modifier =
                    Modifier.size(140.dp).semantics { contentDescription = "Ouvir som do bichinho" },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (audioStatus == AudioStatus.LOADING)
                        CircularProgressIndicator(Modifier.size(48.dp))
                    else
                        Icon(
                            if (audioStatus == AudioStatus.PLAYING) Icons.Rounded.GraphicEq
                            else Icons.Rounded.PlayArrow,
                            null,
                            Modifier.size(72.dp),
                        )
                    Text(
                        if (audioStatus == AudioStatus.PLAYING) "Escutando…" else "Ouvir de novo",
                        fontSize = 16.sp,
                    )
                }
            }
            if (audioStatus == AudioStatus.ERROR)
                Text(
                    "Não foi possível tocar o som. Toque para tentar novamente.",
                    textAlign = TextAlign.Center,
                )
            else
                Text(
                    "Se não ouvir, ajuste o volume do celular",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
        } else {
            Surface(
                color = Cream.copy(alpha = .88f),
                shape = RoundedCornerShape(36.dp),
                modifier =
                    Modifier.size(if (compact) 140.dp else 155.dp)
                        .graphicsLayer {
                            scaleX = foundScale
                            scaleY = foundScale
                        }
                        .onGloballyPositioned { targetBounds = it.boundsInRoot() }
                        .semantics {
                            contentDescription = "Modelo: ${session.challenge.target.label()}"
                        },
            ) {
                TokenArt(
                    session.challenge.target,
                    Modifier.padding(10.dp).fillMaxSize(),
                    outline =
                        session.challenge.activity == Activity.SHAPES &&
                            session.feedback != Feedback.FOUND,
                    motion = !reducedMotion,
                )
            }
        }
        Text(
            if (session.challenge.activity == Activity.SHAPES) "Toque ou arraste para o encaixe"
            else "Toque no seu escolhido",
            fontSize = 16.sp,
        )
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3,
        ) {
            session.challenge.choices.forEach { token ->
                var origin by remember { mutableStateOf(Offset.Zero) }
                val drag =
                    if (
                        session.challenge.activity == Activity.SHAPES &&
                            session.feedback != Feedback.FOUND
                    )
                        Modifier.pointerInput(token, session.index, targetBounds) {
                            detectDragGestures(
                                onDragStart = {
                                    dragging = token
                                    dragOffset = Offset.Zero
                                },
                                onDragCancel = {
                                    dragging = null
                                    dragOffset = Offset.Zero
                                },
                                onDragEnd = {
                                    if (targetBounds.contains(origin + dragOffset)) onChoose(token)
                                    dragging = null
                                    dragOffset = Offset.Zero
                                },
                            ) { change, delta ->
                                change.consume()
                                dragOffset += delta
                            }
                        }
                    else Modifier
                Surface(
                    onClick = { onChoose(token) },
                    enabled = session.feedback != Feedback.FOUND,
                    modifier =
                        Modifier.padding(horizontal = 4.dp)
                            .width(100.dp)
                            .height(if (compact) 120.dp else 130.dp)
                            .onGloballyPositioned {
                                if (dragging != token) origin = it.boundsInRoot().center
                            }
                            .then(drag)
                            .zIndex(if (dragging == token) 2f else 0f)
                            .offset {
                                if (dragging == token)
                                    IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                                else IntOffset.Zero
                            }
                            .semantics { contentDescription = "Escolher ${token.label()}" },
                    shape = RoundedCornerShape(26.dp),
                    border =
                        BorderStroke(
                            3.dp,
                            if (
                                session.feedback == Feedback.FOUND &&
                                    token == session.challenge.target
                            )
                                Forest
                            else Color.Transparent,
                        ),
                    color = Cream,
                    shadowElevation = 3.dp,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        TokenArt(token, Modifier.size(88.dp), motion = !reducedMotion)
                        Text(token.label(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        AnimatedContent(
            session.feedback,
            transitionSpec = {
                fadeIn(tween(if (reducedMotion) 0 else 180)) togetherWith
                    fadeOut(tween(if (reducedMotion) 0 else 120))
            },
            label = "gentle-feedback",
        ) { feedback ->
            when (feedback) {
                Feedback.FOUND ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Você encontrou!",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Forest,
                        )
                        Spacer(Modifier.height(10.dp))
                        BigButton(
                            if (session.index == 2) "Ver nosso jardim" else "Outra descoberta",
                            Icons.Rounded.ArrowForward,
                            onNext,
                        )
                    }
                Feedback.TRY_ANOTHER ->
                    Surface(color = Color(0xFFFFE9CA), shape = RoundedCornerShape(22.dp)) {
                        Text(
                            "Vamos olhar de novo?\nVocê pode tentar outro.",
                            Modifier.padding(16.dp),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                else ->
                    Text(
                        "Estamos descobrindo juntos",
                        Modifier.padding(12.dp),
                        fontSize = 16.sp,
                        color = Forest.copy(alpha = .65f),
                    )
            }
        }
    }
}

@Composable
internal fun Celebration(reducedMotion: Boolean, onHome: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TokenArt(Token.BUNNY, Modifier.size(190.dp), motion = !reducedMotion)
        Text(
            "Olha nosso jardim!",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Row { repeat(3) { TokenArt(Token.YELLOW, Modifier.size(95.dp), motion = !reducedMotion) } }
        Text(
            "Três flores, muitas descobertas.\nFoi muito bom brincar com você!",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(26.dp))
        BigButton("Escolher outra brincadeira", Icons.Rounded.Home, onHome)
    }
}
