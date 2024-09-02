package com.example.spendee.feature_budget.domain.use_case

import com.example.spendee.feature_budget.data.repository.FakeBudgetRepository
import com.example.spendee.feature_budget.domain.model.Budget
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DeleteBudgetTest {
    private lateinit var deleteBudget: DeleteBudget
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var budget: Budget

    @Before
    fun setUp() {
        budgetRepository = FakeBudgetRepository()
        deleteBudget = DeleteBudget(budgetRepository)
        budget = Budget(
            totalAmount = 100.0,
            leftToSpend = 80.0,
            totalSpent = 20.0,
            startDate = LocalDate.now().minusDays(3),
            endDate = LocalDate.now().plusDays(3),
            isExceeded = true,
            isExceedNotificationEnabled = true
        )
        runBlocking {
            budgetRepository.upsertBudget(
                Budget(
                    totalAmount = 100.0,
                    leftToSpend = 80.0,
                    totalSpent = 20.0,
                    startDate = LocalDate.now().minusDays(3),
                    endDate = LocalDate.now().plusDays(3),
                    isExceeded = true,
                    isExceedNotificationEnabled = true
                )
            )
        }
    }

    @Test
    fun `Correctly delete budget`() = runBlocking {
        deleteBudget(budget)

        assert(budgetRepository.getBudget().firstOrNull() == null)
    }

}