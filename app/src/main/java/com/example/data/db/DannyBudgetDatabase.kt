package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AdBannerDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.FeedbackDao
import com.example.data.dao.NotificationDao
import com.example.data.entity.AdBannerEntity
import com.example.data.entity.AppNotificationEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.FeedbackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        AdBannerEntity::class,
        FeedbackEntity::class,
        AppNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DannyBudgetDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun adBannerDao(): AdBannerDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: DannyBudgetDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DannyBudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DannyBudgetDatabase::class.java,
                    "danny_budget_local.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: DannyBudgetDatabase) {
            val categoryDao = database.categoryDao()
            val bannerDao = database.adBannerDao()

            val defaultCategories = listOf(
                CategoryEntity(
                    id = 1,
                    name = "Loyer",
                    icon = "🏠",
                    colorHex = "#2196F3",
                    monthlyBudgetLimit = 450.0,
                    isDefault = true,
                    orderIndex = 0
                ),
                CategoryEntity(
                    id = 2,
                    name = "Nourriture",
                    icon = "🍔",
                    colorHex = "#4CAF50",
                    monthlyBudgetLimit = 200.0,
                    isDefault = true,
                    orderIndex = 1
                ),
                CategoryEntity(
                    id = 3,
                    name = "Transport",
                    icon = "🚇",
                    colorHex = "#FF9800",
                    monthlyBudgetLimit = 40.0,
                    isDefault = true,
                    orderIndex = 2
                ),
                CategoryEntity(
                    id = 4,
                    name = "Divertissement",
                    icon = "🎉",
                    colorHex = "#9C27B0",
                    monthlyBudgetLimit = 80.0,
                    isDefault = true,
                    orderIndex = 3
                ),
                CategoryEntity(
                    id = 5,
                    name = "Autres",
                    icon = "✨",
                    colorHex = "#607D8B",
                    monthlyBudgetLimit = 50.0,
                    isDefault = true,
                    orderIndex = 4
                )
            )
            categoryDao.insertCategories(defaultCategories)

            val defaultBanners = listOf(
                AdBannerEntity(
                    id = 1,
                    title = "Paniers Anti-Gaspi Étudiants -30%",
                    subtitle = "Repas équilibrés à petit prix près de ton campus",
                    ctaText = "Découvrir le bon plan",
                    actionUrl = "https://example.com/antigaspi",
                    badgeText = "PARTENAIRE ÉTUDIANT",
                    isActive = true
                ),
                AdBannerEntity(
                    id = 2,
                    title = "Pass Transport & Vélo Étudiant",
                    subtitle = "Demande ton aide régionale de transport en 3 clics",
                    ctaText = "Simuler l'aide",
                    actionUrl = "https://example.com/transport-aide",
                    badgeText = "AIDE FINANCIÈRE",
                    isActive = true
                )
            )
            bannerDao.insertBanners(defaultBanners)
        }
    }
}
