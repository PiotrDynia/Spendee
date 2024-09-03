package com.example.spendee.feature_budget.presentation.budget

import SpendeeTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spendee.R
import com.example.spendee.core.presentation.util.LoadingScreen
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@HiltAndroidTest
@UninstallModules(AppModule::class)
class BudgetScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    private lateinit var mockViewModel: BudgetViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        val mockUiEvent = mock(UiEvent::class.java)

        mockViewModel = mock(BudgetViewModel::class.java).apply {
            whenever(budget).thenReturn(MutableStateFlow(null))
            whenever(isLoading).thenReturn(MutableStateFlow(false))
            whenever(uiEvent).thenReturn(MutableStateFlow(mockUiEvent))
        }

        composeRule.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.BUDGET
                ) {
                    composable(route = Routes.BUDGET) {
                        val viewModel = mockViewModel
                        val budget = viewModel.budget.collectAsStateWithLifecycle().value
                        val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value

                        when {
                            isLoading -> LoadingScreen()
                            budget == null -> NoBudgetScreen(
                                onEvent = viewModel::onEvent,
                                onNavigate = { navController.navigate(it) },
                                uiEvent = viewModel.uiEvent
                            )
                            else -> BudgetScreen(
                                budget = budget,
                                onEvent = viewModel::onEvent,
                                onNavigate = { navController.navigate(it) },
                                uiEvent = viewModel.uiEvent
                            )
                        }

                    }
                }
            }
        }
    }

    @Test
    fun showNoBudgetScreenWhenNoBudget() {
        composeRule.onNodeWithText(context.getString(R.string.you_have_no_budget_set)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.set_budget)).assertIsDisplayed()
    }
}