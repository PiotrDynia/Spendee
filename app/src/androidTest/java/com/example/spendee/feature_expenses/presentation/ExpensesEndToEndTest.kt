package com.example.spendee.feature_expenses.presentation

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
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
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
import com.example.spendee.core.presentation.navigation.BottomNavigationBar
import com.example.spendee.core.presentation.navigation.generateBottomNavItems
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.di.AppModule
import com.example.spendee.feature_current_balance.presentation.current_balance.CurrentBalanceScreen
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
class ExpensesEndToEndTest {

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
            val items = generateBottomNavItems()

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
                                    onNavigate = { navController.navigate(it) }
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
    fun addExpenseEditAfterwards() {
        val expenseAmount = "50.0"
        val expenseDescription = "Example description"

        // 1. Set balance so we can add an expense
        composeRule
            .onNodeWithText(context.getString(R.string.set_balance))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.balance))
            .performTextClearance()
        composeRule
            .onNodeWithText(context.getString(R.string.balance))
            .performTextInput("5000.0")
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .performClick()

        // 2. Add expense
        composeRule
            .onNodeWithText(context.getString(R.string.expenses))
            .performClick()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.add_expense))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.amount))
            .performTextInput(expenseAmount)
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .performTextInput(expenseDescription)
        composeRule
            .onNodeWithText(context.getString(R.string.entertainment))
            .performClick()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .performClick()

        // 3. Assert expense is present
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription(context.getString(R.string.add_expense)),
            timeoutMillis = 3000
        )
        composeRule
            .onNodeWithText("$expenseDescription at ${dateToString(LocalDate.now())}")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("$expenseAmount$")
            .assertIsDisplayed()

        // 4. Edit expense
        composeRule
            .onNodeWithText("$expenseAmount$")
            .performClick()
        composeRule
            .onNodeWithText(expenseAmount)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(expenseDescription)
            .assertIsDisplayed()

        val newAmount = "100.0"
        val newDescription = "New description"
        composeRule
            .onNodeWithText(expenseAmount)
            .performTextClearance()
        composeRule
            .onNodeWithText(context.getString(R.string.amount))
            .performTextInput(newAmount)
        composeRule
            .onNodeWithText(expenseDescription)
            .performTextClearance()
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .performTextInput(newDescription)
        composeRule
            .onNodeWithText(context.getString(R.string.everyday))
            .performClick()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .performClick()

        // 5. Check expense is present
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription(context.getString(R.string.add_expense)),
            timeoutMillis = 3000
        )
        composeRule
            .onNodeWithText("$newDescription at ${dateToString(LocalDate.now())}")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("$newAmount$")
            .assertIsDisplayed()
    }
}