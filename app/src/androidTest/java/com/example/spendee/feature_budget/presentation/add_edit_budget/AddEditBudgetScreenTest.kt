package com.example.spendee.feature_budget.presentation.add_edit_budget

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

@HiltAndroidTest
@UninstallModules(AppModule::class)
class AddEditBudgetScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockViewModel: AddEditBudgetViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        mockViewModel = mock(AddEditBudgetViewModel::class.java)

        composeRule.activity.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.ADD_EDIT_BUDGET
                ) {
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
                }
            }
        }
    }

    @Test
    fun showsAllInputs() {
        composeRule
            .onNodeWithText(context.getString(R.string.enter_budget_amount))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.select_a_starting_day_of_the_month))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.notify_me_when_i_exceed_my_budget))
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
    fun amountInputWorks() {
        val exampleValue = "30.0"
        composeRule
            .onNodeWithText(context.getString(R.string.enter_budget_amount))
            .performTextInput(exampleValue)
        composeRule
            .onNodeWithText(exampleValue)
            .assertIsDisplayed()
    }

    @Test
    fun calendarInputWorks() {
        composeRule
            .onNodeWithText(context.getString(R.string.select_a_starting_day_of_the_month))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .performClick()
        composeRule
            .onNodeWithText("1")
            .assertIsDisplayed()
    }
}