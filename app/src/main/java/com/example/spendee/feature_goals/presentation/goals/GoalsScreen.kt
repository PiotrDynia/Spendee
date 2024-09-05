package com.example.spendee.feature_goals.presentation.goals

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spendee.R
import com.example.spendee.core.presentation.util.DismissBackground
import com.example.spendee.core.presentation.util.LoadingScreen
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_goals.presentation.goals.components.GoalCard
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GoalsScreen(onNavigate: (String) -> Unit,
                modifier: Modifier = Modifier,
                viewModel: GoalsViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val goals = viewModel.goalsState.collectAsStateWithLifecycle(initialValue = emptyList()).value
    val balance = viewModel.balanceState.collectAsStateWithLifecycle().value
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> {
                    onNavigate(event.route)
                }
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(event.message),
                        actionLabel = context.getString(event.action!!),
                        duration = SnackbarDuration.Long
                    )
                    if(result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(GoalsEvent.OnUndoDeleteGoal)
                    }
                }
                else -> Unit
            }
        }
    }

    when {
        isLoading -> LoadingScreen()
        goals.isEmpty() -> NoGoalsScreen(
            onEvent = viewModel::onEvent,
            onNavigate = { onNavigate(it) },
            uiEvent = viewModel.uiEvent
        )
        else -> Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(GoalsEvent.OnAddGoalClick)
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_a_new_goal)
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    items(items = goals, key = { it.id }) { goal ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                when(it) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        viewModel.onEvent(GoalsEvent.OnDeleteGoal(goal))
                                    }
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        viewModel.onEvent(GoalsEvent.OnGoalClick(goal))
                                    }
                                    SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
                                }
                                return@rememberSwipeToDismissBoxState true
                            },
                            positionalThreshold = { it * .25f }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = { DismissBackground(dismissState = dismissState) },
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .animateItemPlacement()
                        ) {
                            GoalCard(
                                goal = goal,
                                onEvent = viewModel::onEvent,
                                currentBalance = balance!!.amount
                            )
                        }
                    }
                }
            }
        }
    }
}