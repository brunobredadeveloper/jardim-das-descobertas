package dev.breda.garden

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import dev.breda.garden.domain.*

@Composable
internal fun ParentGate(onDismiss: () -> Unit, onUnlocked: () -> Unit) {
    var answer by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Área de responsáveis") },
        text = {
            Column {
                Text("Para abrir as configurações, quanto é 8 + 7?")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    answer,
                    { answer = it.filter(Char::isDigit).take(2) },
                    label = { Text("Resposta") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onUnlocked, enabled = answer == "15") {
                Text("Abrir configurações")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Voltar") } },
    )
}

@Composable
internal fun Parents(
    settings: GardenSettings,
    voice: Boolean,
    onSettings: (Boolean, Boolean) -> Unit,
    onHome: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Para quem cuida", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(
            "Um jardim para explorar juntos, sem cadastro, anúncios, coleta de dados ou pagamentos.",
            fontSize = 18.sp,
        )
        Surface(color = Cream, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Voz e sons de animais", Modifier.weight(1f), fontSize = 18.sp)
                    Switch(settings.sound, { onSettings(it, settings.reducedMotion) })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Movimento reduzido", Modifier.weight(1f), fontSize = 18.sp)
                    Switch(settings.reducedMotion, { onSettings(settings.sound, it) })
                }
            }
        }
        Text(
            if (voice) "Voz em português disponível no aparelho."
            else
                "O aparelho precisa de uma voz offline em português para narrar. As letras e instruções também aparecem na tela. Os sons de animais já estão no app e não dependem da voz do Android.",
            fontSize = 17.sp,
        )
        if (!voice)
            OutlinedButton(
                onClick = {
                    runCatching {
                        context.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
                    }
                }
            ) {
                Text("Configurar voz no Android")
            }
        Text("Sugestão para brincar juntos", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(
            "Depois das flores, procurem algo azul pela casa. Depois dos bichinhos, imitem os animais. A brincadeira pode continuar fora da tela.",
            fontSize = 18.sp,
        )
        Text(
            "Os desafios não avaliam desenvolvimento e não registram desempenho. O acesso por conta matemática é apenas uma barreira simples, não uma autenticação.",
            fontSize = 14.sp,
            color = Forest.copy(alpha = .7f),
        )
        Text("Créditos dos sons", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "Gato: Dan Crosby / Dcrosby — Meow.ogg\nCachorro: Amada44 — Barking_of_a_dog.ogg\nVaca: Secretlondon — Mudchute_cow_1.ogg\n\nWikimedia Commons. Licença CC BY-SA 3.0. Arquivos originais sem edição; volume reduzido na reprodução.\ncreativecommons.org/licenses/by-sa/3.0/\nOrigem: commons.wikimedia.org/wiki/File: seguida pelo nome de cada arquivo acima.",
            fontSize = 14.sp,
        )
        Spacer(Modifier.height(10.dp))
        BigButton("Voltar ao jardim", Icons.Rounded.Home, onHome)
        Text("breda_dev  •  Jardim das Descobertas v0.2.0", fontSize = 13.sp)
    }
}
