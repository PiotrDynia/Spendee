package com.example.spendee.ui.current_balance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R

@Composable
fun CurrentBalanceTexts(currentBalance: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.current_balance),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(text = "${currentBalance}$", fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}