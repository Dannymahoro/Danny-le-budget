package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AdBannerEntity
import com.example.data.entity.FeedbackEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    viewModel: DannyBudgetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val banners by viewModel.allBanners.collectAsState()
    val feedbacks by viewModel.allFeedbacks.collectAsState()

    BackHandler {
        viewModel.handleBack()
    }

    var selectedAdminTab by remember { mutableStateOf(0) } // 0: Bannières, 1: Push Notifications, 2: Feedbacks
    var bannerToEdit by remember { mutableStateOf<AdBannerEntity?>(null) }

    // État pour envoi Push manuel
    var notifTitle by remember { mutableStateOf("") }
    var notifMessage by remember { mutableStateOf("") }
    var notifSegment by remember { mutableStateOf("Tous les étudiants") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.handleBack() },
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = PureWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Dashboard Administrateur",
                        color = PureWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Monétisation, Notifications manuelles & Feedbacks",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Onglets du Dashboard
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
                AdminTabButton(
                    title = "Bannières (2)",
                    isSelected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                AdminTabButton(
                    title = "Push Notifs",
                    isSelected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                AdminTabButton(
                    title = "Avis (${feedbacks.size})",
                    isSelected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // CONTENU SELON L'ONGLET
        when (selectedAdminTab) {
            0 -> {
                // GESTION DES 2 BANNIÈRES PUBLICITAIRES FIXES
                item {
                    Text(
                        text = "ESPACES PUBLICITAIRES FIXES (GESTION MANUELLE)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                item {
                    Text(
                        text = "Les 2 bannières sont gérées directement ici, sans régie publicitaire tierce, conformément aux règles du projet.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                items(banners, key = { it.id }) { banner ->
                    AdminBannerCard(
                        banner = banner,
                        onEdit = { bannerToEdit = banner },
                        onToggleActive = { isActive ->
                            viewModel.updateBanner(banner.copy(isActive = isActive))
                        }
                    )
                }
            }

            1 -> {
                // NOTIFICATIONS MANUELLES PUSH AUX UTILISATEURS / SEGMENTS
                item {
                    Text(
                        text = "ENVOI D'UNE NOTIFICATION PUSH MANUELLE",
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
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Diffuser une annonce aux étudiants",
                                color = PureWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = notifTitle,
                                onValueChange = { notifTitle = it },
                                label = { Text("Titre de la notification", color = TextMuted) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_notif_title_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite,
                                    focusedBorderColor = ElectricYellow,
                                    unfocusedBorderColor = MidnightBlueBorder
                                )
                            )

                            OutlinedTextField(
                                value = notifMessage,
                                onValueChange = { notifMessage = it },
                                label = { Text("Message de l'annonce ou promo", color = TextMuted) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .testTag("admin_notif_message_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite,
                                    focusedBorderColor = ElectricYellow,
                                    unfocusedBorderColor = MidnightBlueBorder
                                )
                            )

                            Text(
                                text = "Segment cible :",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val segments = listOf("Tous les étudiants", "Promo Bons Plans", "Nouveautés")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                segments.forEach { segment ->
                                    val isChosen = notifSegment == segment
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isChosen) ElectricYellow else MidnightBlueDark)
                                            .clickable { notifSegment = segment }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = segment,
                                            color = if (isChosen) MidnightBlueDark else TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    viewModel.sendAdminAnnouncement(
                                        context = context,
                                        title = notifTitle,
                                        message = notifMessage,
                                        segment = notifSegment
                                    )
                                    notifTitle = ""
                                    notifMessage = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("admin_send_notif_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricYellow,
                                    contentColor = MidnightBlueDark
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = MidnightBlueDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Envoyer le Push aux abonnés",
                                    fontWeight = FontWeight.Bold,
                                    color = MidnightBlueDark
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // DASHBOARD DES AVIS ET FEEDBACKS
                item {
                    Text(
                        text = "AVIS & SUGGESTIONS DES ÉTUDIANTS (${feedbacks.size})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (feedbacks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MidnightBlueDark)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aucun feedback reçu pour l'instant.\nLes retours envoyés par les étudiants apparaîtront ici.",
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(feedbacks, key = { it.id }) { feedback ->
                        AdminFeedbackCard(
                            feedback = feedback,
                            onStatusChange = { newStatus ->
                                viewModel.updateFeedbackStatus(feedback, newStatus)
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Modal Modification Bannière Pub
    if (bannerToEdit != null) {
        val banner = bannerToEdit!!
        var title by remember { mutableStateOf(banner.title) }
        var subtitle by remember { mutableStateOf(banner.subtitle) }
        var badge by remember { mutableStateOf(banner.badgeText) }
        var cta by remember { mutableStateOf(banner.ctaText) }
        var url by remember { mutableStateOf(banner.actionUrl) }

        AlertDialog(
            onDismissRequest = { bannerToEdit = null },
            title = {
                Text(
                    text = "Modifier Bannière #${banner.id} (${if (banner.id == 1) "Haut Calculatrice" else "Bas Historique"})",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de l'annonce", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = ElectricYellow,
                            unfocusedBorderColor = MidnightBlueBorder
                        )
                    )

                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Sous-titre / Description", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = ElectricYellow,
                            unfocusedBorderColor = MidnightBlueBorder
                        )
                    )

                    OutlinedTextField(
                        value = badge,
                        onValueChange = { badge = it },
                        label = { Text("Badge (ex: BON PLAN ÉTUDIANT)", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = ElectricYellow,
                            unfocusedBorderColor = MidnightBlueBorder
                        )
                    )

                    OutlinedTextField(
                        value = cta,
                        onValueChange = { cta = it },
                        label = { Text("Texte du bouton CTA", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = ElectricYellow,
                            unfocusedBorderColor = MidnightBlueBorder
                        )
                    )

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("Lien URL partenaire", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = ElectricYellow,
                            unfocusedBorderColor = MidnightBlueBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateBanner(
                            banner.copy(
                                title = title,
                                subtitle = subtitle,
                                badgeText = badge,
                                ctaText = cta,
                                actionUrl = url
                            )
                        )
                        bannerToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricYellow,
                        contentColor = MidnightBlueDark
                    )
                ) {
                    Text("Enregistrer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { bannerToEdit = null }) {
                    Text("Annuler", color = PureWhite)
                }
            },
            containerColor = MidnightBlueSurface
        )
    }
}

@Composable
private fun AdminTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ElectricYellow else androidx.compose.ui.graphics.Color.Transparent)
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
private fun AdminBannerCard(
    banner: AdBannerEntity,
    onEdit: () -> Unit,
    onToggleActive: (Boolean) -> Unit
) {
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
                Text(
                    text = if (banner.id == 1) "1. BANNIÈRE HAUT (Calculatrice)" else "2. BANNIÈRE BAS (Historique)",
                    color = ElectricYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (banner.isActive) "Active" else "Masquée",
                        color = if (banner.isActive) PureWhite else TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = banner.isActive,
                        onCheckedChange = onToggleActive,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MidnightBlueDark,
                            checkedTrackColor = ElectricYellow
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = banner.title,
                color = PureWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = banner.subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MidnightBlueSurface,
                    contentColor = PureWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Modifier le texte et le lien", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AdminFeedbackCard(
    feedback: FeedbackEntity,
    onStatusChange: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE) }
    val formattedDate = remember(feedback.timestamp) { dateFormat.format(Date(feedback.timestamp)) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i <= feedback.rating) ElectricYellow else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${feedback.rating}/5",
                        color = ElectricYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = formattedDate,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = feedback.comment,
                color = PureWhite,
                fontSize = 14.sp
            )

            if (feedback.userEmailOrPseudo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Auteur : ${feedback.userEmailOrPseudo}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Nouveau", "Lu", "Traité").forEach { status ->
                    val isCurrent = feedback.status == status
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) ElectricYellow else MidnightBlueSurface)
                            .clickable { onStatusChange(status) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status,
                            color = if (isCurrent) MidnightBlueDark else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
