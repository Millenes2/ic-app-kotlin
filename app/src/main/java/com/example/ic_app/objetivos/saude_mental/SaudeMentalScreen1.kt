package com.example.ic_app.objetivos.saude_mental

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ic_app.ui.theme.Ic_appTheme

@Composable
fun SaudeMentalScreen1(
    modifier: Modifier = Modifier,
    onContinuarClick: (String) -> Unit,
    onPularClick: () -> Unit
) {
    var opcaoSelecionada by remember { mutableStateOf("") }

    val fundo = Color(0xFFFFF7FA)
    val rosa = Color(0xFFE91E63)
    val rosaClaro = Color(0xFFF8BBD0)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF777777)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
    ) {
        TextButton(
            onClick = onPularClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 16.dp)
        ) {
            Text(
                text = "Pular",
                color = textoSecundario,
                fontSize = 16.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "O que você gostaria de melhorar no seu bem-estar emocional?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textoPrincipal,
                lineHeight = 38.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Escolha a opção que mais combina com o que você sente hoje.",
                fontSize = 16.sp,
                color = textoSecundario,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            OpcaoSaudeMentalCard(
                titulo = "Ansiedade",
                icone = "\uD83D\uDE30",
                selecionado = opcaoSelecionada == "Ansiedade",
                onClick = { opcaoSelecionada = "Ansiedade" }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcaoSaudeMentalCard(
                titulo = "Estresse do dia a dia",
                icone = "\uD83E\uDD2F",
                selecionado = opcaoSelecionada == "Estresse do dia a dia",
                onClick = { opcaoSelecionada = "Estresse do dia a dia" }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcaoSaudeMentalCard(
                titulo = "Autoestima",
                icone = "✨",
                selecionado = opcaoSelecionada == "Autoestima",
                onClick = { opcaoSelecionada = "Autoestima" }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcaoSaudeMentalCard(
                titulo = "Equilíbrio emocional",
                icone = "\uD83E\uDDD8",
                selecionado = opcaoSelecionada == "Equilíbrio emocional",
                onClick = { opcaoSelecionada = "Equilíbrio emocional" }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onContinuarClick(opcaoSelecionada) },
                enabled = opcaoSelecionada.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = rosa,
                    disabledContainerColor = rosaClaro
                )
            ) {
                Text(
                    text = "Avançar",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun OpcaoSaudeMentalCard(
    titulo: String,
    icone: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val rosa = Color(0xFFE91E63)
    val rosaFundoCard = Color(0xFFFFF0F5)
    val cinzaFundo = Color(0xFFF2EEF1)
    val textoPrincipal = Color(0xFF1F1F1F)

    val corFundo = if (selecionado) rosaFundoCard else cinzaFundo
    val corBorda = if (selecionado) rosa else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = corFundo
        ),
        border = BorderStroke(1.dp, corBorda)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icone,
                    fontSize = 22.sp,
                    color = rosa,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textoPrincipal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SaudeMentalScreen1Preview() {
    Ic_appTheme {
        SaudeMentalScreen1(
            onContinuarClick = {},
            onPularClick = {}
        )
    }
}