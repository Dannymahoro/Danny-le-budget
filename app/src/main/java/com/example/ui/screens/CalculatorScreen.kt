package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CategoryEntity
import com.example.ui.components.AdBannerCard
import com.example.ui.components.CategoryPillRow
import com.example.ui.components.NumericKeypad
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.MidnightBlue
import com.example.ui.theme.MidnightBlueBorder
import com.example.ui.theme.MidnightBlueCard
import com.example.ui.theme.MidnightBlueDark
import com.example.ui.theme.MidnightBlueSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DannyBudgetViewModel

@Composable
fun CalculatorScreen(
    viewModel: DannyBudgetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calculatorInput by viewModel.calculatorInput.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val expenseNote by viewModel.expenseNote.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val banners by viewModel.allBanners.collectAsState()

    val topBanner = banners.find { it.id == 1 }

    // Pick first category as default if none selected
    val activeCategory = selectedCategory ?: categories.firstOrNull()

    val amountDouble = calculatorInput.toDoubleOrNull() ?: 0.0
    val formattedAmount = if (calculatorInput.contains(".")) {
        "$calculatorInput €"
    } else {
        "$calculatorInput.00 €"
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElectricYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Danny Étudiant",
                        tint = MidnightBlueDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Danny le budget",
                        color = PureWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Gère ton budget étudiant au centime près",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightBlueSurface)
                    .border(1.dp, MidnightBlueBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Mode Étudiant",
                    color = ElectricYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 1. BANNIÈRE PUBLICITAIRE EN HAUT DE L'ÉCRAN PRINCIPAL (Obligatoire d'après brief)
        if (topBanner != null && topBanner.isActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                AdBannerCard(banner = topBanner)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ÉCRAN CALCULATRICE / AFFICHAGE DU MONTANT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MidnightBlueDark)
                .border(1.5.dp, MidnightBlueBorder, RoundedCornerShape(20.dp))
                .padding(vertical = 18.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Category Tag Selected
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MidnightBlueSurface)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = activeCategory?.icon ?: "🏷️",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = activeCategory?.name ?: "Sélectionner",
                        color = PureWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Big digital display
                Text(
                    text = formattedAmount,
                    color = PureWhite,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("calculator_amount_display")
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (amountDouble > 0) "Prêt à être enregistré" else "Saisis un montant sur le pavé",
                    color = if (amountDouble > 0) ElectricYellow else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Champ facultatif : Note rapide
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = expenseNote,
                onValueChange = { viewModel.setExpenseNote(it) },
                placeholder = {
                    Text(
                        text = "Note facultative (ex: Courses Lidl, Sandwich U, Ticket RATP...)",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_note_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MidnightBlueDark,
                    unfocusedContainerColor = MidnightBlueDark,
                    focusedBorderColor = ElectricYellow,
                    unfocusedBorderColor = MidnightBlueBorder,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Catégories (sélection en 1 clic)
        Text(
            text = "CHOISIR LA CATÉGORIE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        CategoryPillRow(
            categories = categories,
            selectedCategory = activeCategory,
            onCategorySelected = { category ->
                viewModel.selectCategory(category)
            },
            onManageCategoriesClick = {
                viewModel.navigateTo(AppScreen.CATEGORIES)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Pavé numérique principal
        NumericKeypad(
            onDigitClick = { digit -> viewModel.onDigitPressed(digit) },
            onDecimalClick = { viewModel.onDecimalPressed() },
            onBackspaceClick = { viewModel.onBackspacePressed() },
            onClearClick = { viewModel.onClearPressed() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grand bouton Call-To-Action (Electric Yellow avec texte Midnight Blue Dark contrasté)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.addCurrentExpense(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("add_expense_cta_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricYellow,
                    contentColor = MidnightBlueDark
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MidnightBlueDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (amountDouble > 0) {
                        "Enregistrer $formattedAmount"
                    } else {
                        "Ajouter la dépense"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MidnightBlueDark
                )
            }
        }
    }
}
