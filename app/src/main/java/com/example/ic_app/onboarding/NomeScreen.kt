package com.example.ic_app.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun NomeScreen(
    modifier: Modifier = Modifier,
    onContinuarClick: (String) -> Unit,
    onPularClick: () -> Unit
) {
    var nome by remember { mutableStateOf("") }

    val fundo = Color(0xFFFFF7FA)
    val rosa = Color(0xFFE91E63)
    val rosaClaro = Color(0xFFF8BBD0)
    val campoFundo = Color(0xFFFFF0F5)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF777777)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
    ) {
        TextButton(
            onClick = onPularClick,//chama a função que vai para a tela de objetivo
            modifier = Modifier
                .align(Alignment.TopEnd)//alinha o botão no canto superior direito
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
                text = "Vamos nos conhecer",
                fontSize = 18.sp,
                color = textoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Como devemos te chamar?",
                color = textoPrincipal,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Seu nome ajuda a personalizar sua experiência no app.",
                color = textoSecundario,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = nome,//guarda o nome digitado
                onValueChange = { nome = it },//atualiza o nome digitado
                singleLine = true,//só uma linha
                placeholder = {//texto que aparece quando não tem nada digitado
                    Text(
                        text = "Digite seu nome",//texto que aparece quando não tem nada digitado
                        color = Color(0xFFBDBDBD)
                    )
                },
                shape = RoundedCornerShape(18.dp),//arredondamento
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(//cores
                    unfocusedContainerColor = campoFundo,//cor do fundo quando não esta selecionado
                    focusedContainerColor = campoFundo,//cor do fundo quando selecionado
                    unfocusedBorderColor = Color.Transparent,//cor da borda quando não esta selecionado
                    focusedBorderColor = rosa,//cor da borda quando selecionado
                    cursorColor = rosa//cor do cursor
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onContinuarClick(nome) },//passa o nome para a tela de objetivo e guarda no App screen
                enabled = nome.isNotBlank(),//só habilita o botão se o nome não estiver vazio
                modifier = Modifier//modifica o botão
                    .fillMaxWidth()//ocupa a tela inteira
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),//arredondamento
                elevation = ButtonDefaults.buttonElevation(4.dp),
                colors = ButtonDefaults.buttonColors(//cores do botão
                    containerColor = rosa, //cor do botão
                    disabledContainerColor = rosaClaro//cor do botão quando estiver desabilitado
                )
            ) {
                Text(
                    text = "Vamos lá",
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
fun NomeScreenPreview() {
    Ic_appTheme {
        NomeScreen(
            onContinuarClick = {},
            onPularClick = {}
        )
    }
}