package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonLime

class MainActivity : ComponentActivity() {
    private val viewModel: MotorideViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.currentUser.collectAsState()
                var showBlueprints by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Quick Spec Access Button at the bottom of the screens
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(vertical = 10.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { showBlueprints = !showBlueprints },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("specs_overlay_trigger")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "Specs Book",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Ver Blueprints & Arquitectura",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        if (currentUser == null) {
                            LoginScreenView(
                                viewModel = viewModel,
                                onLoginSuccess = {
                                    // Managed by Flow state automatically
                                }
                            )
                        } else {
                            when (currentUser!!.role) {
                                "CLIENT" -> ClientPortalView(viewModel = viewModel, user = currentUser!!)
                                "DRIVER" -> DriverPortalView(viewModel = viewModel, user = currentUser!!)
                                "ADMIN" -> AdminPortalView(viewModel = viewModel, user = currentUser!!)
                                else -> {
                                    // Fallback to role selection
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Rol Desconocido", color = Color.White)
                                    }
                                }
                            }
                        }

                        // Overlay for Portal Specs Blueprint View
                        if (showBlueprints) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .testTag("specs_overlay_root")
                            ) {
                                PortalSpecsView(
                                    onDismiss = { showBlueprints = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
