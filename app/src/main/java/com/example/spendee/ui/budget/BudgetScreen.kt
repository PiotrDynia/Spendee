package com.example.spendee.ui.budget

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.util.AnimatedVisibilityComposable
import kotlinx.coroutines.delay

// TODO give an option to set notifications when exceeding the budget and when exceeding 80% of budget
@Composable
fun BudgetScreen(modifier: Modifier = Modifier) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "animated progress"
    )

    LaunchedEffect(Unit) {
        while (true) {
            animationProgress = 1f
            delay(2000)
        }
    }
    val totalBudget = 10000.0
    val amountSpent = 10000.0 - 2137.69
    val percentageSpent = amountSpent / totalBudget
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(72.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .aspectRatio(1f)
                .drawBehind {
                    val strokeWidth = 24.dp.toPx()
                    val halfStrokeWidth = strokeWidth / 2
                    val gapAngle = 2f
                    val rect = Rect(
                        halfStrokeWidth,
                        halfStrokeWidth,
                        size.width - halfStrokeWidth,
                        size.height - halfStrokeWidth
                    )
                    drawArc(
                        color = Color.Red,
                        startAngle = -90f + gapAngle / 2,
                        sweepAngle = (percentageSpent * 360f * animatedProgress - gapAngle).toFloat(),
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = Size(rect.width, rect.height),
                        style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                    )
                    drawArc(
                        color = Color(0xFF04AF70),
                        startAngle = (-90f + percentageSpent * 360f * animatedProgress + gapAngle / 2).toFloat(),
                        sweepAngle = ((1f - percentageSpent) * 360f * animatedProgress - gapAngle).toFloat(),
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = Size(rect.width, rect.height),
                        style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                    )
                }
        ) {
            AnimatedVisibilityComposable {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.you_can_spend),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "2137.69$",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF04AF70),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "From 10000.00$",
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "5 days left",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "4.07.2024-4.08.2024",
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        AnimatedVisibilityComposable {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF04AF70))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.you_can_spend), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.spent), fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibilityComposable {
            Button(
                onClick = { }
            ) {
                Text(text = stringResource(R.string.set_budget))
            }
        }
        AnimatedVisibilityComposable {
            Column {
                ElevatedCard(
                    onClick = { },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.spent),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp, 16.dp)
                                    .background(Color.Red)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "8000$",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.weight(2f))
                            Text(
                                text = "From 10000.00$",
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                ElevatedCard(
                    onClick = { },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.you_can_spend),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp, 16.dp)
                                    .background(Color(0xFF04AF70))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "8000$",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.weight(2f))
                            Text(
                                text = "100$/day",
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BudgetScreenPreview() {
    BudgetScreen()
}