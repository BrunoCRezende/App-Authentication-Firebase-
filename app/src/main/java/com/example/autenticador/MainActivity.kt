package com.example.autenticador

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autenticador.ui.theme.AutenticadorTheme
import com.example.autenticador.ui.theme.Azul
import com.example.autenticador.ui.theme.Branco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            AuthenticationScreen(auth)
        }
    }
}

@Composable
fun AuthenticationScreen(auth: FirebaseAuth) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isLoginMode) {
                    loginUser(auth, email, password, context)
                } else {
                    registerUser(auth, email, password, context)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A6EA5)
            )
        ) {
            Text(text = if (isLoginMode) "Login" else "Registrar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { isLoginMode = !isLoginMode },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A6EA5)
            )
        ) {
            Text(
                text = if (isLoginMode) "Criar Conta" else "Fazer Login"
            )
        }
    }
}

fun loginUser(auth: FirebaseAuth, email: String, password: String, context: android.content.Context) {
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isBlank()) {
        Toast.makeText(context, "Email ou senha inválidos", Toast.LENGTH_SHORT).show()
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                when (task.exception) {
                    is FirebaseAuthInvalidUserException -> {
                        Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(context, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Erro de login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
}

fun registerUser(auth: FirebaseAuth, email: String, password: String, context: android.content.Context) {
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.length < 6) {
        Toast.makeText(context, "Email inválido ou senha muito curta", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Registro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                when (task.exception) {
                    is FirebaseAuthUserCollisionException -> {
                        Toast.makeText(context, "Este email já está em uso", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseAuthWeakPasswordException -> {
                        Toast.makeText(context, "Senha muito fraca", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Erro de registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
}
