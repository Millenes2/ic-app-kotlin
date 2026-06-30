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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange

@Composable
fun DataNascimentoScreen(
    modifier: Modifier = Modifier,
    onContinuarClick: (String) -> Unit,
    onPularClick: () -> Unit
) {
    var dataNascimento by remember { mutableStateOf(TextFieldValue("")) }
    var erro by remember { mutableStateOf("") }

    val fundo = Color(0xFFFFF7FA)
    val rosa = Color(0xFFE91E63)
    val rosaClaro = Color(0xFFF8BBD0)
    val campoFundo = Color(0xFFFFF0F5)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF777777)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Qual é sua data de nascimento?",
            color = textoPrincipal,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Digite sua data no formato dd/mm/aaaa.",
            color = textoSecundario,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = dataNascimento,
            onValueChange = { novoValor -> //o que a usuário digitar vai ser armazenado na variavel
                erro = ""

                val numeros = novoValor.text
                    .filter { it.isDigit() }
                    .take(8) //pega os 8 primeiros numeros

                val dataFormatada = when {
                    numeros.length <= 2 -> numeros //se tiver menos que 2 numeros, vai mostrar os numeros
                    numeros.length <= 4 -> numeros.substring(0, 2) + "/" + numeros.substring(2) //se tiver menos que 4 numeros, vai mostrar os numeros com /

                    else -> numeros.substring(0, 2) + "/" + numeros.substring(2, 4) + "/" + numeros.substring(4) //se tiver mais que 4 numeros, vai mostrar os numeros com /

                }

                dataNascimento = TextFieldValue(
                    text = dataFormatada,
                    selection = TextRange(dataFormatada.length)
                )
                },

            placeholder = {
                Text( "dd/mm/aaaa")

            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = campoFundo,
                focusedContainerColor = campoFundo,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = rosa,
                cursorColor = rosa
            )
        )

        if (erro.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = erro,
                color = rosa,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formato.isLenient = false

                try {
                    val data = formato.parse(dataNascimento.text)

                    val hoje = Calendar.getInstance()
                    val nascimento = Calendar.getInstance()
                    nascimento.time = data!!

                    var idade = hoje.get(Calendar.YEAR) - nascimento.get(Calendar.YEAR)

                    if (hoje.get(Calendar.DAY_OF_YEAR) < nascimento.get(Calendar.DAY_OF_YEAR)) {
                        idade--
                    }

                    if (idade < 12) {
                        erro = "Você precisa ter pelo menos 12 anos."
                    } else {
                        erro = ""
                        onContinuarClick(dataNascimento.text)
                    }
                } catch (e: Exception) {
                    erro = "Digite uma data válida."
                }
            },
            enabled = dataNascimento.text.isNotEmpty() && erro.isEmpty(),
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
                text = "Continuar",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onPularClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Pular",
                color = textoSecundario,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DataNascimentoScreenPreview() {
    Ic_appTheme {
        DataNascimentoScreen(
            onContinuarClick = {},
            onPularClick = {}
        )
    }
}