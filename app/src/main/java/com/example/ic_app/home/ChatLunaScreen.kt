package com.example.ic_app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ic_app.ui.theme.Ic_appTheme

data class MensagemChat(
    val texto: String,
    val enviadaPelaUsuario: Boolean
)

@Composable
fun ChatLunaScreen(
    modifier: Modifier = Modifier,
    nomeUsuario: String = "Usuária",
    onVoltarHomeClick: () -> Unit
) {
    var textoDigitado by remember { mutableStateOf("") }

    var mensagens by remember {
        mutableStateOf(
            listOf(
                MensagemChat(
                    texto = "Olá, $nomeUsuario. Eu sou a Luna. Você pode me contar como está se sentindo hoje.",
                    enviadaPelaUsuario = false
                )
            )
        )
    }

    val fundo = Color(0xFFFFF9FB)
    val rosaPremium = Color(0xFFD86C9E)
    val rosaClaro = Color(0xFFFFEEF5)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF666666)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onVoltarHomeClick
        ) {
            Text(
                text = "Voltar",
                color = textoSecundario
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Chat com a Luna",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = textoPrincipal
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Converse em linguagem natural sobre ciclo, sintomas, humor e bem-estar.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = textoSecundario
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mensagens) { mensagem ->
                BalaoMensagem(
                    texto = mensagem.texto,
                    enviadaPelaUsuario = mensagem.enviadaPelaUsuario,
                    rosaPremium = rosaPremium,
                    rosaClaro = rosaClaro
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = textoDigitado,
            onValueChange = { textoDigitado = it },
            placeholder = {
                Text("Ex: Estou com cólica e cansada hoje")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = rosaPremium,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = rosaPremium
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (textoDigitado.isNotBlank()) {
                    val textoUsuario = textoDigitado

                    mensagens = mensagens + MensagemChat(
                        texto = textoUsuario,
                        enviadaPelaUsuario = true
                    )

                    mensagens = mensagens + MensagemChat(
                        texto = gerarRespostaSimulada(textoUsuario),
                        enviadaPelaUsuario = false
                    )

                    textoDigitado = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = rosaPremium
            )
        ) {
            Text(
                text = "Enviar",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun BalaoMensagem(
    texto: String,
    enviadaPelaUsuario: Boolean,
    rosaPremium: Color,
    rosaClaro: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (enviadaPelaUsuario) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (enviadaPelaUsuario) rosaPremium else rosaClaro
            ),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = texto,
                modifier = Modifier.padding(16.dp),
                color = if (enviadaPelaUsuario) Color.White else Color(0xFF1F1F1F),
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
    }
}

fun gerarRespostaSimulada(texto: String): String {
    val textoMinusculo = texto.lowercase()

    return when {
        "cólica" in textoMinusculo || "colica" in textoMinusculo -> {
            "Entendi. Posso registrar cólica no seu histórico de hoje e acompanhar se esse sintoma aparece em outros dias do ciclo."
        }

        "cansada" in textoMinusculo || "cansaço" in textoMinusculo -> {
            "Obrigada por me contar. Posso registrar cansaço como sinal de hoje para acompanhar sua energia ao longo da semana."
        }

        "triste" in textoMinusculo || "ansiosa" in textoMinusculo -> {
            "Sinto muito que você esteja assim. Posso registrar esse estado emocional para acompanhar seu bem-estar com mais cuidado."
        }

        "menstruação" in textoMinusculo || "menstruacao" in textoMinusculo -> {
            "Certo. Posso usar essa informação para ajudar no acompanhamento do seu ciclo e melhorar suas próximas previsões."
        }

        else -> {
            "Entendi. Posso guardar essa informação como parte do seu registro de hoje para acompanhar sua jornada com mais contexto."
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatLunaScreenPreview() {
    Ic_appTheme {
        ChatLunaScreen(
            nomeUsuario = "Millene",
            onVoltarHomeClick = {}
        )
    }
}