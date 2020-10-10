package com.example.util.simpletimetracker.domain.interactor

import com.example.util.simpletimetracker.domain.model.CardOrder
import com.example.util.simpletimetracker.domain.repo.PrefsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PrefsInteractor @Inject constructor(
    private val prefsRepo: PrefsRepo
) {

    suspend fun getFilteredTypes(): List<Long> = withContext(Dispatchers.IO) {
        prefsRepo.recordTypesFilteredOnChart
            .mapNotNull(String::toLongOrNull)
    }

    suspend fun setFilteredTypes(typeIdsFiltered: List<Long>) = withContext(Dispatchers.IO) {
        prefsRepo.recordTypesFilteredOnChart = typeIdsFiltered
            .map(Long::toString).toSet()
    }

    suspend fun getCardOrder(): CardOrder = withContext(Dispatchers.IO) {
        when (prefsRepo.cardOrder) {
            0 -> CardOrder.NAME
            1 -> CardOrder.COLOR
            2 -> CardOrder.MANUAL
            else -> CardOrder.NAME
        }
    }

    suspend fun setCardOrder(cardOrder: CardOrder) = withContext(Dispatchers.IO) {
        prefsRepo.cardOrder = when (cardOrder) {
            CardOrder.NAME -> 0
            CardOrder.COLOR -> 1
            CardOrder.MANUAL -> 2
        }
    }

    suspend fun getShowUntrackedInRecords(): Boolean = withContext(Dispatchers.IO) {
        prefsRepo.showUntrackedInRecords
    }

    suspend fun setShowUntrackedInRecords(isEnabled: Boolean) = withContext(Dispatchers.IO) {
        prefsRepo.showUntrackedInRecords = isEnabled
    }

    suspend fun getAllowMultitasking(): Boolean = withContext(Dispatchers.IO) {
        prefsRepo.allowMultitasking
    }

    suspend fun setAllowMultitasking(isEnabled: Boolean) = withContext(Dispatchers.IO) {
        prefsRepo.allowMultitasking = isEnabled
    }

    suspend fun getShowNotifications(): Boolean = withContext(Dispatchers.IO) {
        prefsRepo.showNotifications
    }

    suspend fun setShowNotifications(isEnabled: Boolean) = withContext(Dispatchers.IO) {
        prefsRepo.showNotifications = isEnabled
    }

    suspend fun getNumberOfCards(): Int = withContext(Dispatchers.IO) {
        prefsRepo.numberOfCards
    }

    suspend fun setNumberOfCards(cardSize: Int) = withContext(Dispatchers.IO) {
        prefsRepo.numberOfCards = cardSize
    }

    suspend fun setWidget(widgetId: Int, recordType: Long) = withContext(Dispatchers.IO) {
        prefsRepo.setWidget(widgetId, recordType)
    }

    suspend fun getWidget(widgetId: Int): Long = withContext(Dispatchers.IO) {
        prefsRepo.getWidget(widgetId)
    }

    suspend fun removeWidget(widgetId: Int) = withContext(Dispatchers.IO) {
        prefsRepo.removeWidget(widgetId)
    }

    suspend fun setCardOrderManual(cardsOrder: Map<Long, Long>) = withContext(Dispatchers.IO) {
        prefsRepo.setCardOrderManual(cardsOrder)
    }

    suspend fun getCardOrderManual(): Map<Long, Long> = withContext(Dispatchers.IO) {
        prefsRepo.getCardOrderManual()
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        prefsRepo.clear()
    }
}