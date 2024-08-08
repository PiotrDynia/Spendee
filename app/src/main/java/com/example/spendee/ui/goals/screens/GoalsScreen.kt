package com.example.spendee.ui.goals.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.data.entities.Goal
import com.example.spendee.ui.goals.components.GoalCard
import java.util.Date

@Composable
fun GoalsScreen(modifier: Modifier = Modifier) {
    val exampleGoals = generateExampleGoals()
    val currentBalance = 1000.0

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_a_new_goal)
                )
            }
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.your_financial_goals),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(exampleGoals) { goal ->
                    GoalCard(goal = goal, currentBalance = currentBalance)
                }
            }
        }
    }
}

fun generateExampleGoals(): List<Goal> {
    return listOf(
        Goal(description = "Buy a new car", targetAmount = 15000.0, currentAmount = 100.0, isReachedNotificationEnabled = false, deadline = Date()),
        Goal(description = "Vacation to Hawaii", targetAmount = 5000.0, currentAmount = 100.0, isReachedNotificationEnabled = false, deadline = Date()),
        Goal(description = "Emergency Fund", targetAmount = 10000.0, currentAmount = 100.0, isReachedNotificationEnabled = false, deadline = Date())
    )
}