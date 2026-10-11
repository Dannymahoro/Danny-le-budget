package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DannyBottomNav
import com.example.ui.components.OnboardingConsentDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.FeedbackScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.MidnightBlue
import com.example.ui.theme.MidnightBlueDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureWhite
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DannyBudgetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val budgetViewModel: DannyBudgetViewModel = viewModel()
                DannyBudgetApp(viewModel = budgetViewModel)
            }
        }
    }
}

@Composable
fun DannyBudgetApp(
    viewModel: DannyBudgetViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val hasAcceptedGdpr by viewModel.hasAcceptedGdpr.collectAsState()
    val snackbarMessage by viewModel.snackBarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Secondary screen back handler
    BackHandler(enabled = currentScreen != AppScreen.CALCULATOR) {
        viewModel.handleBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBlue),
        bottomBar = {
            // Show bottom nav only on main top-level tabs
            if (currentScreen in listOf(
                    AppScreen.CALCULATOR,
                    AppScreen.HISTORY,
                    AppScreen.CATEGORIES,
                    AppScreen.SETTINGS
                )
            ) {
                DannyBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MidnightBlueDark,
                        contentColor = PureWhite,
                        actionColor = ElectricYellow
                    )
                }
            )
        },
        containerColor = MidnightBlue
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (currentScreen) {
                AppScreen.CALCULATOR -> CalculatorScreen(viewModel = viewModel)
                AppScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
                AppScreen.CATEGORIES -> CategoriesScreen(viewModel = viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                AppScreen.FEEDBACK -> FeedbackScreen(viewModel = viewModel)
                AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
            }
        }
    }

    // Onboarding & Consent Dialog on first launch
    if (!hasAcceptedGdpr) {
        OnboardingConsentDialog(
            onAcceptConsent = { enableNotifications ->
                viewModel.setNotificationOptIn(enableNotifications)
                viewModel.acceptGdprConsent()
            }
        )
    }
}
