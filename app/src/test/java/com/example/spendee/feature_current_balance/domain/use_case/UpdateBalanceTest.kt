package com.example.spendee.feature_current_balance.domain.use_case

import com.example.spendee.R
import com.example.spendee.core.domain.util.FakeNotificationService
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.feature_current_balance.data.repository.FakeBalanceRepository
import com.example.spendee.feature_current_balance.domain.model.InvalidBalanceException
import com.example.spendee.feature_goals.data.repository.FakeGoalsRepository
import com.example.spendee.feature_goals.domain.model.Goal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class UpdateBalanceTest {

    private lateinit var updateBalance: UpdateBalance
    private lateinit var balanceRepository: FakeBalanceRepository
    private lateinit var goalsRepository: FakeGoalsRepository
    private lateinit var notificationService: FakeNotificationService

    @Before
    fun setUp() {
        balanceRepository = FakeBalanceRepository()
        goalsRepository = FakeGoalsRepository()
        notificationService = FakeNotificationService()
        updateBalance = UpdateBalance(balanceRepository, goalsRepository, notificationService)
    }

    @Test
    fun `Update balance with empty amount, throw exception`() {
        val exception = assertThrows(InvalidBalanceException::class.java) {
            runBlocking {
                updateBalance("")
            }
        }
        assertEquals(R.string.amount_cant_be_empty, exception.messageResId)
    }

    @Test
    fun `Update balance, does not throw`() = runBlocking {
        val amount = "50"
        try {
            updateBalance(amount)
        } catch (e: Exception) {
            fail("Expected no exception to be thrown, but got: ${e.message}")
        }
    }

    @Test
    fun `Update balance, updates goals and show notification if enabled`() = runBlocking {
        val goals = listOf(
            Goal(
                id = 1,
                description = "Example goal 1",
                targetAmount = 150.0,
                deadline = LocalDate.now().plusDays(5),
                isReached = false,
                isReachedNotificationEnabled = false
            ),
            Goal(
                id = 2,
                description = "Example goal 2",
                targetAmount = 100.0,
                deadline = LocalDate.now().plusDays(5),
                isReached = false,
                isReachedNotificationEnabled = false
            ),
            Goal(
                id = 3,
                description = "Example goal 3",
                targetAmount = 120.0,
                deadline = LocalDate.now().plusDays(5),
                isReached = false,
                isReachedNotificationEnabled = true
            )
        )
        goals.forEach {
            goalsRepository.upsertGoal(it)
        }
        val balanceAmount = "130"
        updateBalance(balanceAmount)

        val firstGoal = goalsRepository.getGoalById(1)
        val secondGoal = goalsRepository.getGoalById(2)
        val thirdGoal = goalsRepository.getGoalById(3)

        assertFalse(firstGoal!!.isReached)
        assertTrue(secondGoal!!.isReached)
        assertTrue(thirdGoal!!.isReached)

        assertTrue(notificationService.wasNotificationShown(R.string.goal_reached, R.string.congratulations_you_have_reached_your_goal, Routes.GOALS))
        assertFalse(thirdGoal.isReachedNotificationEnabled)
    }
}