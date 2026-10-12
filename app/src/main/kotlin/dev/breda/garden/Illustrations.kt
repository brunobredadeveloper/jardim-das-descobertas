package dev.breda.garden

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import dev.breda.garden.domain.Token
import kotlin.math.*

val Forest = Color(0xFF244C3A)
val Cream = Color(0xFFFFFCF2)
val Meadow = Color(0xFFEAF8EC)
val Peach = Color(0xFFFFCFAB)

fun tokenColor(token: Token) =
    when (token) {
        Token.BLUE,
        Token.BIRD -> Color(0xFF70BFE8)
        Token.YELLOW,
        Token.CAT -> Color(0xFFFFC95A)
        Token.GREEN -> Color(0xFF75C991)
        Token.BUNNY -> Color(0xFFF3A6BA)
        Token.DOG -> Color(0xFFDDA976)
        Token.COW -> Color(0xFFE9E4D8)
        Token.CIRCLE -> Color(0xFFA5A0EC)
        Token.TRIANGLE -> Color(0xFFFFB278)
        Token.SQUARE -> Color(0xFF81CDBE)
        else -> Color(0xFFA5A0EC)
    }

@Composable
fun TokenArt(
    token: Token,
    modifier: Modifier = Modifier,
    outline: Boolean = false,
    motion: Boolean = true,
) {
    if (token.letter != null) {
        LetterArt(token, modifier)
        return
    }
    val transition = rememberInfiniteTransition(label = "gentle-bob")
    val phase =
        transition.animateFloat(
            0f,
            1f,
            infiniteRepeatable(tween(2800, easing = LinearEasing)),
            label = "phase",
        )
    Canvas(modifier) {
        val side = min(size.width, size.height)
        translate((size.width - side) / 2, (size.height - side) / 2) {
            scale(side / 200, side / 200, pivot = Offset.Zero) {
                translate(0f, if (motion) sin(phase.value * 2 * PI).toFloat() * 3 else 0f) {
                    drawToken(token, outline)
                }
            }
        }
    }
}

private fun DrawScope.drawToken(token: Token, outline: Boolean) {
    val tint = tokenColor(token)
    if (token in listOf(Token.BLUE, Token.YELLOW, Token.GREEN)) {
        repeat(6) { i ->
            val angle = i * PI / 3
            drawCircle(
                tint,
                35f,
                Offset(100 + cos(angle).toFloat() * 43, 100 + sin(angle).toFloat() * 43),
            )
        }
        drawCircle(Color(0xFFFFF1B7), 33f, Offset(100f, 100f))
        face(100f, 102f, 16f)
        return
    }
    if (token in listOf(Token.CIRCLE, Token.TRIANGLE, Token.SQUARE)) {
        val style =
            if (outline) Stroke(7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
            else Fill
        val color = if (outline) tint.copy(alpha = .55f) else tint
        when (token) {
            Token.CIRCLE -> drawCircle(color, 64f, Offset(100f, 100f), style = style)
            Token.TRIANGLE ->
                drawPath(
                    Path().apply {
                        moveTo(100f, 30f)
                        lineTo(171f, 159f)
                        lineTo(29f, 159f)
                        close()
                    },
                    color,
                    style = style,
                )
            else ->
                drawRoundRect(
                    color,
                    Offset(37f, 37f),
                    Size(126f, 126f),
                    CornerRadius(22f),
                    style = style,
                )
        }
        if (!outline) face(100f, 105f, 17f)
        return
    }
    drawOval(Forest.copy(alpha = .08f), Offset(34f, 169f), Size(132f, 17f))
    when (token) {
        Token.DOG -> {
            drawOval(Color(0xFFB67B50), Offset(23f, 54f), Size(52f, 104f))
            drawOval(Color(0xFFB67B50), Offset(125f, 54f), Size(52f, 104f))
            drawCircle(tint, 64f, Offset(100f, 109f))
            drawOval(Color(0xFFF7DBC0), Offset(61f, 110f), Size(78f, 58f))
            face(100f, 105f, 24f)
            drawOval(Forest, Offset(88f, 117f), Size(24f, 16f))
            drawOval(Color(0xFFF3A6BA), Offset(95f, 143f), Size(15f, 20f))
        }
        Token.COW -> {
            drawPath(
                Path().apply {
                    moveTo(54f, 56f)
                    lineTo(41f, 22f)
                    lineTo(73f, 50f)
                    close()
                },
                Color(0xFFE6C57A),
            )
            drawPath(
                Path().apply {
                    moveTo(146f, 56f)
                    lineTo(159f, 22f)
                    lineTo(127f, 50f)
                    close()
                },
                Color(0xFFE6C57A),
            )
            drawOval(tint, Offset(13f, 61f), Size(49f, 28f))
            drawOval(tint, Offset(138f, 61f), Size(49f, 28f))
            drawOval(tint, Offset(34f, 45f), Size(132f, 129f))
            drawOval(Forest.copy(alpha = .8f), Offset(46f, 60f), Size(36f, 47f))
            face(100f, 108f, 25f)
            drawOval(Color(0xFFF3B9BE), Offset(49f, 124f), Size(102f, 51f))
            drawOval(Color(0xFFB27583), Offset(75f, 140f), Size(9f, 14f))
            drawOval(Color(0xFFB27583), Offset(116f, 140f), Size(9f, 14f))
        }
        Token.CAT -> {
            drawPath(
                Path().apply {
                    moveTo(44f, 76f)
                    lineTo(36f, 25f)
                    lineTo(84f, 56f)
                    close()
                },
                tint,
            )
            drawPath(
                Path().apply {
                    moveTo(156f, 76f)
                    lineTo(164f, 25f)
                    lineTo(116f, 56f)
                    close()
                },
                tint,
            )
            drawCircle(tint, 67f, Offset(100f, 110f))
            drawPath(
                Path().apply {
                    moveTo(45f, 58f)
                    lineTo(43f, 39f)
                    lineTo(65f, 59f)
                    close()
                },
                Peach,
            )
            drawPath(
                Path().apply {
                    moveTo(155f, 58f)
                    lineTo(157f, 39f)
                    lineTo(135f, 59f)
                    close()
                },
                Peach,
            )
            face(100f, 110f, 25f)
            drawCircle(Color(0xFFE69778), 6f, Offset(100f, 125f))
            for (y in listOf(118f, 130f)) {
                drawLine(
                    Forest.copy(alpha = .6f),
                    Offset(41f, y - 2),
                    Offset(68f, y),
                    3f,
                    StrokeCap.Round,
                )
                drawLine(
                    Forest.copy(alpha = .6f),
                    Offset(132f, y),
                    Offset(159f, y - 2),
                    3f,
                    StrokeCap.Round,
                )
            }
        }
        Token.BUNNY -> {
            drawOval(tint, Offset(50f, 15f), Size(42f, 103f))
            drawOval(tint, Offset(108f, 15f), Size(42f, 103f))
            drawOval(Color(0xFFFFDDE3), Offset(62f, 26f), Size(18f, 62f))
            drawOval(Color(0xFFFFDDE3), Offset(120f, 26f), Size(18f, 62f))
            drawOval(tint, Offset(33f, 72f), Size(134f, 108f))
            face(100f, 120f, 24f)
            drawCircle(Color(0xFFFFDDE3), 14f, Offset(62f, 139f))
            drawCircle(Color(0xFFFFDDE3), 14f, Offset(138f, 139f))
        }
        Token.BIRD -> {
            drawCircle(tint, 67f, Offset(100f, 105f))
            drawOval(Color(0xFFDFF1F4), Offset(67f, 101f), Size(65f, 70f))
            drawOval(Color(0xFF529ACC), Offset(18f, 104f), Size(50f, 38f))
            drawOval(Color(0xFF529ACC), Offset(132f, 104f), Size(50f, 38f))
            drawCircle(Forest, 7f, Offset(77f, 88f))
            drawCircle(Forest, 7f, Offset(123f, 88f))
            drawPath(
                Path().apply {
                    moveTo(87f, 105f)
                    lineTo(113f, 105f)
                    lineTo(100f, 126f)
                    close()
                },
                Color(0xFFFFB754),
            )
            drawLine(Color(0xFFE3A04C), Offset(77f, 169f), Offset(72f, 182f), 5f, StrokeCap.Round)
            drawLine(Color(0xFFE3A04C), Offset(123f, 169f), Offset(128f, 182f), 5f, StrokeCap.Round)
        }
        else -> Unit
    }
}

private fun DrawScope.face(x: Float, y: Float, gap: Float) {
    drawCircle(Forest, 6f, Offset(x - gap, y - 8))
    drawCircle(Forest, 6f, Offset(x + gap, y - 8))
    drawArc(
        Forest,
        0f,
        180f,
        false,
        Offset(x - 11, y + 3),
        Size(22f, 13f),
        style = Stroke(4f, cap = StrokeCap.Round),
    )
}

@Composable
fun GardenScenery(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawRect(Brush.verticalGradient(listOf(Color(0xFFDDF2F3), Meadow)))
        val w = size.width
        val h = size.height
        drawCircle(Color(0xFFFFE3A0), w * .105f, Offset(w * .84f, h * .16f))
        fun cloud(x: Float, y: Float, r: Float) {
            drawCircle(Color.White.copy(alpha = .8f), r, Offset(x, y))
            drawCircle(Color.White.copy(alpha = .8f), r * .7f, Offset(x - r, y + r * .2f))
            drawCircle(Color.White.copy(alpha = .8f), r * .7f, Offset(x + r, y + r * .2f))
        }
        cloud(w * .22f, h * .12f, w * .065f)
        cloud(w * .61f, h * .21f, w * .045f)
        drawOval(Color(0xFFCFEAD4), Offset(-w * .5f, h * .68f), Size(w * 2, h * .7f))
        drawOval(Color(0xFFB9DFBC), Offset(w * .35f, h * .78f), Size(w, h * .5f))
        repeat(8) { i ->
            val x = w * (i + .5f) / 8
            val y = h * (.9f + (i % 2) * .035f)
            drawLine(Color(0xFF8BB98C), Offset(x, y), Offset(x, y - 16), 4f)
            repeat(5) { j ->
                val a = j * 2 * PI / 5
                drawCircle(
                    if (i % 2 == 0) Peach else Color(0xFFF6C6D7),
                    7f,
                    Offset(x + cos(a).toFloat() * 7, y - 20 + sin(a).toFloat() * 7),
                )
            }
            drawCircle(Color(0xFFFFF1B7), 5f, Offset(x, y - 20))
        }
    }
}
