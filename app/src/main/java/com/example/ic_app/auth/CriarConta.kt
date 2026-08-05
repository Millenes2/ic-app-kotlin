package com.example.ic_app.auth

import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ic_app.ui.theme.Ic_appTheme


@Composable
fun CriarConta(
    modifier: Modifier = Modifier,
    onCadastrarClick: () -> Unit,
    onVoltarClick: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
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
                    text = "Criar conta",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoPrincipal
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Comece a acompanhar seu ciclo de forma simples.",
                    color = textoSecundario,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        mensagemErro = ""
                    },
                    placeholder = {
                        Text("Seu nome completo", color = Color(0xFFBDBDBD))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = campoFundo,
                        focusedContainerColor = campoFundo,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = rosa,
                        cursorColor = rosa
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        mensagemErro = ""
                    },
                    placeholder = {
                        Text("exemplo@email.com", color = Color(0xFFBDBDBD))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = campoFundo,
                        focusedContainerColor = campoFundo,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = rosa,
                        cursorColor = rosa
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = {
                        senha = it
                        mensagemErro = ""
                    },
                    placeholder = {
                        Text("Senha", color = Color(0xFFBDBDBD))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = campoFundo,
                        focusedContainerColor = campoFundo,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = rosa,
                        cursorColor = rosa
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = {
                        confirmarSenha = it
                        mensagemErro = ""
                    },
                    placeholder = {
                        Text("Confirmar senha", color = Color(0xFFBDBDBD))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    visualTransformation = PasswordVisualTransformation(),
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
                        if (nome.isBlank()) {
                            mensagemErro = "Preencha o nome"
                        } else if (email.isBlank()) {
                            mensagemErro = "Preencha o email" //colocar o negocio do @

                        } else if (senha.isBlank()) {
                            mensagemErro = "Preencha a senha"
                        } else if (confirmarSenha.isBlank()) {
                            mensagemErro = "Confirme a senha"
                        } else if (senha.length < 6) {
                            mensagemErro = "A senha deve ter pelo menos 6 caracteres"
                        } else if (senha != confirmarSenha) {
                            mensagemErro = "As senhas não coincidem"
                        } else {
                            mensagemErro = ""

                            auth.createUserWithEmailAndPassword(email, senha)
                                .addOnSuccessListener {
                                    onCadastrarClick()
                                }
                                .addOnFailureListener { erro ->
                                    mensagemErro = erro.message ?: "Erro ao criar conta"
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rosa
                    )
                ) {
                    Text(
                        text = "Criar conta",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ) //criar um botão para voltar para home
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = onVoltarClick
                ) {
                    Text(
                        text = "Já tenho uma conta",
                        color = rosa,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CriarContaPreview() {
    Ic_appTheme {
        CriarConta(
            onCadastrarClick = {},
            onVoltarClick = {}
        )
    }
}