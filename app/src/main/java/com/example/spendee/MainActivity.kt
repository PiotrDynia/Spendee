package com.example.spendee

import SpendeeTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spendee.ui.budget.screens.AddEditBudgetScreen
import com.example.spendee.ui.budget.screens.BudgetScreen
import com.example.spendee.ui.current_balance.CurrentBalanceViewModel
import com.example.spendee.ui.current_balance.screens.CurrentBalanceScreen
import com.example.spendee.ui.expenses.AddEditExpenseViewModel
import com.example.spendee.ui.expenses.ExpensesViewModel
import com.example.spendee.ui.expenses.screens.AddEditExpenseScreen
import com.example.spendee.ui.expenses.screens.ExpensesScreen
import com.example.spendee.ui.goals.screens.AddEditGoalScreen
import com.example.spendee.ui.goals.screens.GoalsScreen
import com.example.spendee.ui.navigation.BottomNavItem
import com.example.spendee.ui.navigation.BottomNavigationBar
import com.example.spendee.util.AnimatedVisibilityComposable
import com.example.spendee.util.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendeeTheme {
                val navController = rememberNavController()
                val items = listOf(
                    BottomNavItem(
                        stringResource(R.string.home),
                        painterResource(R.drawable.ic_home),
                        Routes.CURRENT_BALANCE
                    ),
                    BottomNavItem(
                        stringResource(R.string.expenses),
                        painterResource(R.drawable.ic_money),
                        Routes.EXPENSES
                    ),
                    BottomNavItem(
                        stringResource(R.string.budget),
                        painterResource(R.drawable.ic_budget),
                        Routes.BUDGET
                    ),
                    BottomNavItem(
                        stringResource(R.string.goals),
                        painterResource(R.drawable.ic_goals),
                        Routes.GOALS
                    )
                )

                var selectedItem by remember { mutableStateOf(items.first().route) }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            items = items,
                            selectedItem = selectedItem,
                            onNavigate = { route ->
                                selectedItem = route
                                navController.navigate(route)
                            }
                        )
                    })
                { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AnimatedVisibilityComposable {
                            NavHost(
                                navController = navController,
                                startDestination = Routes.CURRENT_BALANCE
                            ) {
                                composable(Routes.CURRENT_BALANCE) {
                                    val viewModel = hiltViewModel<CurrentBalanceViewModel>()
                                    CurrentBalanceScreen(
                                        state = viewModel.viewState.collectAsState().value,
                                        onEvent = viewModel::onEvent,
                                        onNavigate = { route -> navController.navigate(route) },
                                        onShowMoreClick = { selectedItem = Routes.EXPENSES
                                            navController.navigate(Routes.EXPENSES) },
                                        uiEvent = viewModel.uiEvent
                                    )
                                }
                                composable(Routes.EXPENSES) {
                                    val viewModel = hiltViewModel<ExpensesViewModel>()
                                    ExpensesScreen(
                                        onEvent = viewModel::onEvent,
                                        expenses = viewModel.expenses.collectAsState(initial = emptyList()).value,
                                        onNavigate = { navController.navigate(it) },
                                        uiEvent = viewModel.uiEvent
                                    )
                                }
                                composable(Routes.BUDGET) {
                                    BudgetScreen()
                                }
                                composable(Routes.GOALS) {
                                    GoalsScreen()
                                }
                                composable(
                                    route = Routes.ADD_EDIT_EXPENSE + "?expenseId={expenseId}",
                                    arguments = listOf(
                                        navArgument(name = "expenseId") {
                                            type = NavType.IntType
                                            defaultValue = 0
                                        }
                                    )
                                ) {
                                    val viewModel = hiltViewModel<AddEditExpenseViewModel>()
                                    AddEditExpenseScreen(
                                        onEvent = viewModel::onEvent,
                                        uiEvent = viewModel.uiEvent,
                                        state = viewModel.state.collectAsState().value,
                                        onNavigate = { route -> navController.navigate(route)},
                                        onPopBackStack = { navController.popBackStack() }
                                    )
                                }
                                composable(
                                    route = Routes.ADD_EDIT_BUDGET + "?budgetId={budgetId}",
                                    arguments = listOf(
                                        navArgument(name = "budgetId") {
                                            type = NavType.IntType
                                            defaultValue = 1
                                        }
                                    )
                                ) {
                                    AddEditBudgetScreen()
                                }
                                composable(
                                    route = Routes.ADD_EDIT_GOAL + "?goalId={goalId}",
                                    arguments = listOf(
                                        navArgument(name = "goalId") {
                                            type = NavType.IntType
                                            defaultValue = 0
                                        }
                                    )
                                ) {
                                    AddEditGoalScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendeeTheme {
    }
}