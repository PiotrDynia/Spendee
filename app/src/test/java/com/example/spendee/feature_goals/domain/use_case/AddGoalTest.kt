package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.R
import com.example.spendee.feature_current_balance.data.repository.FakeBalanceRepository
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_goals.data.repository.FakeGoalsRepository
import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.model.InvalidGoalException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AddGoalTest {

    private lateinit var addGoal: AddGoal
    private lateinit var balanceRepository: FakeBalanceRepository
    private lateinit var goalsRepository: FakeGoalsRepository

    @Before
    fun setUp() {
        balanceRepository = FakeBalanceRepository()
        goalsRepository = FakeGoalsRepository()
        addGoal = AddGoal(goalsRepository, balanceRepository)
    }

    @Test
    fun `Add a goal with no target amount, throw an exception`() {
        val exception = assertThrows(InvalidGoalException::class.java) {
            runBlocking {
                addGoal(
                    Goal(
                        description = "Description",
                        targetAmount = 0.0,
                        deadline = LocalDate.now().plusDays(5),
                        isReached = false,
                        isReachedNotificationEnabled = false
                    )
                )
            }
        }
        assertEquals(R.string.target_amount_cant_be_empty, exception.messageResId)
    }

    @Test
    fun `Add a goal with no description, throw an exception`() {
        val exception = assertThrows(InvalidGoalException::class.java) {
            runBlocking {
                addGoal(
                    Goal(
                        description = "",
                        targetAmount = 30.0,
                        deadline = LocalDate.now().plusDays(5),
                        isReached = false,
                        isReachedNotificationEnabled = false
                    )
                )
            }
        }
        assertEquals(R.string.description_cant_be_empty, exception.messageResId)
    }

    @Test
    fun `Add a goal with past deadline, throw an exception`() {
        val exception = assertThrows(InvalidGoalException::class.java) {
            runBlocking {
                addGoal(
                    Goal(
                        description = "Description",
                        targetAmount = 30.0,
                        deadline = LocalDate.now().minusDays(3),
                        isReached = false,
                        isReachedNotificationEnabled = false
                    )
                )
            }
        }
        assertEquals(R.string.deadline_should_be_after_todays_date, exception.messageResId)
    }

    @Test
    fun `Add a goal with today's date, throw an exception`() {
        val exception = assertThrows(InvalidGoalException::class.java) {
            runBlocking {
                addGoal(
                    Goal(
                        description = "Description",
                        targetAmount = 30.0,
                        deadline = LocalDate.now(),
                        isReached = false,
                        isReachedNotificationEnabled = false
                    )
                )
            }
        }
        assertEquals(R.string.deadline_should_be_after_todays_date, exception.messageResId)
    }

    @Test
    fun `Add a reached goal, set isReached to true`() = runBlocking {
        balanceRepository.upsertBalance(Balance(amount = 50.0))
        val addedGoal = Goal(
            id = 1,
            description = "Description",
            targetAmount = 30.0,
            deadline = LocalDate.now().plusDays(3),
            isReached = false,
            isReachedNotificationEnabled = false
        )
        addGoal(addedGoal)
        val fetchedGoal = goalsRepository.getGoalById(1)
        assertTrue(fetchedGoal!!.isReached)
    }

    @Test
    fun `Add a goal, does not throw`() = runBlocking {
        val addedGoal = Goal(
            id = 1,
            description = "Description",
            targetAmount = 30.0,
            deadline = LocalDate.now().plusDays(3),
            isReached = false,
            isReachedNotificationEnabled = false
        )
        try {
            addGoal(addedGoal)
        } catch(e: Exception) {
            fail("Expected no exception to be thrown, but got: ${e.message}")
        }
    }
}