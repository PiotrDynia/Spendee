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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Goal
import com.example.spendee.ui.goals.GoalsEvent
import com.example.spendee.ui.goals.components.GoalCard
import com.example.spendee.util.UiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun GoalsScreen(goals: List<Goal>,
                balance: Balance,
                onEvent: (GoalsEvent) -> Unit,
                uiEvent: Flow<UiEvent>,
                onNavigate: (String) -> Unit,
                modifier: Modifier = Modifier) {
    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> {
                    onNavigate(event.route)
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(GoalsEvent.OnAddGoalClick)
                },
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
                items(goals) { goal ->
                    GoalCard(goal = goal, onClick = { onEvent(GoalsEvent.OnGoalClick(goal))}, currentBalance = balance.amount)
                }
            }
        }
    }
}