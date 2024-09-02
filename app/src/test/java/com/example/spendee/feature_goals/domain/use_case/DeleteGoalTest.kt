package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.feature_goals.data.repository.FakeGoalsRepository
import com.example.spendee.feature_goals.domain.model.Goal
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DeleteGoalTest {

    private lateinit var deleteGoal: DeleteGoal
    private lateinit var repository: FakeGoalsRepository

    @Before
    fun setUp() {
        repository = FakeGoalsRepository()
        deleteGoal = DeleteGoal(repository)
        val exampleGoals = listOf(
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
        runBlocking {
            exampleGoals.forEach { repository.upsertGoal(it) }
        }
    }

    @Test
    fun `Delete goal, correctly change list size`() = runBlocking {
        val startingListSize = repository.getAllGoals().firstOrNull()?.size ?: 0
        val goalToDelete = repository.getGoalById(3)
        deleteGoal(goalToDelete!!)
        val expectedListSize = repository.getAllGoals().firstOrNull()?.size ?: 0

        assertEquals(startingListSize - 1, expectedListSize)
    }

    @Test
    fun `Delete all goals, list is empty`() = runBlocking {
        val allGoals = repository.getAllGoals().firstOrNull().orEmpty().toList()
        allGoals.forEach {
            deleteGoal(it)
        }

        assertEquals(emptyList<Goal>(), repository.getAllGoals().firstOrNull())
    }
}