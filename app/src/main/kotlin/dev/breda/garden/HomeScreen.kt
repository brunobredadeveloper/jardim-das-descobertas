package dev.breda.garden

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import dev.breda.garden.domain.*

@Composable
internal fun Home(
    reducedMotion: Boolean,
    onStart: (Activity) -> Unit,
    onAlphabet: () -> Unit,
    onParents: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Olá, exploradora!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text("O que vamos descobrir?", fontSize = 17.sp)
            }
            IconButton(
                onClick = onParents,
                modifier = Modifier.size(56.dp).background(Cream, CircleShape),
            ) {
                Icon(Icons.Rounded.Lock, contentDescription = "Área de responsáveis")
            }
        }
        ActivityCard(
            "Alfabeto",
            "Letras de A até Z",
            Token.A,
            Color(0xFFE5E3FF),
            reducedMotion,
            onAlphabet,
        )
        ActivityCard(
            "Sons dos bichinhos",
            "Ouça e encontre o amigo",
            Token.DOG,
            Color(0xFFD7EFE7),
            reducedMotion,
        ) {
            onStart(Activity.ANIMAL_SOUNDS)
        }
        ActivityCard(
            "Bichinhos",
            "Encontre um amigo",
            Token.CAT,
            Color(0xFFFFE6C4),
            reducedMotion,
        ) {
            onStart(Activity.ANIMALS)
        }
        ActivityCard(
            "Flores coloridas",
            "Vamos combinar cores",
            Token.YELLOW,
            Color(0xFFFFE4EC),
            reducedMotion,
        ) {
            onStart(Activity.COLORS)
        }
        ActivityCard(
            "Formas divertidas",
            "Cada forma tem seu lugar",
            Token.CIRCLE,
            Color(0xFFE7E5FF),
            reducedMotion,
        ) {
            onStart(Activity.SHAPES)
        }
        Text(
            "Sem pressa. Do seu jeitinho.",
            Modifier.fillMaxWidth().padding(10.dp),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            color = Forest.copy(alpha = .7f),
        )
    }
}

@Composable
private fun ActivityCard(
    title: String,
    subtitle: String,
    token: Token,
    color: Color,
    reducedMotion: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier =
            Modifier.fillMaxWidth().heightIn(min = 130.dp).semantics {
                contentDescription = "$title. $subtitle"
            },
        shape = RoundedCornerShape(30.dp),
        color = color,
        shadowElevation = 3.dp,
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            TokenArt(token, Modifier.size(90.dp), motion = !reducedMotion)
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Icon(Icons.Rounded.ArrowForward, contentDescription = null)
            }
        }
    }
}
