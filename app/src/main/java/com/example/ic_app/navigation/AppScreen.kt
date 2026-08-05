package com.example.ic_app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ic_app.auth.CriarConta
import com.example.ic_app.objetivos.engravidar.EngravidarScreen1
import com.example.ic_app.objetivos.engravidar.EngravidarScreen2
import com.example.ic_app.objetivos.engravidar.EngravidarScreen3
import com.example.ic_app.objetivos.entender_corpo.EntenderCorpoScreen1
import com.example.ic_app.objetivos.gestacao.GestacaoScreen1
import com.example.ic_app.objetivos.gestacao.GestacaoScreen2
import com.example.ic_app.objetivos.gestacao.GestacaoScreen3
import com.example.ic_app.home.HomeScreen
import com.example.ic_app.auth.LoginScreen
import com.example.ic_app.home.RegistrarHojeScreen
import com.example.ic_app.objetivos.regularidade.RegularidadeScreen
import com.example.ic_app.objetivos.regularidade.RegularidadeScreen2
import com.example.ic_app.objetivos.regularidade.RegularidadeScreen3
import com.example.ic_app.objetivos.saude_mental.SaudeMentalScreen1
import com.example.ic_app.objetivos.saude_mental.SaudeMentalScreen2
import com.example.ic_app.objetivos.saude_mental.SaudeMentalScreen3
import com.example.ic_app.onboarding.BemVindoScreen
import com.example.ic_app.onboarding.ConsentScreen
import com.example.ic_app.onboarding.DataNascimentoScreen
import com.example.ic_app.onboarding.NomeScreen
import com.example.ic_app.onboarding.ObjetivoScreen
import com.example.ic_app.onboarding.PesoScreen
import com.example.ic_app.home.ChatLunaScreen
import com.example.ic_app.home.CalendarioScreen
import com.example.ic_app.home.PerfilScreen

@Composable
fun AppScreen() {
    var telaAtual by remember { mutableStateOf("boas-vindas") }//tela inicial é o consentimento
    var nomeUsuario by remember { mutableStateOf("") }//guarda o nome que eu escrevi na nome
    var objetivoUsuario by remember { mutableStateOf("") }//guarda o objetivo que eu escolhi na tela de objetivo
    var dataNascimentoUsuario by remember { mutableStateOf("") }
    var pesoUsuario by remember { mutableStateOf("") }

    //Scaffold é um layout que contém um topo, um conteúdo e um rodapé
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->//innerPadding é o padding interno da tela
        when (telaAtual) {

            "boas-vindas" -> BemVindoScreen(
                modifier = Modifier.padding(innerPadding),
                onFinalizar = {
                    telaAtual = "consentimento" //tela de consentimento

                }
            )


            "consentimento" -> ConsentScreen(//tela de consentimento
                modifier = Modifier.padding(innerPadding),//adiciona o padding
                onContinuarClick = {//função que vai ser chamada quando eu clicar no botão
                    telaAtual = "data_nascimento"
                }
            )

            "data_nascimento" -> DataNascimentoScreen(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = { data ->//função que vai ser chamada quando eu clicar no botão
                    dataNascimentoUsuario = data//guarda a data que eu digitou
                    telaAtual = "peso"
                },
                onPularClick = {
                    telaAtual = "peso"
                }
            )

            "peso" -> PesoScreen(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = { peso ->
                    pesoUsuario = peso
                    telaAtual = "nome"
                },
                onPularClick = {
                    telaAtual = "nome"
                }
            )


            "nome" -> NomeScreen(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = { nomeDigitado ->
                    nomeUsuario = nomeDigitado
                    telaAtual = "objetivo"
                },
                onPularClick = {//função que vai ser chamada quando eu clicar no botão
                    nomeUsuario = "Usuária"//nome padrão
                    telaAtual = "objetivo"//tela de objetivo
                }
            )

            "registrar_hoje" -> RegistrarHojeScreen(
                modifier = Modifier.padding(innerPadding),
                onSalvarClick = {
                    telaAtual = "home"
                },
                onVoltarClick = {
                    telaAtual = "home"
                }
            )

            "objetivo" -> ObjetivoScreen(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = { objetivoEscolhido ->
                    objetivoUsuario = objetivoEscolhido

                    telaAtual = when (objetivoEscolhido) {
                        "Monitorar meu ciclo" -> "regularidade1"
                        "Acompanhar sintomas" -> "sintomas1"
                        "Entender meu corpo" -> "entender1"
                        "Melhorar meu bem-estar" -> "bemestar1"
                        "Engravidar" -> "engravidar1"
                        "Acompanhar minha gestação" -> "gestacao1"
                        "Melhorar minha Saúde Mental" -> "saudemental1"
                        else -> "home"
                    }
                },
                onPularClick = {
                    objetivoUsuario = "Geral"
                    telaAtual = "home"
                }
            )

            // Monitorar meu ciclo
            "regularidade1" -> RegularidadeScreen(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "regularidade2"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "regularidade2" -> RegularidadeScreen2(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "regularidade3"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "regularidade3" -> RegularidadeScreen3(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "home"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            // Entender meu corpo
            "entender1" -> EntenderCorpoScreen1(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "home"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            // Engravidar
            "engravidar1" -> EngravidarScreen1(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "engravidar2"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "engravidar2" -> EngravidarScreen2(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "engravidar3"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "engravidar3" -> EngravidarScreen3(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "home"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            // Acompanhar minha gestação
            "gestacao1" -> GestacaoScreen1(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "gestacao2"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "gestacao2" -> GestacaoScreen2(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "gestacao3"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "gestacao3" -> GestacaoScreen3(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "home"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            // Melhorar minha saúde mental
            "saudemental1" -> SaudeMentalScreen1(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "saudemental2"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "saudemental2" -> SaudeMentalScreen2(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "saudemental3"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "saudemental3" -> SaudeMentalScreen3(
                modifier = Modifier.padding(innerPadding),
                onContinuarClick = {
                    telaAtual = "home"
                },
                onPularClick = {
                    telaAtual = "home"
                }
            )

            "home" -> HomeScreen(
                modifier = Modifier.padding(innerPadding),
                nomeUsuario = nomeUsuario,
                objetivoUsuario = objetivoUsuario,
                onRegistrarHojeClick = {
                    telaAtual = "registrar_hoje"
                },
                onChatClick = {
                    telaAtual = "chat"
                },
                onCalendarioClick = {
                    telaAtual = "calendario"
                },
                onPerfilClick = {
                    telaAtual = "perfil"
                },
                onRelatoriosClick = {
                    telaAtual = "relatorios"
                },
                onLoginClick = {
                    telaAtual = "login"
                }
            )

            "chat" -> ChatLunaScreen(
                modifier = Modifier.padding(innerPadding),
                nomeUsuario = nomeUsuario,
                onVoltarHomeClick = {
                    telaAtual = "home"
                }
            )


            "calendario" -> CalendarioScreen(
                modifier = Modifier.padding(innerPadding),
                onVoltarHomeClick = {
                    telaAtual = "home"
                }
            )

            "perfil" -> PerfilScreen(
                modifier = Modifier.padding(innerPadding),
                nomeUsuario = nomeUsuario,
                dataNascimentoUsuario = dataNascimentoUsuario,
                pesoUsuario = pesoUsuario,
                objetivoUsuario = objetivoUsuario,
                onVoltarHomeClick = {
                    telaAtual = "home"
                }
            )

            "chat" -> ChatLunaScreen(
                modifier = Modifier.padding(innerPadding),
                nomeUsuario = nomeUsuario,
                onVoltarHomeClick = {
                    telaAtual = "home"
                }
            )

            "login" -> LoginScreen(
                modifier = Modifier.padding(innerPadding),
                onEntrarClick = {
                    telaAtual = "home"
                },
                onCriarContaClick = {
                    telaAtual = "criar"
                }
            )

            "criar" -> CriarConta(
                modifier = Modifier.padding(innerPadding),
                onCadastrarClick = {
                    telaAtual = "home"
                },
                onVoltarClick = {
                    telaAtual = "login"
                }
            )
        }
    }
}
