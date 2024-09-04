package com.example.spendee.feature_current_balance.presentation.current_balance

import SpendeeTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spendee.MainActivity
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.di.AppModule
import com.example.spendee.feature_budget.presentation.add_edit_budget.AddEditBudgetScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
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
                    startDestination = Routes.ADD_EDIT_BUDGET
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
}