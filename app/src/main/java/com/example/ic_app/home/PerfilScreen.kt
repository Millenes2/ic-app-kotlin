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
import com.example.ic_app.ui.theme.Ic_appTheme

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    nomeUsuario: String,
    dataNascimentoUsuario: String,
    pesoUsuario: String,
    objetivoUsuario: String,
    onVoltarHomeClick: () -> Unit
) {
    var nome by remember { mutableStateOf(nomeUsuario) }
    var dataNascimento by remember { mutableStateOf(dataNascimentoUsuario) }
    var peso by remember { mutableStateOf(pesoUsuario) }
    var objetivo by remember { mutableStateOf(objetivoUsuario) }

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

        Button(
            onClick = onVoltarHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = rosaPremium)
        ) {
            Text(
                text = "Salvar alterações",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
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