package dev.breda.garden

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import dev.breda.garden.domain.*

@Composable
internal fun LetterArt(token: Token, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            requireNotNull(token.letter).toString(),
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Forest,
        )
    }
}

@Composable
internal fun AlphabetScreen(
    selected: Token,
    reducedMotion: Boolean,
    sound: Boolean,
    onSelect: (Token) -> Unit,
    onRepeat: () -> Unit,
    onGame: () -> Unit,
    onHome: () -> Unit,
) {
    val entry = Alphabet.entry(selected)
    Column(
        Modifier.fillMaxSize().padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onHome, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Rounded.Home, "Voltar ao jardim")
            }
            Text(
                "Jardim das letras",
                Modifier.weight(1f),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = onRepeat, enabled = sound, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Rounded.VolumeUp, "Escutar a letra")
            }
        }
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Cream,
            modifier =
                Modifier.fillMaxWidth().semantics {
                    contentDescription = "${entry.letter} de ${entry.word}"
                },
        ) {
            Row(
                Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(entry.letter.toString(), fontSize = 72.sp, fontWeight = FontWeight.ExtraBold)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(entry.picture, fontSize = 48.sp)
                    Text(entry.word, fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Text("Toque nas letras para descobrir", fontSize = 16.sp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier =
                Modifier.weight(1f).fillMaxWidth().semantics {
                    contentDescription = "Alfabeto completo"
                },
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            items(Alphabet.entries, key = { it.letter }) { letter ->
                Surface(
                    onClick = { onSelect(letter.token) },
                    color = if (selected == letter.token) Forest else Cream,
                    contentColor = if (selected == letter.token) Cream else Forest,
                    shape = RoundedCornerShape(20.dp),
                    modifier =
                        Modifier.height(72.dp).semantics {
                            contentDescription = "Explorar letra ${letter.letter}"
                        },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            letter.letter.toString(),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        BigButton("Encontrar letras", Icons.Rounded.Search, onGame)
    }
}
