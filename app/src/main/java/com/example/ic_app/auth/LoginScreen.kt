package com.example.ic_app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ic_app.ui.theme.Ic_appTheme
import com.example.ic_app.viewmodel.AuthUiState
import com.example.ic_app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onEntrarClick: () -> Unit,
    onCriarContaClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(estado) {
        when (val estadoAtual = estado) {
            is AuthUiState.Sucesso -> onEntrarClick()
            is AuthUiState.Erro -> mensagemErro = estadoAtual.mensagem
            else -> {}
        }
    }

    val fundo = Color(0xFFFFF7FA)
    val rosa = Color(0xFFE91E63)
    val rosaClaro = Color(0xFFF8BBD0)
    val campoFundo = Color(0xFFFFF0F5)
    val textoPrincipal = Color(0xFF1F1F1F)
    val textoSecundario = Color(0xFF777777)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fundo),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Entrar",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoPrincipal
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Acesse sua conta para salvar e acompanhar seus dados.",
                    color = textoSecundario,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Email",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textoSecundario,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        mensagemErro = ""
                    },
                    placeholder = {
                        Text(
                            text = "nome@exemplo.com",
                            color = Color(0xFFBDBDBD)
                        )
                    },
                    singleLine = true,
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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Senha",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textoSecundario
                    )

                    TextButton(
                        onClick = { }
                    ) {
                        Text(
                            text = "Esqueci minha senha",
                            color = rosa,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = {
                        senha = it
                        mensagemErro = ""
                    },
                    placeholder = {
                        Text(
                            text = "Senha",
                            color = Color(0xFFBDBDBD)
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
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

                Spacer(modifier = Modifier.height(16.dp))

                if (mensagemErro.isNotEmpty()) {
                    Text(
                        text = mensagemErro,
                        color = rosa,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            mensagemErro = "Preencha o email"
                        } else if (senha.isBlank()) {
                            mensagemErro = "Preencha a senha"
                        } else {
                            mensagemErro = ""
                            viewModel.login(email, senha)
                        }
                    },
                    enabled = estado != AuthUiState.Carregando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rosa
                    )
                ) {
                    if (estado == AuthUiState.Carregando) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.height(20.dp)
                        )
                    } else {
                        Text(
                            text = "Entrar",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(rosaClaro)
                    )

                    Text(
                        text = "  OU  ",
                        color = textoSecundario,
                        fontSize = 12.sp
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(rosaClaro)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = campoFundo
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        text = "Entrar com Google",
                        color = textoPrincipal,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Não tem conta? ",
                        color = textoSecundario,
                        fontSize = 14.sp
                    )

                    TextButton(onClick = onCriarContaClick) {
                        Text(
                            text = "Criar conta",
                            color = rosa,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Ic_appTheme {
        LoginScreen(
            onEntrarClick = {},
            onCriarContaClick = {}
        )
    }
}