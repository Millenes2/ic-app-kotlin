package com.example.ic_app.objetivos.regularidade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun RegularidadeScreen2(
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
                text = "Quando foi sua última menstruação?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textoPrincipal,
                lineHeight = 38.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Isso nos ajuda a calcular seu ciclo com mais precisão.",
                fontSize = 16.sp,
                color = textoSecundario,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            OpcaoCard(
                titulo = "Hoje",
                icone = "\uD83D\uDCCD",
                selecionado = opcaoSelecionada == "Hoje",
                onClick = { opcaoSelecionada = "Hoje" }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcaoCard(
                titulo = "Nos últimos 3 dias",
                icone = "\uD83D\uDD5C",
                selecionado = opcaoSelecionada == "Nos últimos 3 dias",
                onClick = { opcaoSelecionada = "Nos últimos 3 dias" }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcaoCard(
                titulo = "Esta semana",
                icone = "\uD83D\uDDD3\uFE0F",
                selecionado = opcaoSelecionada == "Esta semana",
                onClick = { opcaoSelecionada = "Esta semana" }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcaoCard(
                titulo = "Não lembro",
                icone = "❔",
                selecionado = opcaoSelecionada == "Não lembro",
                onClick = { opcaoSelecionada = "Não lembro" }
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

@Preview(showBackground = true)
@Composable
fun RegularidadeScreen2Preview() {
    Ic_appTheme {
        RegularidadeScreen2(
            onContinuarClick = {},
            onPularClick = {}
        )
    }
}