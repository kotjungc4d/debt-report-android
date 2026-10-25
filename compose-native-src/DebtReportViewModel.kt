package com.fintech.debtreport.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DebtItemEntity(
    val id: String,
    val title: String,
    val amount: Double,
    val dueDate: String,
    val category: String,
    val isPaid: Boolean,
    val isNextMonth: Boolean = false,
    val paidAt: String? = null
)

@Serializable
data class CashflowEntity(
    val id: String,
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val category: String,
    val dateIso: String,
    val timeLabel: String
)

@Serializable
data class DebtReportUiState(
    val baseBudget: Double = 45000.0,
    val liveBalance: Double = 22450.0,
    val soundEnabled: Boolean = true,
    val selectedFilter: String = "HAS_AMOUNT",
    val debts: List<DebtItemEntity> = emptyList(),
    val cashflows: List<CashflowEntity> = emptyList()
)

class DebtReportViewModel(
    private val prefs: SharedPreferences
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<DebtReportUiState> = _uiState.asStateFlow()

    private fun loadInitialState(): DebtReportUiState {
        val raw = prefs.getString("debt_report_state_v1", null) ?: return DebtReportUiState()
        return runCatching { json.decodeFromString<DebtReportUiState>(raw) }
            .getOrDefault(DebtReportUiState())
    }

    private fun persist(state: DebtReportUiState) {
        prefs.edit().putString("debt_report_state_v1", json.encodeToString(state)).apply()
    }

    fun toggleDebtPaid(id: String, timestamp: String) {
        _uiState.update { current ->
            val target = current.debts.find { it.id == id } ?: return@update current
            val becomingPaid = !target.isPaid
            val updatedDebts = current.debts.map {
                if (it.id == id) it.copy(isPaid = becomingPaid, paidAt = if (becomingPaid) timestamp else null)
                else it
            }
            val updatedBalance = if (becomingPaid) {
                (current.liveBalance - target.amount).coerceAtLeast(0.0)
            } else {
                current.liveBalance + target.amount
            }
            current.copy(debts = updatedDebts, liveBalance = updatedBalance).also(::persist)
        }
    }

    fun updateLiveBalance(newBalance: Double) {
        _uiState.update { it.copy(liveBalance = newBalance.coerceAtLeast(0.0)).also(::persist) }
    }

    fun updateBaseBudget(newBudget: Double) {
        _uiState.update { it.copy(baseBudget = newBudget.coerceAtLeast(0.0)).also(::persist) }
    }
}