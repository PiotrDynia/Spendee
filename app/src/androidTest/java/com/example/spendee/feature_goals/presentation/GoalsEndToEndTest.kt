package com.example.spendee.feature_goals.presentation

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
import androidx.compose.ui.test.hasText
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
import com.example.spendee.feature_goals.presentation.add_edit_goal.AddEditGoalScreen
import com.example.spendee.feature_goals.presentation.goals.GoalsScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@HiltAndroidTest
@UninstallModules(AppModule::class)
class GoalsEndToEndTest {

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
                            startDestination = Routes.GOALS
                        ) {
                            composable(Routes.GOALS) {
                                GoalsScreen(
                                    onNavigate = { navController.navigate(it) }
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
    fun addGoalEditAfterwards() {
        val goalTargetAmount = "50.0"
        val goalDescription = "Example description"
        val goalDeadline = LocalDate.now().plusDays(10)
        val deadlineDayOfWeek = goalDeadline.dayOfWeek.name
        val deadlineMonth = goalDeadline.month.name
        val deadlineMonthDay = goalDeadline.dayOfMonth
        val deadlineYear = goalDeadline.year
        val dateString = "$deadlineDayOfWeek, $deadlineMonth $deadlineMonthDay, $deadlineYear"

        // 1. Add goal
        composeRule
            .onNodeWithText(context.getString(R.string.add_a_financial_goal))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.target_amount))
            .performTextInput(goalTargetAmount)
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .performTextInput(goalDescription)
        composeRule
            .onNodeWithText(context.getString(R.string.set_a_deadline))
            .performClick()
        composeRule
            .onNode(hasText(dateString, ignoreCase = true))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .performClick()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .performClick()

        // 2. Assert goal is present
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription(context.getString(R.string.add_a_new_goal)),
            timeoutMillis = 3000
        )
        composeRule
            .onNodeWithText(context.getString(R.string.target, goalTargetAmount))
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(goalDescription)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(dateToString(goalDeadline))
            .assertIsDisplayed()

        // 3. Edit goal
        composeRule
            .onNodeWithText(goalDescription)
            .performClick()
        composeRule
            .onNodeWithText(goalTargetAmount)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(goalDescription)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(dateToString(goalDeadline))
            .assertIsDisplayed()

        val newAmount = "100.0"
        val newDescription = "New description"
        val newDeadline = LocalDate.now().plusDays(20)
        val newDeadlineDayOfWeek = newDeadline.dayOfWeek.name
        val newDeadlineMonth = newDeadline.month.name
        val newDeadlineMonthDay = newDeadline.dayOfMonth
        val newDeadlineYear = newDeadline.year
        val newDateString = "$newDeadlineDayOfWeek, $newDeadlineMonth $newDeadlineMonthDay, $newDeadlineYear"
        composeRule
            .onNodeWithText(goalTargetAmount)
            .performTextClearance()
        composeRule
            .onNodeWithText(context.getString(R.string.target_amount))
            .performTextInput(newAmount)
        composeRule
            .onNodeWithText(goalDescription)
            .performTextClearance()
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .performTextInput(newDescription)
        composeRule
            .onNodeWithText(dateToString(goalDeadline))
            .performClick()
        composeRule
            .onNode(hasText(newDateString, ignoreCase = true))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .performClick()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .performClick()

        // 4. Check goal is present
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription(context.getString(R.string.add_a_new_goal)),
            timeoutMillis = 3000
        )
        composeRule
            .onNodeWithText(context.getString(R.string.target, newAmount))
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(newDescription)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(dateToString(newDeadline))
            .assertIsDisplayed()
    }
}