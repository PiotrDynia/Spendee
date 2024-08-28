package com.example.spendee

import SpendeeTheme
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spendee.ui.budget.screens.AddEditBudgetScreen
import com.example.spendee.ui.budget.screens.BudgetScreen
import com.example.spendee.ui.budget.screens.NoBudgetScreen
import com.example.spendee.ui.budget.state.AddEditBudgetViewModel
import com.example.spendee.ui.budget.state.BudgetViewModel
import com.example.spendee.ui.current_balance.screens.CurrentBalanceScreen
import com.example.spendee.ui.current_balance.state.CurrentBalanceViewModel
import com.example.spendee.ui.expenses.screens.AddEditExpenseScreen
import com.example.spendee.ui.expenses.screens.ExpensesScreen
import com.example.spendee.ui.expenses.state.AddEditExpenseViewModel
import com.example.spendee.ui.expenses.state.ExpensesViewModel
import com.example.spendee.ui.goals.screens.AddEditGoalScreen
import com.example.spendee.ui.goals.screens.GoalsScreen
import com.example.spendee.ui.goals.screens.NoGoalsScreen
import com.example.spendee.ui.goals.state.AddEditGoalViewModel
import com.example.spendee.ui.goals.state.GoalsViewModel
import com.example.spendee.ui.navigation.BottomNavItem
import com.example.spendee.ui.navigation.BottomNavigationBar
import com.example.spendee.util.AnimatedVisibilityComposable
import com.example.spendee.util.LoadingScreen
import com.example.spendee.util.Routes
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(false)
        } else {
            mutableStateOf(true)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                hasNotificationPermission = true
            }
        }
    }

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
                            onShowMoreClick = {
                                selectedItem = Routes.EXPENSES
                                navController.navigate(Routes.EXPENSES)
                            },
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
                        val viewModel = hiltViewModel<BudgetViewModel>()
                        val budget = viewModel.budget
                        val isLoading = viewModel.isLoading.collectAsState().value
                        when {
                            isLoading -> {
                                LoadingScreen()
                            }
                            budget.collectAsState().value == null -> {
                                NoBudgetScreen(
                                    onEvent = viewModel::onEvent,
                                    onNavigate = { navController.navigate(it) },
                                    uiEvent = viewModel.uiEvent
                                )
                            }
                            else -> {
                                BudgetScreen(
                                    budget = budget.collectAsState().value!!,
                                    onEvent = viewModel::onEvent,
                                    onNavigate = { navController.navigate(it) },
                                    uiEvent = viewModel.uiEvent
                                )
                            }
                        }
                    }
                    composable(Routes.GOALS) {
                        val viewModel = hiltViewModel<GoalsViewModel>()
                        val goals = viewModel.goalsState.collectAsState(initial = emptyList()).value
                        val balance = viewModel.balanceState.collectAsState().value
                        val isLoading = viewModel.isLoading.collectAsState().value

                        when {
                            isLoading -> {
                                LoadingScreen()
                            }
                            goals.isEmpty() -> {
                                NoGoalsScreen(
                                    onEvent = viewModel::onEvent,
                                    onNavigate = { navController.navigate(it) },
                                    uiEvent = viewModel.uiEvent
                                )
                            }
                            else -> {
                                GoalsScreen(
                                    goals = goals,
                                    balance = balance!!,
                                    onEvent = viewModel::onEvent,
                                    onNavigate = { navController.navigate(it) },
                                    uiEvent = viewModel.uiEvent
                                )
                            }
                        }
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
                            onNavigate = { route -> navController.navigate(route) },
                            onPopBackStack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Routes.ADD_EDIT_BUDGET + "?isCreated={isCreated}",
                        arguments = listOf(
                            navArgument(name = "isCreated") {
                                type = NavType.BoolType
                                defaultValue = false
                            }
                        )
                    ) {
                        val viewModel = hiltViewModel<AddEditBudgetViewModel>()
                        AddEditBudgetScreen(
                            onEvent = viewModel::onEvent,
                            uiEvent = viewModel.uiEvent,
                            state = viewModel.state.collectAsState().value,
                            onNavigate = { route -> navController.navigate(route) },
                            onPopBackStack = { navController.popBackStack() }
                        )
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
                        val viewModel = hiltViewModel<AddEditGoalViewModel>()
                        AddEditGoalScreen(
                            onEvent = viewModel::onEvent,
                            uiEvent = viewModel.uiEvent,
                            state = viewModel.state.collectAsState().value,
                            onNavigate = { route -> navController.navigate(route) },
                            onPopBackStack = { navController.popBackStack() }
                        )
                    }
                }
                initialRoute?.let { route ->
                    LaunchedEffect(route) {
                        selectedItem = route
                        navController.navigate(route)
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
        MainScreen()
    }
}