package com.example.ic_app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ic_app.ui.theme.Ic_appTheme
import java.util.Calendar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    nomeUsuario: String = "Usuária",
    objetivoUsuario: String = "Geral",
    onRegistrarHojeClick: () -> Unit,
    onChatClick: () -> Unit,
    onCalendarioClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onRelatoriosClick: () -> Unit = {},
    onLoginClick: () -> Unit
) {
    val fundo = Color(0xFFFBF9FB)
    val primary = Color(0xFFD86C9E)
    val lavender = Color(0xFFFCEAF2)
    val surfaceLow = Color(0xFFF5F3F5)
    val textoPrincipal = Color(0xFF1B1C1D)
    val textoSecundario = Color(0xFF6F6F6F)

    val saudacao = saudacaoPorHorario()

    Scaffold(
        containerColor = fundo,
        bottomBar = {
            LunaPremiumBottomBar(
                primary = primary,
                onHomeClick = {},
                onCalendarioClick = onCalendarioClick,
                onChatClick = onChatClick,
                onRelatoriosClick = onRelatoriosClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(fundo)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            HomeTopBar(
                nomeUsuario = nomeUsuario,
                saudacao = saudacao,
                primary = primary,
                textoSecundario = textoSecundario
            )

            Spacer(modifier = Modifier.height(22.dp))

            HealthChips(
                primary = primary,
                surfaceLow = surfaceLow,
                textoSecundario = textoSecundario
            )

            Spacer(modifier = Modifier.height(22.dp))

            CyclePremiumCard(
                primary = primary,
                surfaceLow = surfaceLow,
                textoSecundario = textoSecundario,
                onClick = onCalendarioClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            LuneteCard(
                primary = primary,
                lavender = lavender,
                textoPrincipal = textoPrincipal,
                textoSecundario = textoSecundario,
                onClick = onChatClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            QuickAccessSection(
                primary = primary,
                surfaceLow = surfaceLow,
                textoPrincipal = textoPrincipal,
                textoSecundario = textoSecundario,
                onCicloClick = onCalendarioClick,
                onSintomasClick = onRegistrarHojeClick,
                onHumorClick = onRegistrarHojeClick,
                onRelatoriosClick = onRelatoriosClick,
                onHistoricoClick = onRelatoriosClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            AiInsightCard(
                primary = primary,
                surfaceLow = surfaceLow,
                textoSecundario = textoSecundario,
                onClick = onChatClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            LoginCard(
                primary = primary,
                textoPrincipal = textoPrincipal,
                textoSecundario = textoSecundario,
                onLoginClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LoginCard(
    primary: Color,
    textoPrincipal: Color,
    textoSecundario: Color,
    onLoginClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Salve seus dados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textoPrincipal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Entre na sua conta para manter seu histórico, registros e conversas com a Lunete.",
                fontSize = 14.sp,
                color = textoSecundario,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                Text("Fazer login", color = Color.White)
            }
        }
    }
}

@Composable
fun HomeTopBar(
    nomeUsuario: String,
    saudacao: String,
    primary: Color,
    textoSecundario: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFFCEAF2)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🌙", fontSize = 25.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$saudacao, $nomeUsuario 👋",
                color = primary,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Como você está se sentindo hoje?",
                color = textoSecundario,
                fontSize = 13.sp
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F3F5)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔔", fontSize = 20.sp)
        }
    }
}

@Composable
fun HealthChips(
    primary: Color,
    surfaceLow: Color,
    textoSecundario: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HealthChip(
            dotColor = primary,
            text = "Humor: Estável",
            background = Color(0xFFFFEEF5),
            textColor = Color(0xFF8A4A68)
        )

        HealthChip(
            dotColor = Color(0xFF74696D),
            text = "Energia: Média",
            background = Color(0xFFFCEAF2),
            textColor = Color(0xFF5B5155)
        )

        HealthChip(
            dotColor = Color(0xFF787584),
            text = "Sintomas: Leves",
            background = surfaceLow,
            textColor = textoSecundario
        )
    }
}

@Composable
fun HealthChip(
    dotColor: Color,
    text: String,
    background: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CyclePremiumCard(
    primary: Color,
    surfaceLow: Color,
    textoSecundario: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(surfaceLow),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SEU CICLO",
                            color = textoSecundario,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )

                        Text(
                            text = "Dia 14",
                            color = primary,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "de 28 dias",
                            color = textoSecundario,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Período fértil previsto",
                color = primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Próxima menstruação em 12 dias",
                color = textoSecundario,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Estimativas informativas. O Luna não substitui " +
                        "avaliação de profissional de saúde e não deve ser " +
                        "usado como método contraceptivo.",
                color = textoSecundario,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun LuneteCard(
    primary: Color,
    lavender: Color,
    textoPrincipal: Color,
    textoSecundario: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = lavender),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.65f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✨", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "IA Ativa",
                    color = primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Converse com a Lunete",
                color = textoPrincipal,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sua assistente inteligente para dúvidas, registros e orientações personalizadas.",
                color = textoSecundario,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                Text(
                    text = "Conversar com a IA",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun QuickAccessSection(
    primary: Color,
    surfaceLow: Color,
    textoPrincipal: Color,
    textoSecundario: Color,
    onCicloClick: () -> Unit,
    onSintomasClick: () -> Unit,
    onHumorClick: () -> Unit,
    onRelatoriosClick: () -> Unit,
    onHistoricoClick: () -> Unit
) {
    Text(
        text = "ACESSO RÁPIDO",
        color = textoSecundario,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessItem("📅", "Ciclo", surfaceLow, textoPrincipal, Modifier.weight(1f), onCicloClick)
            QuickAccessItem("🩺", "Sintomas", surfaceLow, textoPrincipal, Modifier.weight(1f), onSintomasClick)
            QuickAccessItem("😊", "Humor", surfaceLow, textoPrincipal, Modifier.weight(1f), onHumorClick)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessItem("📊", "Relatórios", surfaceLow, textoPrincipal, Modifier.weight(1f), onRelatoriosClick)
            QuickAccessItem("🕘", "Histórico", surfaceLow, textoPrincipal, Modifier.weight(1f), onHistoricoClick)
        }
    }
}

@Composable
fun QuickAccessItem(
    emoji: String,
    title: String,
    background: Color,
    textoPrincipal: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 19.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = textoPrincipal,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AiInsightCard(
    primary: Color,
    surfaceLow: Color,
    textoSecundario: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "INSIGHTS DA IA",
            color = textoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Ver todos",
            color = primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onClick() }
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceLow),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(primary.copy(alpha = 0.10f))
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "PERSONALIZADO",
                        color = primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Hoje, 09:41",
                    color = textoSecundario,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Seu humor esteve mais sensível nos últimos dias. Registrar sintomas pode ajudar a identificar padrões específicos do seu ciclo.",
                color = textoSecundario,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Saber mais →",
                color = primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LunaPremiumBottomBar(
    primary: Color,
    onHomeClick: () -> Unit,
    onCalendarioClick: () -> Unit,
    onChatClick: () -> Unit,
    onRelatoriosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = onHomeClick,
            icon = { Text("🏠", fontSize = 20.sp) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = primary,
                selectedIconColor = primary,
                indicatorColor = Color(0xFFFFEEF5)
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = onCalendarioClick,
            icon = { Text("📅", fontSize = 20.sp) },
            label = { Text("Calendário") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onChatClick,
            icon = { Text("✨", fontSize = 20.sp) },
            label = { Text("Lunete") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onRelatoriosClick,
            icon = { Text("📊", fontSize = 20.sp) },
            label = { Text("Relatórios") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onPerfilClick,
            icon = { Text("👤", fontSize = 20.sp) },
            label = { Text("Perfil") }
        )
    }
}

fun saudacaoPorHorario(): String {
    val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when (hora) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Ic_appTheme {
        HomeScreen(
            nomeUsuario = "Millene",
            objetivoUsuario = "Monitorar meu ciclo",
            onRegistrarHojeClick = {},
            onChatClick = {},
            onCalendarioClick = {},
            onPerfilClick = {},
            onRelatoriosClick = {},
            onLoginClick = {}
        )
    }
}