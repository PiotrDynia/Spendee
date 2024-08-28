package com.example.spendee.ui.expenses.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.data.entities.ExpenseCategory
import com.example.spendee.ui.expenses.AddEditExpenseEvent
import com.example.spendee.ui.expenses.AddEditExpenseState
import com.example.spendee.ui.expenses.components.CategoryCard
import com.example.spendee.util.UiEvent
import com.example.spendee.util.isValidNumberInput
import kotlinx.coroutines.flow.Flow

@Composable
fun AddEditExpenseScreen(
    onEvent: (AddEditExpenseEvent) -> Unit,
    state: AddEditExpenseState,
    uiEvent: Flow<UiEvent>,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when(event) {
                UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(context.getString(event.message))
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(AddEditExpenseEvent.OnSaveExpenseClick)
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save)
                )
            }
        }
    ) { _ ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.your_expense),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            TextField(
                value = state.amount,
                onValueChange = { amount ->
                    if (isValidNumberInput(amount)) {
                        onEvent(AddEditExpenseEvent.OnAmountChange(amount))
                    }
                },
                placeholder = { Text(stringResource(R.string.amount)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = state.description,
                onValueChange = {description ->
                    if (description.isNotBlank()) {
                        onEvent(AddEditExpenseEvent.OnDescriptionChange(description))
                    }
                },
                placeholder = {
                    Text(text = stringResource(R.string.description))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.select_a_category), fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(ExpenseCategory.getAllCategories()) { category ->
                    CategoryCard(
                        drawable = category.iconResource,
                        isSelected = category.id == state.categoryId,
                        onClick = { onEvent(AddEditExpenseEvent.OnCategoryChange(category.id)) },
                        text = category.name
                    )
                }
            }
        }
    }
}