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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spendee.ui.budget.screens.AddEditBudgetScreen
import com.example.spendee.ui.budget.screens.BudgetScreen
import com.example.spendee.ui.current_balance.screens.CurrentBalanceScreen
import com.example.spendee.ui.expenses.screens.AddEditExpenseScreen
import com.example.spendee.ui.expenses.screens.ExpensesScreen
import com.example.spendee.ui.goals.screens.AddEditGoalScreen
import com.example.spendee.ui.goals.screens.GoalsScreen
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
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(
                        onNavigate = {
                            route -> navController.navigate(route)
                        }
                    ) })
                { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AnimatedVisibilityComposable {
                            // TODO highlight selected tab
                            NavHost(navController = navController, startDestination = Routes.CURRENT_BALANCE) {
                                composable(Routes.CURRENT_BALANCE) {
                                    CurrentBalanceScreen()
                                }
                                composable(Routes.EXPENSES) {
                                    ExpensesScreen()
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
                                    AddEditExpenseScreen()
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