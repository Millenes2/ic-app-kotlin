package com.example.ic_app.home

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ic_app.ui.theme.Ic_appTheme
import com.example.ic_app.viewmodel.RegistroDiarioUiState
import com.example.ic_app.viewmodel.RegistroDiarioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegistrarHojeScreen(
    modifier: Modifier = Modifier,
    onSalvarClick: () -> Unit,
    onVoltarClick: () -> Unit,
    viewModel: RegistroDiarioViewModel = viewModel()
) {
    var humorSelecionado by remember { mutableStateOf("") }
    var sintomaSelecionado by remember { mutableStateOf("") }
    var observacao by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(estado) {
        when (val estadoAtual = estado) {
            is RegistroDiarioUiState.Sucesso -> onSalvarClick()
            is RegistroDiarioUiState.Erro -> mensagemErro = estadoAtual.mensagem
            else -> {}
        }
    }

    val fundo = Color(0xFFFFF7FA)
    val flamingo = Color(0xFFE91E63)
    val flamingoClaro = Color(0xFFF8BBD0)
    val campoFundo = Color(0xFFFFF0F5)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF777777)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
            .padding(24.dp)
    ) {
        TextButton(onClick = onVoltarClick) {
            Text(
                text = "Voltar",
                color = textoSecundario
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Registrar hoje",
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            color = textoPrincipal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registre como você está se sentindo para acompanhar sua jornada.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = textoSecundario
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Como está seu humor?",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textoPrincipal
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RegistroEmojiItem("😊", "Bem", humorSelecionado == "Bem") {
                humorSelecionado = "Bem"
            }

            RegistroEmojiItem("😴", "Cansada", humorSelecionado == "Cansada") {
                humorSelecionado = "Cansada"
            }

            RegistroEmojiItem("😔", "Triste", humorSelecionado == "Triste") {
                humorSelecionado = "Triste"
            }

            RegistroEmojiItem("😠", "Irritada", humorSelecionado == "Irritada") {
                humorSelecionado = "Irritada"
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Sintoma principal",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textoPrincipal
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RegistroOpcaoCard("🤕", "Cólicas e dor", sintomaSelecionado == "Cólicas e dor") {
                sintomaSelecionado = "Cólicas e dor"
            }

            RegistroOpcaoCard("😵‍💫", "Alterações de humor", sintomaSelecionado == "Alterações de humor") {
                sintomaSelecionado = "Alterações de humor"
            }

            RegistroOpcaoCard("😴", "Cansaço", sintomaSelecionado == "Cansaço") {
                sintomaSelecionado = "Cansaço"
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Observação",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textoPrincipal
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = observacao,
            onValueChange = { observacao = it },
            placeholder = {
                Text(
                    text = "Escreva algo sobre seu dia...",
                    color = Color(0xFFBDBDBD)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = campoFundo,
                focusedContainerColor = campoFundo,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = flamingo,
                cursorColor = flamingo
            )
        )

        if (mensagemErro.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensagemErro,
                color = flamingo,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                mensagemErro = ""
                val dataHoje = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                viewModel.salvar(
                    data = dataHoje,
                    humor = humorSelecionado.ifBlank { null },
                    sintomaPrincipal = sintomaSelecionado.ifBlank { null },
                    observacao = observacao.ifBlank { null }
                )
            },
            enabled = (humorSelecionado.isNotBlank() || sintomaSelecionado.isNotBlank() || observacao.isNotBlank()) &&
                estado != RegistroDiarioUiState.Carregando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = flamingo,
                disabledContainerColor = flamingoClaro
            )
        ) {
            if (estado == RegistroDiarioUiState.Carregando) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.height(20.dp)
                )
            } else {
                Text(
                    text = "Salvar registro",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun RegistroEmojiItem(
    emoji: String,
    texto: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val flamingo = Color(0xFFE91E63)
    val textoSecundario = Color(0xFF666666)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(
                    color = if (selecionado) flamingo.copy(alpha = 0.12f) else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = texto,
            fontSize = 12.sp,
            color = if (selecionado) flamingo else textoSecundario,
            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RegistroOpcaoCard(
    emoji: String,
    titulo: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val flamingo = Color(0xFFE91E63)
    val rosaClaro = Color(0xFFFFF0F5)
    val cinzaFundo = Color(0xFFF2EEF1)
    val textoPrincipal = Color(0xFF1F1F1F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selecionado) rosaClaro else cinzaFundo
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (selecionado) flamingo else textoPrincipal
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun RegistrarHojeScreenPreview() {
    Ic_appTheme {
        RegistrarHojeScreen(
            onSalvarClick = {},
            onVoltarClick = {}
        )
    }
}