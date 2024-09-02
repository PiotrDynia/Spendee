package com.example.spendee.feature_goals.domain.use_case

import com.example.spendee.feature_goals.data.repository.FakeGoalsRepository
import com.example.spendee.feature_goals.domain.model.Goal
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetGoalsTest {

    private lateinit var getGoals: GetGoals
    private lateinit var repository: FakeGoalsRepository

    @Before
    fun setUp() {
        repository = FakeGoalsRepository()
        getGoals = GetGoals(repository)
    }

    @Test
    fun `Retrieve all goals, correctly return`() = runBlocking {
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
        exampleGoals.forEach { repository.upsertGoal(it) }

        assertEquals(exampleGoals, getGoals().firstOrNull())
    }

    @Test
    fun `Retrieve empty list, correctly return`() = runBlocking {
        assertEquals(emptyList<Goal>(), getGoals().firstOrNull())
    }

}