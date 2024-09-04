package com.example.spendee.feature_expenses.presentation.expenses

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spendee.MainActivity
import com.example.spendee.R
import com.example.spendee.core.domain.util.dateToString
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.di.AppModule
import com.example.spendee.feature_expenses.domain.model.Expense
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

@HiltAndroidTest
@UninstallModules(AppModule::class)
class ExpensesScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockViewModel: ExpensesViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        val mockUiEvent = mock(UiEvent::class.java)

        val expensesFlow = MutableStateFlow(
            listOf(
                Expense(
                    id = 1,
                    amount = 50.0,
                    description = "Example expense 1",
                    date = LocalDate.now(),
                    categoryId = 5
                ),
                Expense(
                    id = 2,
                    amount = 30.0,
                    description = "Example expense 2",
                    date = LocalDate.now(),
                    categoryId = 3
                ),
                Expense(
                    id = 3,
                    amount = 40.0,
                    description = "Example expense 3",
                    date = LocalDate.now(),
                    categoryId = 4
                ),
                Expense(
                    id = 4,
                    amount = 70.0,
                    description = "Example expense 4",
                    date = LocalDate.now(),
                    categoryId = 1
                )
            )
        )

        mockViewModel = mock(ExpensesViewModel::class.java).apply {
            whenever(expenses).thenReturn(expensesFlow)
            whenever(uiEvent).thenReturn(MutableStateFlow(mockUiEvent))
        }

        composeRule.activity.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.EXPENSES
                ) {
                    composable(Routes.EXPENSES) {
                        ExpensesScreen(
                            onNavigate = { navController.navigate(it) },
                            viewModel = mockViewModel
                        )
                    }
                }
            }
        }
    }

    @Test
    fun showNoExpensesTextWhenNoExpenses() {
        (mockViewModel.expenses as MutableStateFlow).value = emptyList()
        composeRule
            .onNodeWithText(context.getString(R.string.no_expenses_yet))
            .assertIsDisplayed()
    }

    @Test
    fun expensesArePresentAndClickable() {
        composeRule
            .onNodeWithText("Example expense 1 at ${dateToString(LocalDate.now())}")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText("Example expense 2 at ${dateToString(LocalDate.now())}")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText("Example expense 3 at ${dateToString(LocalDate.now())}")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText("Example expense 4 at ${dateToString(LocalDate.now())}")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun floatingActionButtonIsClickable() {
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.add_expense))
            .assertHasClickAction()
    }

    @Test
    fun optionsAppearAfterLongExpenseClick() {
        composeRule
            .onNodeWithText("Example expense 1 at ${dateToString(LocalDate.now())}")
            .performTouchInput { longClick() }
        composeRule
            .onNodeWithText(context.getString(R.string.edit))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.delete))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun expenseDeletedAfterRightSwipe() {
        composeRule
            .onNodeWithText("Example expense 1 at ${dateToString(LocalDate.now())}")
            .performTouchInput { swipeRight() }
        composeRule
            .onNodeWithText("Example expense 1 at ${dateToString(LocalDate.now())}")
            .assertIsNotDisplayed()
    }
}