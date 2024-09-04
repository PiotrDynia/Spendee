package com.example.spendee.feature_expenses.presentation.add_edit_expense

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
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
class AddEditExpenseScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockViewModel: AddEditExpenseViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        mockViewModel = mock(AddEditExpenseViewModel::class.java)

        composeRule.activity.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.ADD_EDIT_EXPENSE
                ) {
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

    @Test
    fun showsAllInputs() {
        composeRule
            .onNodeWithText(context.getString(R.string.amount))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.select_a_category))
            .assertIsDisplayed()
    }

    @Test
    fun allCategoriesAreClickable() {
        composeRule
            .onNodeWithText(context.getString(R.string.entertainment))
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.payments))
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.transport))
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.personal))
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.house))
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.everyday))
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.health))
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.uncategorized))
            .assertHasClickAction()
    }

    @Test
    fun amountInputWorks() {
        val exampleValue = "30.0"
        composeRule
            .onNodeWithText(context.getString(R.string.amount))
            .performTextInput(exampleValue)
        composeRule
            .onNodeWithText(exampleValue)
            .assertIsDisplayed()
    }

    @Test
    fun amountDescriptionWorks() {
        val exampleValue = "Example description"
        composeRule
            .onNodeWithText(context.getString(R.string.description))
            .performTextInput(exampleValue)
        composeRule
            .onNodeWithText(exampleValue)
            .assertIsDisplayed()
    }

    @Test
    fun floatingActionButtonIsClickable() {
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.save))
            .assertHasClickAction()
    }
}