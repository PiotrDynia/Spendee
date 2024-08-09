package com.example.spendee.ui.goals.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.util.DatePickerInput
import com.example.spendee.util.SwitchButtonRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalScreen(modifier: Modifier = Modifier) {
    val deadlineDateState = rememberDatePickerState()
    var isDeadlineDateOpen by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = {

            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save)
                )
            }
        }
    ) { _ ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.your_goal),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            TextField(
                value = "",
                onValueChange = {

                },
                placeholder = {
                    Text(text = stringResource(R.string.description))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 2
            )
            TextField(
                value = "",
                onValueChange = {
                },
                placeholder = { Text(text = stringResource(R.string.target_amount)) },
                modifier = Modifier.fillMaxWidth()
            )
            DatePickerInput(
                placeholder = R.string.set_a_deadline,
                value = "",
                onClick = { isDeadlineDateOpen = !isDeadlineDateOpen }
            )
            if (isDeadlineDateOpen) {
                DatePickerDialog(
                    onDismissRequest = { isDeadlineDateOpen = !isDeadlineDateOpen },
                    confirmButton = { }
                ) {
                    DatePicker(state = deadlineDateState)
                }
            }
            SwitchButtonRow(text = R.string.notify_me_when_i_reach_my_goal, onCheckedChange = {}, switchState = false)
        }
    }
}