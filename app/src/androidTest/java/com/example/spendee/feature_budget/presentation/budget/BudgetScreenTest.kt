package com.example.spendee.feature_budget.presentation.budget

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spendee.MainActivity
import com.example.spendee.R
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.di.AppModule
import com.example.spendee.feature_budget.domain.model.Budget
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
class BudgetScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockViewModel: BudgetViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        val mockUiEvent = mock(UiEvent::class.java)

        val budgetFlow = MutableStateFlow<Budget?>(
            Budget(
                totalAmount = 200.0,
                leftToSpend = 180.0,
                totalSpent = 20.0,
                startDate = LocalDate.now().minusDays(5),
                endDate = LocalDate.now().plusDays(5),
                isExceeded = false,
                isExceedNotificationEnabled = true
            )
        )

        mockViewModel = mock(BudgetViewModel::class.java).apply {
            whenever(budget).thenReturn(budgetFlow)
            whenever(isLoading).thenReturn(MutableStateFlow(false))
            whenever(uiEvent).thenReturn(MutableStateFlow(mockUiEvent))
        }

        composeRule.activity.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.BUDGET
                ) {
                    composable(route = Routes.BUDGET) {
                        BudgetScreen(
                            onNavigate = { navController.navigate(it) },
                            viewModel = mockViewModel
                        )
                    }
                }
            }
        }
    }

    @Test
    fun showNoBudgetScreenWhenNoBudget() {
        (mockViewModel.budget as MutableStateFlow).value = null
        composeRule
            .onNodeWithText(context.getString(R.string.you_have_no_budget_set))
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(context.getString(R.string.set_budget))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun showBudgetExceededTextWhenBudgetExceeded() {
        val exceededBudget = Budget(
            totalAmount = 200.0,
            leftToSpend = 0.0,
            totalSpent = 220.0,
            startDate = LocalDate.now().minusDays(5),
            endDate = LocalDate.now().plusDays(5),
            isExceeded = true,
            isExceedNotificationEnabled = true
        )
        (mockViewModel.budget as MutableStateFlow).value = exceededBudget

        composeRule
            .onNodeWithText(context.getString(R.string.budget_exceeded) + " by ${(exceededBudget.totalSpent - exceededBudget.totalAmount)}$!")
            .assertIsDisplayed()
    }

    @Test
    fun showBudgetScreenWhenBudgetIsPresent() {
        composeRule
            .onAllNodesWithText(context.getString(R.string.you_can_spend))
            .assertAreDisplayed()
        composeRule
            .onAllNodesWithText(context.getString(R.string.spent))
            .assertAreDisplayed()
    }

    @Test
    fun showOptionsMenuOnClick() {
        composeRule
            .onNodeWithText(context.getString(R.string.edit))
            .assertIsNotDisplayed()
        composeRule
            .onNodeWithText(context.getString(R.string.delete))
            .assertIsNotDisplayed()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.more_options))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.edit))
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText(context.getString(R.string.delete))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    private fun SemanticsNodeInteractionCollection.assertAreDisplayed(): SemanticsNodeInteractionCollection {
        fetchSemanticsNodes().forEachIndexed { index, _ ->
            get(index).assertIsDisplayed()
        }
        return this
    }
}