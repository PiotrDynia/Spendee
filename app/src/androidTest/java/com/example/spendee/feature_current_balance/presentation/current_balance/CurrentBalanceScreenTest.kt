package com.example.spendee.feature_current_balance.presentation.current_balance

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
class CurrentBalanceScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockViewModel: CurrentBalanceViewModel
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        hiltRule.inject()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
            uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        }

        mockViewModel = mock(CurrentBalanceViewModel::class.java)

        composeRule.activity.setContent {
            val navController = rememberNavController()
            SpendeeTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.CURRENT_BALANCE
                ) {
                    composable(Routes.CURRENT_BALANCE) {
                        CurrentBalanceScreen(
                            onNavigate = { route -> navController.navigate(route) },
                            onShowMoreClick = {
                                navController.navigate(Routes.EXPENSES)
                            }
                        )
                    }
                }
            }
        }
    }

    @Test
    fun clickingButtonOpensDialog() {
        composeRule
            .onNodeWithText(context.getString(R.string.set_balance))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(context.getString(R.string.cancel))
            .assertIsDisplayed()
    }

    @Test
    fun clickingCancelClosesDialog() {
        composeRule
            .onNodeWithText(context.getString(R.string.set_balance))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.cancel))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.cancel))
            .assertIsNotDisplayed()
    }

    @Test
    fun clickingOkClosesDialog() {
        composeRule
            .onNodeWithText(context.getString(R.string.set_balance))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .performClick()
        composeRule
            .onNodeWithText(context.getString(R.string.ok))
            .assertIsNotDisplayed()
    }
}