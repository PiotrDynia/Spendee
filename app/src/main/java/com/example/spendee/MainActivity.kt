package com.example.spendee

import SpendeeTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.spendee.ui.budget.screens.AddEditBudgetScreen
import com.example.spendee.ui.budget.screens.BudgetScreen
import com.example.spendee.ui.current_balance.screens.CurrentBalanceScreen
import com.example.spendee.ui.expenses.screens.AddEditExpenseScreen
import com.example.spendee.ui.expenses.screens.ExpensesScreen
import com.example.spendee.util.AnimatedVisibilityComposable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendeeTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)) { _ ->
                    AnimatedVisibilityComposable {
                        CurrentBalanceScreen()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendeeTheme {
        CurrentBalanceScreen()
    }
}