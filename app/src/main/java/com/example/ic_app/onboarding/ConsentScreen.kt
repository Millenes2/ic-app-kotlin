package com.example.ic_app.onboarding

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ic_app.ui.theme.Ic_appTheme

@Composable //função composta
fun ConsentScreen(//função que vai ser composta
    modifier: Modifier = Modifier, // Modifier serve para configurar aparencia/posição/tamanho
    //toda vez temos que passar como parametro para devifinir , cor, altura e etc.
    onContinuarClick: () -> Unit
) {
    var consentimentoMarcado by remember { mutableStateOf(false) }
    var mostrarDialog by remember { mutableStateOf(false) }

    val fundoTela = Color(0xFFFFF7FA)
    val flamingo = Color(0xFFE91E63)
    val flamingoClaro = Color(0xFFF8BBD0)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF666666)

    Box(//box é uma caixa, serve para colocar um fundo, centralizar um conteudo, sobrepor emelmentos
        modifier = modifier //passa o modificador
            .fillMaxSize()//ocupa a tela inteira
            .background(fundoTela),//adiciona um fundo de cor
        contentAlignment = Alignment.Center //alinha o conteudo dentro do box
    ) {
        //Card é um quadrado central que vamos preencher informações
        Card(
            shape = RoundedCornerShape(28.dp),//deixa as bordas arrendodadas
            elevation = CardDefaults.cardElevation(8.dp),//cria sombra
            colors = CardDefaults.cardColors(//deixa o fundo branco
                containerColor = Color.White
            ),
            modifier = Modifier
                .padding(20.dp)//espaçamento
                .fillMaxWidth()//ocupa a tela inteira
        ) {
            Column(//organiza elementos um embaixo do outro
                modifier = Modifier.padding(24.dp),//espaçamento
                horizontalAlignment = Alignment.CenterHorizontally//alinha o conteudo
            ) {
                Text(
                    text = "Antes de continuar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoPrincipal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Para personalizar sua experiência, o app poderá usar informações sobre seu ciclo, sintomas e bem-estar.",
                    color = textoSecundario,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center//alinha o texto
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Esses dados serão usados apenas para adaptar o conteúdo do app e melhorar sua experiência.",
                    color = textoSecundario,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { mostrarDialog = true },
                            modifier = Modifier
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = flamingo
                        )
                ){


                    Text("Ver tipos de Dados")
                }
                if (mostrarDialog) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialog = false },
                        title = { Text("Tipos de Dados") },
                        text = {
                            Text(
                              text =  "O Luna utiliza as informações fornecidas por você para disponibilizar recursos de acompanhamento da saúde feminina, bem-estar, ciclo menstrual, fertilidade e gestação. \n\n" +
                                      "    USO DAS SUAS INFORMAÇÕES PESSOAIS NO LUNA \n" +
                                      "\n" +

                                      "    As informações registradas podem incluir: \n" +
                                      "\n" +
                                      "    • Dados de perfil. \n" +
                                      "    • Data de nascimento.\n" +
                                      "    • Peso e informações corporais.\n" +
                                      "    • Objetivos escolhidos no aplicativo.\n" +
                                      "    • Registros de humor e bem-estar.\n" +
                                      "    • Sintomas informados por você.\n" +
                                      "    • Observações pessoais registradas durante o uso.\n" +
                                      "    • Informações relacionadas ao ciclo menstrual, fertilidade e gestação.\n" +
                                      "\n" +
                                      "    Esses dados são utilizados para:\n" +
                                      "\n" +
                                      "    • Personalizar funcionalidades e conteúdos do aplicativo.\n" +
                                      "    • Gerar históricos e acompanhamentos ao longo do tempo.\n" +
                                      "    • Identificar padrões relacionados à sua saúde e bem-estar.\n" +
                                      "    • Apresentar informações compatíveis com seus objetivos.\n" +
                                      "    • Melhorar continuamente os recursos oferecidos pela plataforma.\n" +
                                      "\n" +
                                      "    O Luna não compartilha suas informações pessoais com outros usuários e utiliza os dados exclusivamente para as finalidades relacionadas ao funcionamento do aplicativo.\n" +
                                      "\n" +
                                      "    As informações registradas permanecem vinculadas à sua conta e podem ser utilizadas para manter seu histórico, acompanhar sua evolução e oferecer uma experiência mais personalizada.\n" +
                                      "\n" +
                                      "    Você permanece no controle das suas informações e poderá solicitar a exclusão dos seus dados conforme os recursos disponibilizados pela plataforma."
                            )

                        },


                        confirmButton = {
                            Button(
                                onClick = { mostrarDialog = false },
                                modifier = Modifier
                                    .height(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = flamingo
                                )
                            ) {

                                Text(
                                    text = "Fechar",
                                    color = Color.White
                                )
                            }


                            }
                    )

                }
                Spacer(modifier = Modifier.height(12.dp))

                Row( //organiza elementos um ao lado do outro
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = consentimentoMarcado,
                        onCheckedChange = { consentimentoMarcado = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = flamingo//cor do checkbox quando marcado
                        )
                    )

                    Column(
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            text = "Li e concordo com o uso dos meus dados.",
                            fontSize = 14.sp,
                            color = textoPrincipal,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Entendo que meus dados serão usados para personalizar minha experiência no app.",
                            fontSize = 12.sp,
                            color = textoSecundario,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onContinuarClick,
                    enabled = consentimentoMarcado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = flamingo,
                        disabledContainerColor = flamingoClaro
                    )
                ) {
                    Text(
                        text = "Continuar",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConsentScreenPreview() {
    Ic_appTheme {
        ConsentScreen(
            onContinuarClick = {}
        )
    }
}