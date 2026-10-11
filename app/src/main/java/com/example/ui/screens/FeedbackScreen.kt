package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.RatingBar
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
fun FeedbackScreen(
    viewModel: DannyBudgetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var emailOrPseudo by remember { mutableStateOf("") }
    var sendEmailCopy by remember { mutableStateOf(true) }

    BackHandler {
        viewModel.handleBack()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // En-tête avec bouton retour
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.handleBack() },
                modifier = Modifier.testTag("feedback_back_button")
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
                    text = "Avis & Suggestions",
                    color = PureWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Aide-nous à améliorer Danny le budget",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Carte d'évaluation sur 5 étoiles
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightBlueCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Quelle note donnes-tu à l'application ?",
                    color = PureWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                RatingBar(
                    rating = rating,
                    onRatingChanged = { rating = it },
                    modifier = Modifier.testTag("feedback_rating_bar")
                )

                Spacer(modifier = Modifier.height(4.dp))

                val ratingDescription = when (rating) {
                    5 -> "🌟 Excellent ! Très utile au quotidien"
                    4 -> "👍 Vraiment bien, quelques détails à peaufiner"
                    3 -> "👌 Pas mal, des points à améliorer"
                    2 -> "😕 Décevant, il manque des fonctions clés"
                    else -> "❌ Pas convaincu"
                }

                Text(
                    text = ratingDescription,
                    color = ElectricYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Champ commentaire libre
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightBlueDark)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Ton retour libre ou suggestion d'amélioration :",
                    color = PureWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = {
                        Text(
                            text = "Ex: J'aimerais pouvoir exporter des graphiques, avoir des alertes de fin de mois...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("feedback_comment_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = ElectricYellow,
                        unfocusedBorderColor = MidnightBlueBorder
                    )
                )

                OutlinedTextField(
                    value = emailOrPseudo,
                    onValueChange = { emailOrPseudo = it },
                    placeholder = {
                        Text(
                            text = "Ton prénom ou email étudiant (facultatif)",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_email_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = ElectricYellow,
                        unfocusedBorderColor = MidnightBlueBorder
                    )
                )
            }
        }

        // Information dispatching
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MidnightBlueSurface)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = ElectricYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Ton message est enregistré dans la base de données et peut être envoyé par email direct au créateur de Danny.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bouton d'envoi
        Button(
            onClick = {
                val creatorEmail = "d0787233211@gmail.com"
                viewModel.submitFeedback(
                    rating = rating,
                    comment = comment,
                    emailOrPseudo = emailOrPseudo
                ) {
                    if (sendEmailCopy) {
                        try {
                            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$creatorEmail")
                                putExtra(Intent.EXTRA_SUBJECT, "Avis Danny le budget ($rating/5 étoiles)")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Note : $rating/5\nÉtudiant : $emailOrPseudo\n\nCommentaire :\n$comment"
                                )
                            }
                            context.startActivity(mailIntent)
                        } catch (_: Exception) {}
                    }
                    viewModel.navigateTo(AppScreen.SETTINGS)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_feedback_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricYellow,
                contentColor = MidnightBlueDark
            )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = MidnightBlueDark,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Envoyer mon avis au créateur",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MidnightBlueDark
            )
        }
    }
}
