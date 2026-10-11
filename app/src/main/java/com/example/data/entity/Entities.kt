package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColorHex: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String,
    val monthlyBudgetLimit: Double = 0.0,
    val isDefault: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "ad_banners")
data class AdBannerEntity(
    @PrimaryKey
    val id: Int, // 1 = Banner Haut (Calculatrice), 2 = Banner Bas (Historique)
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val actionUrl: String,
    val badgeText: String = "OFFRE ÉTUDIANTE",
    val isActive: Boolean = true
)

@Entity(tableName = "feedbacks")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rating: Int, // 1 to 5
    val comment: String,
    val userEmailOrPseudo: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Nouveau" // "Nouveau", "Lu", "Traité"
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val segment: String = "Tous les étudiants",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
