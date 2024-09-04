package com.example.spendee.feature_goals.presentation.goals

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
import androidx.compose.ui.test.swipeRight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spendee.MainActivity
import com.example.spendee.R
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.di.AppModule
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_goals.domain.model.Goal
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
class GoalsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockViewModel: GoalsViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        val mockUiEvent = mock(UiEvent::class.java)

        val mockedBalance = MutableStateFlow(
            Balance(
                amount = 60.0
            )
        )

        val goalsFlow = MutableStateFlow(
            listOf(
                Goal(
                    id = 1,
                    targetAmount = 50.0,
                    description = "Example goal 1",
                    deadline = LocalDate.now().plusDays(5),
                    isReached = true,
                    isReachedNotificationEnabled = false
                ),
                Goal(
                    id = 2,
                    targetAmount = 70.0,
                    description = "Example goal 2",
                    deadline = LocalDate.now().plusDays(5),
                    isReached = false,
                    isReachedNotificationEnabled = false
                ),
                Goal(
                    id = 3,
                    targetAmount = 120.0,
                    description = "Example goal 3",
                    deadline = LocalDate.now().plusDays(5),
                    isReached = false,
                    isReachedNotificationEnabled = false
                ),
                Goal(
                    id = 4,
                    targetAmount = 500.0,
                    description = "Example goal 4",
                    deadline = LocalDate.now().plusDays(5),
                    isReached = false,
                    isReachedNotificationEnabled = false
                ),
            )
        )

        mockViewModel = mock(GoalsViewModel::class.java).apply {
            whenever(goalsState).thenReturn(goalsFlow)
            whenever(balanceState).thenReturn(mockedBalance)
            whenever(isLoading).thenReturn(MutableStateFlow(false))
            whenever(uiEvent).thenReturn(MutableStateFlow(mockUiEvent))
        }

        composeRule.activity.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.GOALS
                ) {
                    composable(Routes.GOALS) {
                        GoalsScreen(
                            onNavigate = { navController.navigate(it) },
                            viewModel = mockViewModel
                        )
                    }
                }
            }
        }
    }

    @Test
    fun showNoGoalsScreenWhenNoGoals() {
        (mockViewModel.goalsState as MutableStateFlow).value = emptyList()
        composeRule
            .onNodeWithText(context.getString(R.string.you_have_no_goals_set))
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(context.getString(R.string.add_a_financial_goal))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun goalsArePresentAndClickable() {
        composeRule
            .onNodeWithText("Example goal 1")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText("Example goal 2")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText("Example goal 3")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeRule
            .onNodeWithText("Example goal 4")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun goalsHaveReachedAndNotReachedIcons() {
        val goals = listOf(
                Goal(
                    id = 1,
                    targetAmount = 50.0,
                    description = "Example goal 1",
                    deadline = LocalDate.now().plusDays(5),
                    isReached = true,
                    isReachedNotificationEnabled = false
                ),
                Goal(
                    id = 2,
                    targetAmount = 70.0,
                    description = "Example goal 2",
                    deadline = LocalDate.now().plusDays(5),
                    isReached = false,
                    isReachedNotificationEnabled = false
                )
            )

        (mockViewModel.goalsState as MutableStateFlow).value = goals
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.goal_reached))
            .assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.goal_not_reached))
            .assertIsDisplayed()
    }

    @Test
    fun floatingActionButtonIsClickable() {
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.add_a_new_goal))
            .assertHasClickAction()
    }

    @Test
    fun optionsAppearAfterLongExpenseClick() {
        composeRule
            .onNodeWithText("Example goal 1")
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
    fun goalDeletedAfterRightSwipe() {
        composeRule
            .onNodeWithText("Example goal 1")
            .performTouchInput { swipeRight() }
        composeRule
            .onNodeWithText("Example goal 1")
            .assertIsNotDisplayed()
    }
}