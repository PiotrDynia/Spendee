package com.example.spendee

import SpendeeTheme
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spendee.core.presentation.navigation.BottomNavItem
import com.example.spendee.core.presentation.navigation.BottomNavigationBar
import com.example.spendee.core.presentation.util.AnimatedVisibilityComposable
import com.example.spendee.core.presentation.util.HandleNotificationPermission
import com.example.spendee.core.presentation.util.LoadingScreen
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.feature_budget.presentation.add_edit_budget.AddEditBudgetScreen
import com.example.spendee.feature_budget.presentation.budget.BudgetScreen
import com.example.spendee.feature_budget.presentation.budget.BudgetViewModel
import com.example.spendee.feature_budget.presentation.budget.NoBudgetScreen
import com.example.spendee.feature_current_balance.presentation.current_balance.CurrentBalanceScreen
import com.example.spendee.feature_expenses.presentation.add_edit_expense.AddEditExpenseScreen
import com.example.spendee.feature_expenses.presentation.expenses.ExpensesScreen
import com.example.spendee.feature_goals.presentation.add_edit_goal.AddEditGoalScreen
import com.example.spendee.feature_goals.presentation.add_edit_goal.AddEditGoalViewModel
import com.example.spendee.feature_goals.presentation.goals.GoalsScreen
import com.example.spendee.feature_goals.presentation.goals.GoalsViewModel
import com.example.spendee.feature_goals.presentation.goals.NoGoalsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val intentRoute = intent.getStringExtra("route")

            SpendeeTheme {
                MainScreen(initialRoute = intentRoute)
            }
        }
    }
}

@Composable
fun MainScreen(initialRoute: String? = null) {
    var hasNotificationPermission by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
    }

    HandleNotificationPermission(onPermissionResult = { isGranted ->
        hasNotificationPermission = isGranted
    })

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedItem = selectedItem,
                onNavigate = { route ->
                    selectedItem = route
                    navController.navigate(route)
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AnimatedVisibilityComposable {
                SetupNavHost(
                    navController = navController,
                    initialRoute = initialRoute,
                    onItemSelected = { route -> selectedItem = route }
                )
            }
        }
    }
}

@Composable
fun SetupNavHost(
    navController: NavHostController,
    initialRoute: String?,
    onItemSelected: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.CURRENT_BALANCE) {
        composable(Routes.CURRENT_BALANCE) {
            CurrentBalanceScreen(
                onNavigate = { route -> navController.navigate(route) },
                onShowMoreClick = {
                    onItemSelected(Routes.EXPENSES)
                    navController.navigate(Routes.EXPENSES)
                }
            )
        }
        composable(Routes.EXPENSES) {
            ExpensesScreen(
                onNavigate = { navController.navigate(it) },
            )
        }
        composable(Routes.BUDGET) {
            val viewModel = hiltViewModel<BudgetViewModel>()
            val budget = viewModel.budget
            val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value
            when {
                isLoading -> LoadingScreen()
                budget.collectAsStateWithLifecycle().value == null -> NoBudgetScreen(
                    onEvent = viewModel::onEvent,
                    onNavigate = { navController.navigate(it) },
                    uiEvent = viewModel.uiEvent
                )
                else -> BudgetScreen(
                    budget = budget.collectAsStateWithLifecycle().value!!,
                    onEvent = viewModel::onEvent,
                    onNavigate = { navController.navigate(it) },
                    uiEvent = viewModel.uiEvent
                )
            }
        }
        composable(Routes.GOALS) {
            val viewModel = hiltViewModel<GoalsViewModel>()
            val goals = viewModel.goalsState.collectAsStateWithLifecycle(initialValue = emptyList()).value
            val balance = viewModel.balanceState.collectAsStateWithLifecycle().value
            val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value

            when {
                isLoading -> LoadingScreen()
                goals.isEmpty() -> NoGoalsScreen(
                    onEvent = viewModel::onEvent,
                    onNavigate = { navController.navigate(it) },
                    uiEvent = viewModel.uiEvent
                )
                else -> GoalsScreen(
                    goals = goals,
                    balance = balance!!,
                    onEvent = viewModel::onEvent,
                    onNavigate = { navController.navigate(it) },
                    uiEvent = viewModel.uiEvent
                )
            }
        }
        composable(
            route = Routes.ADD_EDIT_EXPENSE + "?expenseId={expenseId}",
            arguments = listOf(navArgument("expenseId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            AddEditExpenseScreen(
                onNavigate = { route -> navController.navigate(route) },
                onPopBackStack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.ADD_EDIT_BUDGET + "?isCreated={isCreated}",
            arguments = listOf(navArgument("isCreated") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) {
            AddEditBudgetScreen(
                onNavigate = { route -> navController.navigate(route) },
                onPopBackStack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.ADD_EDIT_GOAL + "?goalId={goalId}",
            arguments = listOf(navArgument("goalId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            AddEditGoalScreen(
                onNavigate = { route -> navController.navigate(route) },
                onPopBackStack = { navController.popBackStack() }
            )
        }
    }

    initialRoute?.let { route ->
        LaunchedEffect(route) {
            onItemSelected(route)
            navController.navigate(route)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendeeTheme {
        MainScreen()
    }
}