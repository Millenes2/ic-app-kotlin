package com.example.ic_app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ic_app.ui.theme.Ic_appTheme

@Composable
fun CalendarioScreen(
    modifier: Modifier = Modifier,
    onVoltarHomeClick: () -> Unit
) {
    var diaSelecionado by remember { mutableStateOf("12") }
    var menstruacaoRegistrada by remember { mutableStateOf(false) }

    val fundo = Color(0xFFFFF9FB)
    val rosaPremium = Color(0xFFD86C9E)
    val rosaClaro = Color(0xFFFFEEF5)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF666666)

    val dias = listOf(
        "Seg" to "10",
        "Ter" to "11",
        "Qua" to "12",
        "Qui" to "13",
        "Sex" to "14",
        "Sáb" to "15",
        "Dom" to "16"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
            .padding(22.dp)
    ) {
        TextButton(onClick = onVoltarHomeClick) {
            Text(
                text = "Voltar",
                color = textoSecundario
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Calendário",
            color = textoPrincipal,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Acompanhe seu ciclo, registre menstruação e veja previsões importantes.",
            color = textoSecundario,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Junho",
                    color = textoPrincipal,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(dias) { dia ->
                        DiaCalendarioSelecionavel(
                            semana = dia.first,
                            numero = dia.second,
                            selecionado = diaSelecionado == dia.second,
                            rosaPremium = rosaPremium,
                            onClick = {
                                diaSelecionado = dia.second
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = rosaClaro),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Previsão do ciclo",
                    color = textoPrincipal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🌸 Próxima menstruação prevista em 14 dias",
                    color = textoPrincipal,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🔔 Alerta sugerido: 2 dias antes da previsão",
                    color = textoSecundario,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Registro do dia $diaSelecionado",
                    color = textoPrincipal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = menstruacaoRegistrada,
                        onCheckedChange = { menstruacaoRegistrada = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = rosaPremium
                        )
                    )

                    Text(
                        text = "Registrar menstruação neste dia",
                        color = textoPrincipal,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (menstruacaoRegistrada)
                        "Menstruação registrada para o dia $diaSelecionado."
                    else
                        "Nenhum registro menstrual neste dia.",
                    color = textoSecundario,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { menstruacaoRegistrada = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = rosaPremium
            )
        ) {
            Text(
                text = "Salvar registro menstrual",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DiaCalendarioSelecionavel(
    semana: String,
    numero: String,
    selecionado: Boolean,
    rosaPremium: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(50.dp)
            .background(
                color = if (selecionado) rosaPremium else Color(0xFFFFEEF5),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = semana,
            fontSize = 11.sp,
            color = if (selecionado) Color.White else Color(0xFF777777)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = numero,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (selecionado) Color.White else Color(0xFF1F1F1F)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarioScreenPreview() {
    Ic_appTheme {
        CalendarioScreen(
            onVoltarHomeClick = {}
        )
    }
}