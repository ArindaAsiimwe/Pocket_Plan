package com.example.pocketplan.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pocketplan.ui.components.PocketPlanTopBar
import com.example.pocketplan.ui.components.SectionHeader
import com.example.pocketplan.ui.theme.*
import com.example.pocketplan.utils.CurrencyUtils
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PocketPlanTopBar(
                title = "Spending Insights",
                onNotificationClick = { },
                onLogoutClick = onLogoutClick
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Section 1: Overview
            Section1Overview(state)

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Monthly Trend
            Section2MonthlyTrend(state)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun Section1Overview(state: InsightsUiState) {
    Column {
        SectionHeader(title = "Overview")
        Spacer(modifier = Modifier.height(12.dp))

        // Total Spent Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TOTAL SPENT",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CurrencyUtils.formatToUGX(state.totalSpent.toDouble()),
                        style = MaterialTheme.typography.headlineMedium,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.percentageUsed.toInt()}% of monthly budget",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Small Donut for percentage
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                    Canvas(modifier = Modifier.size(60.dp)) {
                        drawArc(
                            color = ChipUnselected,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = PrimaryBlue,
                            startAngle = -90f,
                            sweepAngle = (state.percentageUsed / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "${state.percentageUsed.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stat Cards Side by Side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = "REMAINING",
                value = CurrencyUtils.formatToUGX(state.remaining.toDouble()),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "DAYS LEFT",
                value = "${state.daysLeft}",
                subtitle = "On track",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Spending by Category
        Text(text = "Spending by Category", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Donut
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                    val colors = listOf(PrimaryBlue, AccentTeal, WarningOrange, ErrorRed, SecondaryBlue)
                    Canvas(modifier = Modifier.size(100.dp)) {
                        var startAngle = -90f
                        state.categoryBreakdown.values.forEachIndexed { index, percent ->
                            val sweepAngle = percent * 360f
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 20.dp.toPx())
                            )
                            startAngle += sweepAngle
                        }
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Legend
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val colors = listOf(PrimaryBlue, AccentTeal, WarningOrange, ErrorRed, SecondaryBlue)
                    state.categoryBreakdown.entries.forEachIndexed { index, entry ->
                        LegendItem(
                            color = colors[index % colors.size],
                            label = entry.key,
                            percentage = (entry.value * 100).toInt()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, percentage: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label $percentage%",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun Section2MonthlyTrend(state: InsightsUiState) {
    Column {
        SectionHeader(
            title = "Monthly Trend",
            actionText = "LAST 4 MONTHS",
            onActionClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (state.monthlyTrend.isNotEmpty()) {
                    val model = entryModelOf(
                        state.monthlyTrend.mapIndexed { index, pair -> 
                            entryOf(index, pair.second.toFloat()) 
                        }
                    )
                    
                    Chart(
                        chart = columnChart(),
                        model = model,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(
                            valueFormatter = { value, _ ->
                                state.monthlyTrend.getOrNull(value.toInt())?.first ?: ""
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }
    }
}
