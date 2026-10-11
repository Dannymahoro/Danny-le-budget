package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.DannyBudgetDatabase
import com.example.data.entity.AdBannerEntity
import com.example.data.entity.AppNotificationEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.FeedbackEntity
import com.example.data.repository.DannyBudgetRepository
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppScreen {
    CALCULATOR,
    HISTORY,
    CATEGORIES,
    SETTINGS,
    FEEDBACK,
    ADMIN
}

enum class PeriodFilter {
    THIS_WEEK,
    THIS_MONTH,
    ALL
}

class DannyBudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DannyBudgetRepository
    private val prefs = application.getSharedPreferences("danny_budget_prefs", Context.MODE_PRIVATE)

    val allExpenses: StateFlow<List<ExpenseEntity>>
    val allCategories: StateFlow<List<CategoryEntity>>
    val allBanners: StateFlow<List<AdBannerEntity>>
    val allFeedbacks: StateFlow<List<FeedbackEntity>>
    val allNotifications: StateFlow<List<AppNotificationEntity>>

    private val _currentScreen = MutableStateFlow(AppScreen.CALCULATOR)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenBackStack = mutableListOf<AppScreen>()

    private val _calculatorInput = MutableStateFlow("0")
    val calculatorInput: StateFlow<String> = _calculatorInput.asStateFlow()

    private val _selectedCategory = MutableStateFlow<CategoryEntity?>(null)
    val selectedCategory: StateFlow<CategoryEntity?> = _selectedCategory.asStateFlow()

    private val _expenseNote = MutableStateFlow("")
    val expenseNote: StateFlow<String> = _expenseNote.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(PeriodFilter.THIS_WEEK)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()

    private val _isNotificationOptedIn = MutableStateFlow(
        prefs.getBoolean("pref_notif_opt_in", true)
    )
    val isNotificationOptedIn: StateFlow<Boolean> = _isNotificationOptedIn.asStateFlow()

    private val _hasAcceptedGdpr = MutableStateFlow(
        prefs.getBoolean("pref_gdpr_accepted", false)
    )
    val hasAcceptedGdpr: StateFlow<Boolean> = _hasAcceptedGdpr.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    init {
        NotificationHelper.createNotificationChannels(application)

        val db = DannyBudgetDatabase.getDatabase(application, viewModelScope)
        repository = DannyBudgetRepository(
            expenseDao = db.expenseDao(),
            categoryDao = db.categoryDao(),
            adBannerDao = db.adBannerDao(),
            feedbackDao = db.feedbackDao(),
            notificationDao = db.notificationDao()
        )

        allExpenses = repository.allExpenses.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allCategories = repository.allCategories.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allBanners = repository.allBanners.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allFeedbacks = repository.allFeedbacks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allNotifications = repository.allNotifications.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Seed sample expenses if first time running
        viewModelScope.launch(Dispatchers.IO) {
            checkAndSeedSampleData()
        }
    }

    private suspend fun checkAndSeedSampleData() {
        val hasSeeded = prefs.getBoolean("has_seeded_sample_data", false)
        if (!hasSeeded) {
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            val sampleExpenses = listOf(
                ExpenseEntity(
                    amount = 12.80,
                    categoryId = 2,
                    categoryName = "Nourriture",
                    categoryIcon = "🍔",
                    categoryColorHex = "#4CAF50",
                    timestamp = now - (dayMillis * 0.2).toLong(),
                    note = "Courses de la semaine (Lidl)"
                ),
                ExpenseEntity(
                    amount = 4.50,
                    categoryId = 4,
                    categoryName = "Divertissement",
                    categoryIcon = "🎉",
                    categoryColorHex = "#9C27B0",
                    timestamp = now - (dayMillis * 1.1).toLong(),
                    note = "Café & croissant bibliothèque"
                ),
                ExpenseEntity(
                    amount = 35.00,
                    categoryId = 3,
                    categoryName = "Transport",
                    categoryIcon = "🚇",
                    categoryColorHex = "#FF9800",
                    timestamp = now - (dayMillis * 2.5).toLong(),
                    note = "Recharge pass mensuel"
                ),
                ExpenseEntity(
                    amount = 450.00,
                    categoryId = 1,
                    categoryName = "Loyer",
                    categoryIcon = "🏠",
                    categoryColorHex = "#2196F3",
                    timestamp = now - (dayMillis * 4.0).toLong(),
                    note = "Loyer résidence CROUS"
                )
            )
            sampleExpenses.forEach { repository.insertExpense(it) }
            prefs.edit().putBoolean("has_seeded_sample_data", true).apply()
        }
    }

    // Navigation
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (_screenBackStack.isNotEmpty()) {
            _currentScreen.value = _screenBackStack.removeAt(_screenBackStack.size - 1)
            return true
        } else if (_currentScreen.value != AppScreen.CALCULATOR) {
            _currentScreen.value = AppScreen.CALCULATOR
            return true
        }
        return false
    }

    // Keypad Logic
    fun onDigitPressed(digit: String) {
        val current = _calculatorInput.value
        if (current == "0") {
            _calculatorInput.value = digit
        } else {
            // Cap length at 8 digits
            if (current.length < 8) {
                _calculatorInput.value = current + digit
            }
        }
    }

    fun onDecimalPressed() {
        val current = _calculatorInput.value
        if (!current.contains(".")) {
            _calculatorInput.value = "$current."
        }
    }

    fun onBackspacePressed() {
        val current = _calculatorInput.value
        if (current.length > 1) {
            _calculatorInput.value = current.dropLast(1)
        } else {
            _calculatorInput.value = "0"
        }
    }

    fun onClearPressed() {
        _calculatorInput.value = "0"
        _expenseNote.value = ""
    }

    fun selectCategory(category: CategoryEntity) {
        _selectedCategory.value = category
    }

    fun setExpenseNote(note: String) {
        _expenseNote.value = note
    }

    fun setPeriodFilter(period: PeriodFilter) {
        _selectedPeriod.value = period
    }

    // Quick Add Expense (Supports 1-tap with selected or tapped category)
    fun addExpenseWithCategory(category: CategoryEntity, context: Context? = null) {
        _selectedCategory.value = category
        addCurrentExpense(context)
    }

    fun addCurrentExpense(context: Context? = null) {
        val amount = _calculatorInput.value.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) {
            _snackBarMessage.value = "Entre un montant supérieur à 0 € !"
            return
        }

        val category = _selectedCategory.value ?: allCategories.value.firstOrNull()
        if (category == null) {
            _snackBarMessage.value = "Sélectionne une catégorie pour enregistrer."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val expense = ExpenseEntity(
                amount = amount,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.icon,
                categoryColorHex = category.colorHex,
                timestamp = System.currentTimeMillis(),
                note = _expenseNote.value.trim()
            )
            repository.insertExpense(expense)

            _calculatorInput.value = "0"
            _expenseNote.value = ""
            val formatted = String.format(java.util.Locale.FRANCE, "%.2f €", amount)
            _snackBarMessage.value = "$formatted ajouté dans ${category.name} !"
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExpense(expense)
            _snackBarMessage.value = "Dépense supprimée."
        }
    }

    // Category Management
    fun createCategory(name: String, icon: String, colorHex: String, budget: Double) {
        if (name.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val cat = CategoryEntity(
                name = name.trim(),
                icon = icon.ifBlank { "🏷️" },
                colorHex = colorHex,
                monthlyBudgetLimit = budget,
                isDefault = false,
                orderIndex = allCategories.value.size
            )
            repository.insertCategory(cat)
            _snackBarMessage.value = "Catégorie \"$name\" créée !"
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category)
            _snackBarMessage.value = "Catégorie \"${category.name}\" mise à jour !"
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
            if (_selectedCategory.value?.id == category.id) {
                _selectedCategory.value = null
            }
            _snackBarMessage.value = "Catégorie supprimée."
        }
    }

    // Banners Management (Admin)
    fun updateBanner(banner: AdBannerEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBanner(banner)
            _snackBarMessage.value = "Bannière #${banner.id} enregistrée !"
        }
    }

    // Notifications
    fun setNotificationOptIn(enabled: Boolean) {
        _isNotificationOptedIn.value = enabled
        prefs.edit().putBoolean("pref_notif_opt_in", enabled).apply()
        _snackBarMessage.value = if (enabled) {
            "Notifications activées (Bilan le dimanche à 20h)"
        } else {
            "Notifications désactivées."
        }
    }

    fun acceptGdprConsent() {
        _hasAcceptedGdpr.value = true
        prefs.edit().putBoolean("pref_gdpr_accepted", true).apply()
    }

    fun triggerWeeklySummaryTest(context: Context) {
        if (!_isNotificationOptedIn.value) {
            _snackBarMessage.value = "Les notifications sont désactivées dans les paramètres."
            return
        }

        val weekStart = getWeekStartTimestamp()
        val weekExpenses = allExpenses.value.filter { it.timestamp >= weekStart }
        val total = weekExpenses.sumOf { it.amount }

        val topCategory = weekExpenses
            .groupBy { it.categoryName }
            .maxByOrNull { entry -> entry.value.sumOf { it.amount } }

        val breakdown = if (topCategory != null) {
            val topTotal = topCategory.value.sumOf { it.amount }
            "Principal poste : ${topCategory.key} (${String.format(java.util.Locale.FRANCE, "%.2f €", topTotal)})"
        } else {
            "Aucune dépense enregistrée cette semaine !"
        }

        NotificationHelper.showWeeklySummaryNotification(context, total, breakdown)
        _snackBarMessage.value = "Notification du dimanche 20h envoyée !"
    }

    fun sendAdminAnnouncement(context: Context, title: String, message: String, segment: String) {
        if (title.isBlank() || message.isBlank()) {
            _snackBarMessage.value = "Titre et message obligatoires."
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val notif = AppNotificationEntity(
                title = title.trim(),
                message = message.trim(),
                segment = segment,
                timestamp = System.currentTimeMillis()
            )
            repository.insertNotification(notif)
            if (_isNotificationOptedIn.value) {
                NotificationHelper.showAdminAnnouncementNotification(context, title, message, segment)
            }
            _snackBarMessage.value = "Notification push envoyée aux utilisateurs ($segment) !"
        }
    }

    // Feedback Management
    fun submitFeedback(rating: Int, comment: String, emailOrPseudo: String, onComplete: () -> Unit) {
        if (comment.isBlank()) {
            _snackBarMessage.value = "Merci d'ajouter un commentaire !"
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val fb = FeedbackEntity(
                rating = rating,
                comment = comment.trim(),
                userEmailOrPseudo = emailOrPseudo.trim(),
                timestamp = System.currentTimeMillis(),
                status = "Nouveau"
            )
            repository.insertFeedback(fb)
            _snackBarMessage.value = "Merci pour ton avis ! Envoyé au créateur."
            viewModelScope.launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun updateFeedbackStatus(feedback: FeedbackEntity, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFeedback(feedback.copy(status = newStatus))
            _snackBarMessage.value = "Statut mis à jour : $newStatus"
        }
    }

    // RGPD / GDPR
    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllExpenses()
            repository.clearAllFeedbacks()
            repository.clearAllNotifications()
            _calculatorInput.value = "0"
            _expenseNote.value = ""
            _snackBarMessage.value = "Toutes les données locales ont été effacées (RGPD)."
        }
    }

    fun generateDataExportJson(): String {
        val expenses = allExpenses.value
        val categories = allCategories.value
        val feedbacks = allFeedbacks.value

        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"app\": \"Danny le budget\",\n")
        sb.append("  \"export_date\": \"${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.FRANCE).format(java.util.Date())}\",\n")
        sb.append("  \"gdpr_compliance\": \"Local storage only, zero trackers\",\n")
        sb.append("  \"total_expenses\": ${expenses.size},\n")
        sb.append("  \"expenses\": [\n")
        expenses.forEachIndexed { index, exp ->
            sb.append("    {\"id\": ${exp.id}, \"amount\": ${exp.amount}, \"category\": \"${exp.categoryName}\", \"note\": \"${exp.note}\", \"timestamp\": ${exp.timestamp}}")
            if (index < expenses.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")
        sb.append("  \"categories\": [\n")
        categories.forEachIndexed { index, cat ->
            sb.append("    {\"id\": ${cat.id}, \"name\": \"${cat.name}\", \"icon\": \"${cat.icon}\", \"monthlyBudget\": ${cat.monthlyBudgetLimit}}")
            if (index < categories.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ]\n")
        sb.append("}")
        return sb.toString()
    }

    fun clearSnackbar() {
        _snackBarMessage.value = null
    }

    // Helper functions for time periods
    fun getWeekStartTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getMonthStartTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
