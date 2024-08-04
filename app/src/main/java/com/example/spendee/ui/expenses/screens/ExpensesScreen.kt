package com.example.spendee.ui.expenses.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.ui.expenses.components.ExpensesColumn
import com.example.spendee.util.AnimatedVisibilityComposable

@Composable
fun ExpensesScreen(modifier: Modifier = Modifier) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {}, shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_expense))
            }
        }
    ) { _ ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            AnimatedVisibilityComposable(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                ExpensesColumn()
            }
        }
    }
}

@Preview
@Composable
private fun ExpensesScreenPreview() {
    ExpensesScreen()
}