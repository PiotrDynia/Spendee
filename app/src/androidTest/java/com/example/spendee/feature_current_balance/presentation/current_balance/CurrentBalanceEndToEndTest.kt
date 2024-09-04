package com.example.spendee.feature_current_balance.presentation.current_balance

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spendee.MainActivity
import com.example.spendee.R
import com.example.spendee.core.domain.util.dateToString
import com.example.spendee.core.presentation.navigation.BottomNavItem
import com.example.spendee.core.presentation.navigation.BottomNavigationBar
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.di.AppModule
import com.example.spendee.feature_expenses.presentation.add_edit_expense.AddEditExpenseScreen
import com.example.spendee.feature_expenses.presentation.expenses.ExpensesScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@HiltAndroidTest
@UninstallModules(AppModule::class)
class CurrentBalanceEndToEndTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        composeRule.activity.setContent {
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

            val onNavigate: (String) -> Unit = { route ->
                selectedItem = route
                navController.navigate(route)
            }

            SpendeeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            items = items,
                            selectedItem = selectedItem,
                            onNavigate = onNavigate
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Routes.CURRENT_BALANCE
                        ) {
                            composable(Routes.CURRENT_BALANCE) {
                                CurrentBalanceScreen(
                                    onNavigate = { navController.navigate(it) },
                                    onShowMoreClick = {
                                        navController.navigate(Routes.EXPENSES)
                                    }
                                )
                            }
                            composable(Routes.EXPENSES) {
                                ExpensesScreen(
                                    onNavigate = { navController.navigate(it) },
                                )
                            }
                            composable(
                                route = Routes.ADD_EDIT_EXPENSE + "?expenseId={expenseId}",
                                arguments = listOf(navArgument("expenseId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                })
                            ) {
                                AddEditExpenseScreen(
                                    onNavigate = { navController.navigate(it) },
                                    onPopBackStack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addingExpensesUpdatesBalanceCorrectly() {
        val balanceAmount = "5000.0"
        var balanceAmountDouble = balanceAmount.toDouble()

        // 1. Set balance
        composeRule
            .onNodeWithText(context.getString(R.string.set_balance))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.balance))
            .performTextClearance()
        composeRule
            .onNodeWithText(context.getString(R.string.balance))
            .performTextInput(balanceAmount)
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .performClick()
        composeRule
            .onNodeWithText("$balanceAmount$")
            .assertIsDisplayed()

        // 2. Add expenses
        composeRule
            .onNodeWithText(context.getString(R.string.expenses))
            .performClick()
        for (i in 1..5) {
            balanceAmountDouble -= i
            composeRule.waitUntilAtLeastOneExists(
                hasContentDescription(context.getString(R.string.add_expense)),
                timeoutMillis = 3000
            )
            composeRule
                .onNodeWithContentDescription(context.getString(R.string.add_expense))
                .performClick()
            composeRule
                .onNodeWithText(context.getString(R.string.amount))
                .performTextInput(i.toString())
            composeRule
                .onNodeWithText(context.getString(R.string.description))
                .performTextInput(i.toString())
            composeRule
                .onNodeWithText(context.getString(R.string.entertainment))
                .performClick()
            composeRule
                .onNodeWithContentDescription(context.getString(R.string.save))
                .performClick()
        }

        // 3. Go back to balance and check it's updated
        composeRule
            .onNodeWithText(context.getString(R.string.home))
            .performClick()
        composeRule
            .onNodeWithText("$balanceAmountDouble$")
            .assertIsDisplayed()

        // 4. Check that three latest expenses appear
        for (i in 1 .. 3) {
            composeRule
                .onNodeWithText("$i at ${dateToString(LocalDate.now())}")
                .assertIsDisplayed()
        }
        for (i in 4 .. 5) {
            composeRule
                .onNodeWithText("$i at ${dateToString(LocalDate.now())}")
                .assertIsNotDisplayed()
        }
    }
}