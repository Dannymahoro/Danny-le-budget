package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CategoryEntity
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.MidnightBlue
import com.example.ui.theme.MidnightBlueBorder
import com.example.ui.theme.MidnightBlueCard
import com.example.ui.theme.MidnightBlueDark
import com.example.ui.theme.MidnightBlueSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DannyBudgetViewModel
import java.util.Locale

@Composable
fun CategoriesScreen(
    viewModel: DannyBudgetViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.allCategories.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBlue,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    categoryToEdit = null
                    showEditDialog = true
                },
                containerColor = ElectricYellow,
                contentColor = MidnightBlueDark,
                modifier = Modifier.testTag("add_category_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nouvelle catégorie",
                    tint = MidnightBlueDark
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    Text(
                        text = "Catégories de Dépenses",
                        color = PureWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Personnalise tes postes de budget étudiant",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(categories, key = { it.id }) { category ->
                val spent = allExpenses.filter { it.categoryId == category.id }.sumOf { it.amount }
                CategoryManagementCard(
                    category = category,
                    spent = spent,
                    onEdit = {
                        categoryToEdit = category
                        showEditDialog = true
                    },
                    onDelete = {
                        categoryToDelete = category
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp)) // Space for FAB
            }
        }
    }

    // Modal Ajout / Modification de Catégorie
    if (showEditDialog) {
        CategoryEditDialog(
            category = categoryToEdit,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, icon, color, budget ->
                if (categoryToEdit == null) {
                    viewModel.createCategory(name, icon, color, budget)
                } else {
                    viewModel.updateCategory(
                        categoryToEdit!!.copy(
                            name = name,
                            icon = icon,
                            colorHex = color,
                            monthlyBudgetLimit = budget
                        )
                    )
                }
                showEditDialog = false
            }
        )
    }

    // Modal Confirmation Suppression
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = {
                Text("Supprimer la catégorie ?", color = PureWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Es-tu sûr de vouloir supprimer \"${cat.icon} ${cat.name}\" ? Les dépenses existantes resteront dans l'historique.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(cat)
                        categoryToDelete = null
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
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Annuler", color = PureWhite)
                }
            },
            containerColor = MidnightBlueSurface
        )
    }
}

@Composable
private fun CategoryManagementCard(
    category: CategoryEntity,
    spent: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightBlueCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MidnightBlueDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.icon, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.name,
                            color = PureWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (category.isDefault) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Base",
                                color = TextMuted,
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MidnightBlueSurface)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (category.monthlyBudgetLimit > 0) {
                            "Plafond : ${String.format(Locale.FRANCE, "%.0f €/mois", category.monthlyBudgetLimit)} • Dépensé : ${String.format(Locale.FRANCE, "%.2f €", spent)}"
                        } else {
                            "Dépensé : ${String.format(Locale.FRANCE, "%.2f €", spent)}"
                        },
                        color = if (category.monthlyBudgetLimit > 0 && spent > category.monthlyBudgetLimit) {
                            com.example.ui.theme.DangerRed
                        } else {
                            TextSecondary
                        },
                        fontSize = 12.sp
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier",
                        tint = ElectricYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (!category.isDefault || category.name !in listOf("Loyer", "Nourriture")) {
                    IconButton(onClick = onDelete) {
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
}

@Composable
private fun CategoryEditDialog(
    category: CategoryEntity?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, icon: String, colorHex: String, budget: Double) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var icon by remember { mutableStateOf(category?.icon ?: "✨") }
    var budgetStr by remember {
        mutableStateOf(if (category != null && category.monthlyBudgetLimit > 0) category.monthlyBudgetLimit.toString() else "100")
    }

    val emojiChoices = listOf("🏠", "🍔", "🚇", "🎉", "✨", "📚", "☕", "💊", "👗", "💻", "🍕", "🚲")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (category == null) "Ajouter une catégorie" else "Modifier la catégorie",
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la catégorie", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = ElectricYellow,
                        unfocusedBorderColor = MidnightBlueBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Choisir une icône / emoji :",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojiChoices.take(6).forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (icon == emoji) ElectricYellow else MidnightBlueDark)
                                .clickable { icon = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojiChoices.drop(6).forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (icon == emoji) ElectricYellow else MidnightBlueDark)
                                .clickable { icon = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = budgetStr,
                    onValueChange = { budgetStr = it },
                    label = { Text("Budget mensuel cible (€)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = ElectricYellow,
                        unfocusedBorderColor = MidnightBlueBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budget = budgetStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(name, icon, "#2196F3", budget)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricYellow,
                    contentColor = MidnightBlueDark
                )
            ) {
                Text(if (category == null) "Créer" else "Enregistrer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = PureWhite)
            }
        },
        containerColor = MidnightBlueSurface
    )
}
