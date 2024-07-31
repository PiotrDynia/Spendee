package com.example.spendee.ui.current_balance

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.data.entities.Expense
import com.example.spendee.data.entities.ExpenseCategory
import com.example.spendee.util.AnimatedVisibilityComposable
import com.example.spendee.util.formatDate
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun CurrentBalanceScreen(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val animatedCircleColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF60DDAD),
        targetValue = Color(0xFF4285F4),
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "color"
    )
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "animated progress"
    )

    var boxSize by remember { mutableStateOf(0.dp) }
    val animatedSize by animateDpAsState(
        targetValue = boxSize,
        animationSpec = tween(durationMillis = 2000),
        label = "animated size"
    )

    LaunchedEffect(Unit) {
        while (true) {
            animationProgress = 1f
            boxSize = 320.dp
            delay(2000)
        }
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp)
                .aspectRatio(1f)
                .drawBehind {
                    val strokeWidth = 4.dp.toPx()
                    val halfStrokeWidth = strokeWidth / 2
                    val rect = Rect(
                        halfStrokeWidth,
                        halfStrokeWidth,
                        size.width - halfStrokeWidth,
                        size.height - halfStrokeWidth
                    )
                    drawArc(
                        color = animatedCircleColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = Size(rect.width, rect.height),
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )
                }
        ) {
            AnimatedVisibilityComposable {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = stringResource(R.string.current_balance), fontSize = 14.sp)
                    Text(text = "2137.69$", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        AnimatedVisibilityComposable {
            Button(
                onClick = { }
            ) {
                Text(text = stringResource(R.string.set_balance))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .size(animatedSize)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        ) {
            AnimatedVisibilityComposable(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.latest_expenses),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(getExampleExpenses().take(3)) { item ->
                        Card(
                            shape = RoundedCornerShape(25.dp),
                            onClick = { },
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(ExpenseCategory.fromId(item.categoryId)!!.iconResource),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(
                                    modifier = Modifier
                                        .weight(100f)
                                ) {
                                    Text(
                                        text = ExpenseCategory.fromId(item.categoryId)!!.name,
                                        fontSize = 12.sp,
                                        fontStyle = FontStyle.Italic
                                    )
                                    Text(
                                        text = "${item.description} at ${formatDate(item.date)}",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "${item.amount}$", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    item {
                        Text(
                            text = stringResource(R.string.show_more),
                            color = Color(0xFF4228E9),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentBalanceScreenPreview() {
    CurrentBalanceScreen()
}

fun getExampleExpenses(): List<Expense> {
    return listOf(
        Expense(
            id = 0,
            amount = 30.0,
            description = "Movie",
            date = Date(),
            categoryId = 1
        ),
        Expense(
            id = 1,
            amount = 50.0,
            description = "Electricity Bill",
            date = Date(),
            categoryId = 2
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
    )
}