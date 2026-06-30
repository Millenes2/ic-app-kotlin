package com.example.ic_app.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ic_app.ui.theme.Ic_appTheme

@Composable
fun PesoScreen(
    modifier: Modifier = Modifier,
    onContinuarClick: (String) -> Unit,
    onPularClick: () -> Unit
) {
    var peso by remember { mutableStateOf("") }

    val fundo = Color(0xFFFFF7FA)
    val flamingo = Color(0xFFE91E63)
    val flamingoClaro = Color(0xFFF8BBD0)
    val campoFundo = Color(0xFFFFF0F5)
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
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sobre seu corpo",
                fontSize = 18.sp,
                color = textoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Qual é seu peso atual?",
                color = textoPrincipal,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Essa informação ajuda a personalizar recomendações de bem-estar no app.",
                color = textoSecundario,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = peso,
                onValueChange = { novoValor ->
                    peso = novoValor.filter { it.isDigit() || it == ',' || it == '.' }
                },
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Ex: 65",
                        color = Color(0xFFBDBDBD)
                    )
                },
                suffix = {
                    Text(
                        text = "kg",
                        color = textoSecundario,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = campoFundo,
                    focusedContainerColor = campoFundo,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = flamingo,
                    cursorColor = flamingo
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onContinuarClick(peso.trim()) },
                enabled = peso.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = flamingo,
                    disabledContainerColor = flamingoClaro
                )
            ) {
                Text(
                    text = "Continuar",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PesoScreenPreview() {
    Ic_appTheme {
        PesoScreen(
            onContinuarClick = {},
            onPularClick = {}
        )
    }
}