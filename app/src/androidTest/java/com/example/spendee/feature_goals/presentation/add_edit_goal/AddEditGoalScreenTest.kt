package com.example.spendee.feature_goals.presentation.add_edit_goal

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import java.time.LocalDate

@HiltAndroidTest
@UninstallModules(AppModule::class)
class AddEditGoalScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockViewModel: AddEditGoalViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        mockViewModel = mock(AddEditGoalViewModel::class.java)

        composeRule.activity.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.ADD_EDIT_GOAL
                ) {
                    composable(
                        route = Routes.ADD_EDIT_GOAL + "?goalId={goalId}",
                        arguments = listOf(navArgument("goalId") {
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ) {
                        AddEditGoalScreen(
                            onNavigate = { navController.navigate(it) },
                            onPopBackStack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun showsAllInputs() {
        composeRule
            .onNodeWithText(context.getString(R.string.target_amount))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.set_a_deadline))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.notify_me_when_i_reach_my_goal))
            .assertIsDisplayed()
    }

    @Test
    fun canToggleSwitch() {
        val switch = composeRule.onNode(isToggleable())

        switch.assertIsOff()
        switch.performClick()
        switch.assertIsOn()
        switch.performClick()
        switch.assertIsOff()
    }

    @Test
    fun targetAmountInputWorks() {
        val exampleValue = "30.0"
        composeRule
            .onNodeWithText(context.getString(R.string.target_amount))
            .performTextInput(exampleValue)
        composeRule
            .onNodeWithText(exampleValue)
            .assertIsDisplayed()
    }

    @Test
    fun descriptionInputWorks() {
        val exampleValue = "Example description"
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .performTextInput(exampleValue)
        composeRule
            .onNodeWithText(exampleValue)
            .assertIsDisplayed()
    }

    @Test
    fun calendarInputWorks() {
        val deadline = LocalDate.now().plusDays(5)
        val deadlineDayOfWeek = deadline.dayOfWeek.name
        val deadlineMonth = deadline.month.name
        val deadlineMonthDay = deadline.dayOfMonth
        val deadlineYear = deadline.year
        val dateString = "$deadlineDayOfWeek, $deadlineMonth $deadlineMonthDay, $deadlineYear"
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
            .onNodeWithText(dateToString(deadline))
            .assertIsDisplayed()
    }

    @Test
    fun floatingActionButtonIsClickable() {
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .assertHasClickAction()
    }
}