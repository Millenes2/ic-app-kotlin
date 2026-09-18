package com.example.ic_app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ic_app.ui.theme.Ic_appTheme
import com.example.ic_app.viewmodel.PerfilSalvarEvento
import com.example.ic_app.viewmodel.PerfilUiState
import com.example.ic_app.viewmodel.PerfilViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    nomeUsuario: String,
    dataNascimentoUsuario: String,
    pesoUsuario: String,
    objetivoUsuario: String,
    onVoltarHomeClick: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    var nome by remember { mutableStateOf(nomeUsuario) }
    var dataNascimento by remember { mutableStateOf(dataNascimentoUsuario) }
    var peso by remember { mutableStateOf(pesoUsuario) }
    var objetivo by remember { mutableStateOf(objetivoUsuario) }
    var mensagemErro by remember { mutableStateOf("") }
    val estado by viewModel.estado.collectAsState()
    val eventoSalvar by viewModel.eventoSalvar.collectAsState()
    val salvando = eventoSalvar is PerfilSalvarEvento.Salvando

    // Ao abrir a tela, tenta carregar o perfil real do backend (se houver
    // sessão ativa) para substituir o estado local só em memória.
    LaunchedEffect(Unit) {
        viewModel.carregar()
    }

    LaunchedEffect(estado) {
        when (val estadoAtual = estado) {
            is PerfilUiState.Sucesso -> {
                nome = estadoAtual.usuario.nome
                dataNascimento = estadoAtual.usuario.dataNascimento?.let { converterDataIsoParaBr(it) } ?: dataNascimento
                peso = estadoAtual.usuario.peso?.toString() ?: peso
                objetivo = estadoAtual.usuario.objetivoAtual ?: objetivo
            }
            is PerfilUiState.Erro -> mensagemErro = estadoAtual.mensagem
            else -> {}
        }
    }

    // Resultado do "Salvar alterações" (PATCH /perfil) é um evento separado de
    // `estado`: só ele deve navegar de volta à home ou exibir erro de
    // salvamento — `estado` também é usado por `carregar()` (ver acima), que
    // não deve disparar navegação.
    LaunchedEffect(eventoSalvar) {
        when (val eventoAtual = eventoSalvar) {
            is PerfilSalvarEvento.Salvo -> onVoltarHomeClick()
            is PerfilSalvarEvento.Erro -> mensagemErro = eventoAtual.mensagem
            else -> {}
        }
    }

    val fundo = Color(0xFFFFF9FB)
    val rosaPremium = Color(0xFFD86C9E)
    val campoFundo = Color(0xFFFFEEF5)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF666666)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
            .padding(22.dp)
    ) {
        TextButton(onClick = onVoltarHomeClick) {
            Text("Voltar", color = textoSecundario)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Perfil",
            color = textoPrincipal,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Revise e edite suas informações principais.",
            color = textoSecundario,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        PerfilCampo("Nome", nome, { nome = it }, campoFundo, rosaPremium)
        Spacer(modifier = Modifier.height(14.dp))

        PerfilCampo("Data de nascimento", dataNascimento, { dataNascimento = it }, campoFundo, rosaPremium)
        Spacer(modifier = Modifier.height(14.dp))

        PerfilCampo("Peso", peso, { peso = it }, campoFundo, rosaPremium)
        Spacer(modifier = Modifier.height(14.dp))

        PerfilCampo("Objetivo atual", objetivo, { objetivo = it }, campoFundo, rosaPremium)

        Spacer(modifier = Modifier.weight(1f))

        if (mensagemErro.isNotBlank()) {
            Text(
                text = mensagemErro,
                color = Color(0xFFB00020),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                mensagemErro = ""
                viewModel.salvar(nome, dataNascimento, peso, objetivo)
            },
            enabled = !salvando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = rosaPremium)
        ) {
            Text(
                text = if (salvando) "Salvando..." else "Salvar alterações",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Inverso de `PerfilRepository.formatoBackendIso`/`formatoTelaBr`: converte a
 * data ISO ("yyyy-MM-dd") devolvida por `GET /perfil` para o formato
 * "dd/MM/yyyy" já usado por `dataNascimentoUsuario`/`DataNascimentoScreen.kt`.
 * Em caso de valor inesperado, devolve a string original em vez de quebrar a
 * tela (fallback seguro).
 */
private fun converterDataIsoParaBr(dataIso: String): String {
    val formatoIso = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val formatoBr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        formatoBr.format(formatoIso.parse(dataIso)!!)
    } catch (erro: Exception) {
        dataIso
    }
}

@Composable
fun PerfilCampo(
    label: String,
    valor: String,
    onValueChange: (String) -> Unit,
    campoFundo: Color,
    rosaPremium: Color
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = rosaPremium,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = campoFundo,
            unfocusedContainerColor = campoFundo,
            cursorColor = rosaPremium
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    Ic_appTheme {
        PerfilScreen(
            nomeUsuario = "Millene",
            dataNascimentoUsuario = "13/06/2000",
            pesoUsuario = "65",
            objetivoUsuario = "Melhorar minha saúde mental",
            onVoltarHomeClick = {}
        )
    }
}