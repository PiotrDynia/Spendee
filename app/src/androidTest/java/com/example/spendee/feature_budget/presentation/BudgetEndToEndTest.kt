package com.example.spendee.feature_budget.presentation

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
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
import com.example.spendee.core.presentation.navigation.BottomNavigationBar
import com.example.spendee.core.presentation.navigation.generateBottomNavItems
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.di.AppModule
import com.example.spendee.feature_budget.presentation.add_edit_budget.AddEditBudgetScreen
import com.example.spendee.feature_budget.presentation.budget.BudgetScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class BudgetEndToEndTest {

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
                            startDestination = Routes.BUDGET
                        ) {
                            composable(Routes.BUDGET) {
                                BudgetScreen(
                                    onNavigate = { onNavigate(it) }
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
                                    onNavigate = { onNavigate(it) },
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
    fun addBudgetEditAfterwards() {
        val budgetAmount = "300.0"

        // 1. Add budget
        composeRule
            .onNodeWithText(context.getString(R.string.set_budget))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.enter_budget_amount))
            .performTextInput(budgetAmount)
        composeRule
            .onNodeWithText(context.getString(R.string.select_a_starting_day_of_the_month))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .performClick()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .performClick()

        // 2. Assert budget is present
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription(context.getString(R.string.more_options)),
            timeoutMillis = 3000
        )
        composeRule
            .onNodeWithTag("BudgetTextsTotalAmount")
            .assertTextContains(context.getString(R.string.from, budgetAmount))
            .assertIsDisplayed()

        // 3. Edit budget
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.more_options))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.edit))
            .performClick()
        composeRule
            .onNodeWithText(budgetAmount)
            .assertIsDisplayed()

        val newAmount = "100.0"
        composeRule
            .onNodeWithText(budgetAmount)
            .performTextClearance()
        composeRule
            .onNodeWithText(context.getString(R.string.enter_budget_amount))
            .performTextInput(newAmount)
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .performClick()

        // 4. Check budget is present
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription(context.getString(R.string.more_options)),
            timeoutMillis = 3000
        )
        composeRule
            .onNodeWithTag("BudgetTextsTotalAmount")
            .assertTextContains(context.getString(R.string.from, newAmount))
            .assertIsDisplayed()
    }
}