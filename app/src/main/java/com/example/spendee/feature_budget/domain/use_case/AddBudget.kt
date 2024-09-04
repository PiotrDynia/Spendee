package com.example.spendee.feature_budget.domain.use_case

import com.example.spendee.R
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.model.InvalidBudgetException
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_budget.presentation.add_edit_budget.AddEditBudgetState
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.YearMonth

class AddBudget(
    private val budgetRepository: BudgetRepository,
    private val expensesRepository: ExpenseRepository
) {

    @Throws(InvalidBudgetException::class)
    suspend operator fun invoke(state: AddEditBudgetState) {
        val amount = state.amount
        val startingDay = state.startingDayInput

        if (amount.isBlank()) {
            throw InvalidBudgetException(R.string.amount_cant_be_empty)
        }
        if (startingDay == null) {
            throw InvalidBudgetException(R.string.please_select_a_starting_day)
        }

        val startDate = calculateStartDate(startingDay)
        val endDate = startDate.plusMonths(1).minusDays(1)
        val expenses = expensesRepository.getAllExpenses().first()
        val filteredExpenses = expenses.filter { it.date >= startDate }
        val expensesTotalAmount = filteredExpenses.sumOf { it.amount }
        val currentAmount = amount.toDouble() - expensesTotalAmount

        val budget = Budget(
            totalAmount = amount.toDouble(),
            leftToSpend = maxOf(currentAmount, 0.0),
            totalSpent = expensesTotalAmount,
            startDate = startDate,
            endDate = endDate,
            isExceeded = currentAmount < 0,
            isExceedNotificationEnabled = state.isExceedButtonPressed
        )

        budgetRepository.upsertBudget(budget)
    }

    private fun calculateStartDate(startingDay: Int): LocalDate {
        val today = LocalDate.now()
        val currentYearMonth = YearMonth.of(today.year, today.month)
        val validStartingDay = minOf(startingDay, currentYearMonth.lengthOfMonth())

        return if (startingDay < today.dayOfMonth) {
            LocalDate.of(today.year, today.month, validStartingDay)
        } else {
            val previousMonth = currentYearMonth.minusMonths(1)
            LocalDate.of(previousMonth.year, previousMonth.monthValue, validStartingDay)
        }
    }
}