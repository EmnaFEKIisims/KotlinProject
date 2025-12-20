package com.example.smartshop.ui.product

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background

@Composable
fun StatsChart(
    totalProducts: Int,
    totalStockValue: Double
) {
    val primaryPink = Color(0xFFFF6B8B)
    val pastelBlue = Color(0xFF6A9BFF)
    val darkText = Color(0xFF2D2B55)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Stock Distribution",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = darkText
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pie Chart
            Box(
                modifier = Modifier.size(120.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val total = 100f // Max value for visualization
                    val productsAngle = (totalProducts.coerceAtMost(100) / total) * 360f
                    val valueAngle: Float = (((totalStockValue / 1000).coerceIn(0.0, 100.0) / total) * 360).toFloat()

                    // Products slice
                    drawArc(
                        color = primaryPink,
                        startAngle = 0f,
                        sweepAngle = productsAngle,
                        useCenter = true
                    )

                    // Stock value slice
                    drawArc(
                        color = pastelBlue,
                        startAngle = productsAngle,
                        sweepAngle = valueAngle,
                        useCenter = true
                    )

                    // Center circle
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension * 0.3f
                    )

                    // Border
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension * 0.5f,
                        style = Stroke(width = 2f)
                    )
                }
            }

            // Legend
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(primaryPink, shape = RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Products: $totalProducts items",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = darkText.copy(alpha = 0.8f)
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(pastelBlue, shape = RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Value: ${"%.2f".format(totalStockValue)} DT",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = darkText.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}