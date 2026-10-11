package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ExpenseEntity
import com.example.ui.components.AdBannerCard
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.InfoCyan
import com.example.ui.theme.MidnightBlue
import com.example.ui.theme.MidnightBlueBorder
import com.example.ui.theme.MidnightBlueCard
import com.example.ui.theme.MidnightBlueDark
import com.example.ui.theme.MidnightBlueSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DannyBudgetViewModel
import com.example.viewmodel.PeriodFilter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: DannyBudgetViewModel,
    modifier: Modifier = Modifier
) {
    val allExpenses by viewModel.allExpenses.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val banners by viewModel.allBanners.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    val bottomBanner = banners.find { it.id == 2 }

    val weekStart = remember { viewModel.getWeekStartTimestamp() }
    val monthStart = remember { viewModel.getMonthStartTimestamp() }

    val filteredExpenses = remember(allExpenses, selectedPeriod) {
        when (selectedPeriod) {
            PeriodFilter.THIS_WEEK -> allExpenses.filter { it.timestamp >= weekStart }
            PeriodFilter.THIS_MONTH -> allExpenses.filter { it.timestamp >= monthStart }
            PeriodFilter.ALL -> allExpenses
        }
    }

    val totalSpent = remember(filteredExpenses) {
        filteredExpenses.sumOf { it.amount }
    }

    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // En-tête Historique & Statistiques
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Historique & Analyses",
                        color = PureWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Visualise tes dépenses et fais des économies",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Sélecteur de période (Semaine, Mois, Tout)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightBlueDark)
                    .border(1.dp, MidnightBlueBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PeriodChip(
                    title = "Cette semaine",
                    isSelected = selectedPeriod == PeriodFilter.THIS_WEEK,
                    onClick = { viewModel.setPeriodFilter(PeriodFilter.THIS_WEEK) },
                    modifier = Modifier.weight(1f)
                )
                PeriodChip(
                    title = "Ce mois-ci",
                    isSelected = selectedPeriod == PeriodFilter.THIS_MONTH,
                    onClick = { viewModel.setPeriodFilter(PeriodFilter.THIS_MONTH) },
                    modifier = Modifier.weight(1f)
                )
                PeriodChip(
                    title = "Toutes",
                    isSelected = selectedPeriod == PeriodFilter.ALL,
                    onClick = { viewModel.setPeriodFilter(PeriodFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Carte Bilan Étudiant (Total & Estimation)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightBlueCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DÉPENSES DU CYCLE",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = ElectricYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${filteredExpenses.size} achats",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = String.format(Locale.FRANCE, "%.2f €", totalSpent),
                        color = PureWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Conseil étudiant anti-dépense
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MidnightBlueDark)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = ElectricYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (totalSpent > 300) {
                                    "Conseil Danny : Compare les prix du Resto U vs livraisons !"
                                } else {
                                    "Super gestion ! Tu es dans les clous de ton budget étudiant."
                                },
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Graphique à barres hebdomadaire (Canvas personnalisé)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightBlueDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ÉVOLUTION QUOTIDIENNE (7 JOURS)",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    WeeklyExpenseBarChart(expenses = filteredExpenses)
                }
            }
        }

        // Répartition par catégorie
        item {
            Text(
                text = "RÉPARTITION PAR CATÉGORIE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            CategoryBreakdownSection(
                expenses = filteredExpenses,
                totalSpent = totalSpent
            )
        }

        // Liste des transactions récentes
        item {
            Text(
                text = "HISTORIQUE DÉTAILLÉ (${filteredExpenses.size})",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        if (filteredExpenses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune dépense pour cette période.\nUtilise le pavé pour en ajouter une !",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredExpenses, key = { it.id }) { expense ->
                ExpenseItemCard(
                    expense = expense,
                    onDelete = { expenseToDelete = expense }
                )
            }
        }

        // 2. BANNIÈRE EN BAS DE L'ÉCRAN D'HISTORIQUE / STATISTIQUES (Obligatoire d'après brief)
        if (bottomBanner != null && bottomBanner.isActive) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                AdBannerCard(banner = bottomBanner)
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Boîte de dialogue de confirmation de suppression
    if (expenseToDelete != null) {
        val exp = expenseToDelete!!
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = {
                Text("Supprimer la dépense ?", color = PureWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Voulez-vous supprimer cette dépense de ${String.format(Locale.FRANCE, "%.2f €", exp.amount)} (${exp.categoryName}) ?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(exp)
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.DangerRed,
                        contentColor = PureWhite
                    )
                ) {
                    Text("Supprimer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Annuler", color = PureWhite)
                }
            },
            containerColor = MidnightBlueSurface
        )
    }
}

@Composable
private fun PeriodChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ElectricYellow else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) MidnightBlueDark else TextMuted,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun WeeklyExpenseBarChart(
    expenses: List<ExpenseEntity>,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
    val cal = Calendar.getInstance()
    val todayDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0, Sunday = 6

    // Aggregate by day of week (Monday to Sunday)
    val dayAmounts = remember(expenses) {
        val amounts = FloatArray(7) { 0f }
        expenses.forEach { exp ->
            cal.timeInMillis = exp.timestamp
            val dayIndex = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
            if (dayIndex in 0..6) {
                amounts[dayIndex] += exp.amount.toFloat()
            }
        }
        amounts
    }

    val maxAmount = dayAmounts.maxOrNull()?.coerceAtLeast(10f) ?: 10f

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            val barWidth = size.width / 9f
            val spacing = size.width / 7f

            for (i in 0..6) {
                val xCenter = (i + 0.5f) * spacing
                val barHeight = ((dayAmounts[i] / maxAmount) * (size.height - 20f)).coerceAtLeast(6f)
                val topY = size.height - barHeight

                val isToday = i == todayDayOfWeek
                val barColor = if (isToday) ElectricYellow else InfoCyan

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(xCenter - (barWidth / 2), topY),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dayLabels.forEachIndexed { index, label ->
                val isToday = index == todayDayOfWeek
                Text(
                    text = label,
                    color = if (isToday) ElectricYellow else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownSection(
    expenses: List<ExpenseEntity>,
    totalSpent: Double
) {
    if (expenses.isEmpty()) {
        Text(
            text = "Pas de dépenses à ventiler.",
            color = TextMuted,
            fontSize = 12.sp
        )
        return
    }

    val grouped = expenses.groupBy { it.categoryName }
        .map { (catName, list) ->
            val icon = list.firstOrNull()?.categoryIcon ?: "🏷️"
            val total = list.sumOf { it.amount }
            val percent = if (totalSpent > 0) (total / totalSpent * 100).toFloat() else 0f
            Triple(catName, icon, Pair(total, percent))
        }
        .sortedByDescending { it.third.first }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        grouped.forEach { (catName, icon, data) ->
            val (total, percent) = data
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightBlueCard)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = catName,
                                color = PureWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = String.format(Locale.FRANCE, "%.2f € (%.0f%%)", total, percent),
                            color = ElectricYellow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { percent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ElectricYellow,
                        trackColor = MidnightBlueDark
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseItemCard(
    expense: ExpenseEntity,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.FRANCE) }
    val formattedDate = remember(expense.timestamp) { dateFormat.format(Date(expense.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightBlueDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MidnightBlueSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = expense.categoryIcon, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = expense.categoryName,
                        color = PureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (expense.note.isNotBlank()) {
                        Text(
                            text = expense.note,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = formattedDate,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format(Locale.FRANCE, "%.2f €", expense.amount),
                    color = ElectricYellow,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
