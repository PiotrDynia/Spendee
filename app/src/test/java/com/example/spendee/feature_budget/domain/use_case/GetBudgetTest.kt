package com.example.spendee.feature_budget.domain.use_case

import com.example.spendee.feature_budget.data.repository.FakeBudgetRepository
import com.example.spendee.feature_budget.domain.model.Budget
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetBudgetTest {
    private lateinit var getBudget: GetBudget
    private lateinit var budgetRepository: FakeBudgetRepository

    @Before
    fun setUp() {
        budgetRepository = FakeBudgetRepository()
        getBudget = GetBudget(budgetRepository)
    }

    @Test
    fun `Get budget, budget is equal to inserted`() = runBlocking{
        val insertedBudget = Budget(
            totalAmount = 100.0,
            leftToSpend = 80.0,
            totalSpent = 20.0,
            startDate = LocalDate.now().minusDays(3),
            endDate = LocalDate.now().plusDays(3),
            isExceeded = true,
            isExceedNotificationEnabled = true
        )
        budgetRepository.upsertBudget(insertedBudget)
        assertEquals(budgetRepository.getBudget().firstOrNull(), insertedBudget)
    }

    @Test
    fun `Get non existing budget, return null`() = runBlocking {
        assertNull(budgetRepository.getBudget().firstOrNull())
    }
}