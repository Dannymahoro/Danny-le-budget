package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SettingsScreen(
    viewModel: DannyBudgetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isNotifEnabled by viewModel.isNotificationOptedIn.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = "Paramètres & Confidentialité",
                    color = PureWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Contrôle tes données, alertes et préférences",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Section Notifications
        item {
            Text(
                text = "NOTIFICATIONS ÉTUDIANTES",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightBlueCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = ElectricYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Bilan hebdomadaire (Dimanche 20h)",
                                    color = PureWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Résumé récapitulatif de tes dépenses de la semaine",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isNotifEnabled,
                            onCheckedChange = { viewModel.setNotificationOptIn(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MidnightBlueDark,
                                checkedTrackColor = ElectricYellow,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = MidnightBlueDark
                            ),
                            modifier = Modifier.testTag("notification_opt_in_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.triggerWeeklySummaryTest(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_weekly_notif_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MidnightBlueSurface,
                            contentColor = PureWhite
                        )
                    ) {
                        Text(
                            text = "Simuler la notification du dimanche 20h",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section Feedback
        item {
            Text(
                text = "FEEDBACK UTILISATEUR",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            SettingsNavigationCard(
                icon = Icons.Default.Feedback,
                title = "Donner mon avis ou suggérer une idée",
                subtitle = "Envoie une note et un message directement au créateur",
                onClick = { viewModel.navigateTo(AppScreen.FEEDBACK) },
                testTag = "open_feedback_screen_button"
            )
        }

        // Section Sécurité & RGPD
        item {
            Text(
                text = "SÉCURITÉ & PROTECTION DES DONNÉES (RGPD)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightBlueCard)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ElectricYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stockage 100% local sur ton appareil",
                            color = PureWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Aucune donnée de dépense n'est partagée avec des serveurs tiers ou régies publicitaires. Tes finances restent privées et protégées.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPrivacyDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MidnightBlueSurface,
                                contentColor = PureWhite
                            )
                        ) {
                            Text("Politique RGPD", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val jsonExport = viewModel.generateDataExportJson()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Export Données Danny le budget (RGPD)")
                                    putExtra(Intent.EXTRA_TEXT, jsonExport)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Exporter mes données"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MidnightBlueSurface,
                                contentColor = PureWhite
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Exporter", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MidnightBlueDark,
                            contentColor = com.example.ui.theme.DangerRed
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Effacer toutes mes données (Droit à l'oubli)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section Administration & Monétisation
        item {
            Text(
                text = "ESPACE ADMINISTRATEUR",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            SettingsNavigationCard(
                icon = Icons.Default.AdminPanelSettings,
                title = "Tableau de Bord Admin",
                subtitle = "Gestion des 2 bannières pub, push notifications & feedbacks",
                onClick = { viewModel.navigateTo(AppScreen.ADMIN) },
                testTag = "open_admin_dashboard_button"
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Politique de Confidentialité & RGPD
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text("Politique de Confidentialité & RGPD", color = PureWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Stockage Local & Minimisation :",
                        color = ElectricYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Toutes les dépenses et catégories sont stockées uniquement dans la base de données SQLite locale (Room) de votre appareil.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "2. Monétisation & Bannières :",
                        color = ElectricYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Les 2 bannières de l'application sont gérées manuellement en interne sans régie publicitaire intrusive (aucun cookie, aucun traceur tiers).",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "3. Vos Droits RGPD :",
                        color = ElectricYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Droit d'accès et de portabilité : Export JSON complet en 1 clic.\n• Droit à l'effacement : Suppression définitive de toutes les données locales.\n• Consentement aux notifications : Révocable à tout moment.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricYellow,
                        contentColor = MidnightBlueDark
                    )
                ) {
                    Text("J'ai compris", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MidnightBlueSurface
        )
    }

    // Modal Confirmation Suppression Totale
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text("Effacer toutes les données ?", color = PureWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Attention : cette action est irréversible. Toutes vos dépenses enregistrées, catégories et avis locaux seront définitivement effacés.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.DangerRed,
                        contentColor = PureWhite
                    )
                ) {
                    Text("Confirmer la suppression", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Annuler", color = PureWhite)
                }
            },
            containerColor = MidnightBlueSurface
        )
    }
}

@Composable
private fun SettingsNavigationCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick),
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MidnightBlueDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        color = PureWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
