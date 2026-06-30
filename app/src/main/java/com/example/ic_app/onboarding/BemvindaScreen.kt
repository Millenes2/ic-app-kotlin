package com.example.ic_app.onboarding


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.ic_app.ui.theme.Ic_appTheme
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect




@Composable

fun BemVindoScreen(
    modifier : Modifier = Modifier,
    onFinalizar : () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinalizar()
    }

    val fundoTela = Color(0xFFFFF7FA)
    val textoPrincipal = Color(0xFF1F1F1F)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fundoTela)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Bem vinda ao Luna\n\n\n \uD83C\uDF15",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = textoPrincipal,
            textAlign = TextAlign.Center
        )

    }

}

@Preview(showBackground = true)
@Composable
fun BemvindaScreenPreview() {
    Ic_appTheme {
        ConsentScreen(
            onContinuarClick = {}
        )
    }
}