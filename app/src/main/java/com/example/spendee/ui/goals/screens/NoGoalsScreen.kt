package com.example.spendee.ui.goals.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.ui.goals.state.GoalsEvent
import com.example.spendee.util.UiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun NoGoalsScreen(onEvent: (GoalsEvent) -> Unit,
                  uiEvent: Flow<UiEvent>,
                  onNavigate: (String) -> Unit,
                  modifier: Modifier = Modifier) {
    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                else -> Unit
            }
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.you_have_no_goals_set),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = {
                onEvent(GoalsEvent.OnAddGoalClick)
            }) {
                Text(text = stringResource(R.string.add_a_financial_goal))
            }
        }
    }
}