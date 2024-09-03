package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.feature_goals.data.repository.FakeGoalsRepository
import com.example.spendee.feature_goals.domain.model.Goal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetGoalTest {

    private lateinit var getGoal: GetGoal
    private lateinit var repository: FakeGoalsRepository

    @Before
    fun setUp() {
        repository = FakeGoalsRepository()
        getGoal = GetGoal(repository)
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
    fun `Retrieve an existing goal, correctly return`() = runBlocking {
        val expectedGoal = Goal(
            id = 2,
            description = "Example goal 2",
            targetAmount = 100.0,
            deadline = LocalDate.now().plusDays(5),
            isReached = false,
            isReachedNotificationEnabled = false
        )
        val retrievedGoal = getGoal(2)
        assertEquals(expectedGoal, retrievedGoal)
    }

    @Test
    fun `Retrieve a non existing goal, return null`() = runBlocking {
        val retrievedGoal = getGoal(4)
        assertEquals(null, retrievedGoal)
    }
}