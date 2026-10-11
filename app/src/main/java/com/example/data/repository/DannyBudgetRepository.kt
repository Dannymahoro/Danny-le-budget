package com.example.data.repository

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
import kotlinx.coroutines.flow.Flow

class DannyBudgetRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val adBannerDao: AdBannerDao,
    private val feedbackDao: FeedbackDao,
    private val notificationDao: NotificationDao
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allBanners: Flow<List<AdBannerEntity>> = adBannerDao.getAllBanners()
    val allFeedbacks: Flow<List<FeedbackEntity>> = feedbackDao.getAllFeedbacks()
    val allNotifications: Flow<List<AppNotificationEntity>> = notificationDao.getAllNotifications()

    fun getExpensesBetween(start: Long, end: Long): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesBetween(start, end)

    fun getBannerById(id: Int): Flow<AdBannerEntity?> = adBannerDao.getBannerById(id)

    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)
    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteExpenseById(id)
    suspend fun clearAllExpenses() = expenseDao.clearAllExpenses()

    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)
    suspend fun clearAllCategories() = categoryDao.clearAllCategories()

    suspend fun updateBanner(banner: AdBannerEntity) = adBannerDao.insertOrUpdateBanner(banner)

    suspend fun insertFeedback(feedback: FeedbackEntity): Long = feedbackDao.insertFeedback(feedback)
    suspend fun updateFeedback(feedback: FeedbackEntity) = feedbackDao.updateFeedback(feedback)
    suspend fun clearAllFeedbacks() = feedbackDao.clearAllFeedbacks()

    suspend fun insertNotification(notification: AppNotificationEntity): Long =
        notificationDao.insertNotification(notification)
    suspend fun clearAllNotifications() = notificationDao.clearAllNotifications()
}
