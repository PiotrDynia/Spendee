package com.example.spendee.ui.budget.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.spendee.data.entities.Budget
import com.example.spendee.ui.budget.BudgetState

@Composable
fun BudgetCircle(
    percentageSpent: Float,
    animatedProgress: Float,
    budget: Budget,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
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

                when (percentageSpent) {
                    0f -> {
                        drawArc(
                            color = Color(0xFF04AF70),
                            startAngle = -90f + gapAngle / 2,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            topLeft = rect.topLeft,
                            size = Size(rect.width, rect.height),
                            style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                    1f -> {
                        drawArc(
                            color = Color.Red,
                            startAngle = -90f + gapAngle / 2,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            topLeft = rect.topLeft,
                            size = Size(rect.width, rect.height),
                            style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                    else -> {
                        drawArc(
                            color = Color(0xFF04AF70),
                            startAngle = -90f + gapAngle / 2,
                            sweepAngle = percentageSpent * 360f * animatedProgress - gapAngle,
                            useCenter = false,
                            topLeft = rect.topLeft,
                            size = Size(rect.width, rect.height),
                            style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                        )
                        drawArc(
                            color = Color.Red,
                            startAngle = -90f + percentageSpent * 360f * animatedProgress + gapAngle / 2,
                            sweepAngle = (1f - percentageSpent) * 360f * animatedProgress - gapAngle,
                            useCenter = false,
                            topLeft = rect.topLeft,
                            size = Size(rect.width, rect.height),
                            style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                }
            }
    ) {
        BudgetTexts(budget = budget)
    }
}