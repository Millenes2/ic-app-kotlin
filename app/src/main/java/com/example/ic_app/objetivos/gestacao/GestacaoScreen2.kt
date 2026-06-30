package com.example.ic_app.objetivos.gestacao

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GestacaoScreen2(
    modifier: Modifier = Modifier,
    onContinuarClick: (String) -> Unit,
    onPularClick: () -> Unit
) {
    var opcaoSelecionada by remember { mutableStateOf("") }
    val rosa = Color(0xFFE91E63)
    val rosaClaro = Color(0xFFF8BBD0)

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFFFF7FA))) {
        TextButton(onClick = onPularClick, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            Text("Pular", color = Color.Gray)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = "O que você mais deseja acompanhar?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            val opcoes = listOf(
                "Desenvolvimento do bebê",
                "Mudanças no meu corpo",
                "Minha saúde e bem-estar",
                "Organização da rotina da gravidez"
            )
            opcoes.forEach { opcao ->
                OpcaoSimplesCard(titulo = opcao, selecionado = opcaoSelecionada == opcao, onClick = { opcaoSelecionada = opcao })
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onContinuarClick(opcaoSelecionada) },
                enabled = opcaoSelecionada.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = rosa, disabledContainerColor = rosaClaro)
            ) {
                Text("Avançar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
