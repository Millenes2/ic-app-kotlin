package com.example.ic_app.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.ic_app.components.ObjetivoCard
import com.example.ic_app.ui.theme.Ic_appTheme

@Composable
fun ObjetivoScreen(
    modifier: Modifier = Modifier,
    onContinuarClick: (String) -> Unit,
    onPularClick: () -> Unit
) {
    var objetivoSelecionado by remember { mutableStateOf("") }

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
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Como podemos ajudar você?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textoPrincipal,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Escolha a opção que mais combina com o que você busca agora.",
                fontSize = 16.sp,
                color = textoSecundario,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            ObjetivoCard(
                titulo = "Monitorar meu ciclo",
                icone = "\uD83D\uDDD3\uFE0F",
                selecionado = objetivoSelecionado == "Monitorar meu ciclo",
                onClick = { objetivoSelecionado = "Monitorar meu ciclo" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ObjetivoCard(
                titulo = "Entender meu corpo",
                icone = "\uD83E\uDEC2",
                selecionado = objetivoSelecionado == "Entender meu corpo",
                onClick = { objetivoSelecionado = "Entender meu corpo" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ObjetivoCard(
                titulo = "Engravidar",
                icone = "\uD83E\uDD30",
                selecionado = objetivoSelecionado == "Engravidar",
                onClick = { objetivoSelecionado = "Engravidar" }
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            ObjetivoCard(
                titulo = "Acompanhar minha Gestação",
                icone = "\uD83D\uDC76\uD83C\uDFFB",
                selecionado = objetivoSelecionado == "Acompanhar minha Gestação",
                onClick = { objetivoSelecionado = "Acompanhar minha Gestação" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ObjetivoCard(
                titulo = "Melhorar minha Saúde Mental",
                icone = "\uD83D\uDC86\uD83C\uDFFC\u200D♀\uFE0F",
                selecionado = objetivoSelecionado == "Melhorar minha Saúde Mental",
                onClick = { objetivoSelecionado = "Melhorar minha Saúde Mental" }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onContinuarClick(objetivoSelecionado) },
                enabled = objetivoSelecionado.isNotBlank(),
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
fun ObjetivoScreenPreview() {
    Ic_appTheme {
        ObjetivoScreen(
            onContinuarClick = {},
            onPularClick = {}
        )
    }
}
