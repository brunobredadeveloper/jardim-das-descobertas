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
import androidx.compose.ui.layout.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import dev.breda.garden.domain.*

@Composable
internal fun Welcome(reducedMotion: Boolean, onEnter: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(shape = RoundedCornerShape(50), color = Cream.copy(alpha = .85f)) {
            Text(
                "breda_dev  •  brincar e descobrir",
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Medium,
                color = Forest,
            )
        }
        Spacer(Modifier.height(12.dp))
        TokenArt(Token.BUNNY, Modifier.size(150.dp), motion = !reducedMotion)
        Text(
            "Jardim das\nDescobertas",
            fontSize = 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "Um mundo pequeno.\nMuitas descobertas.",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        BigButton("Vamos brincar!", Icons.Rounded.PlayArrow, onEnter)
        Spacer(Modifier.height(12.dp))
        Text("Para brincar juntinhos", fontSize = 15.sp, color = Forest.copy(alpha = .7f))
    }
}
