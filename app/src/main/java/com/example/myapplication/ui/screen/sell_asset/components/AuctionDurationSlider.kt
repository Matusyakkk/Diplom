package com.example.myapplication.ui.screen.sell_asset.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.TimeUnit

@Composable
fun AuctionDurationSlider(
    selectedTime: Int,
    timeUnit: TimeUnit,
    onUnitChange: (TimeUnit) -> Unit,
    onTimeChange: (Int) -> Unit
) {
    val (valueRange, unitText) = when (timeUnit) {
        TimeUnit.MINUTES -> 1f..59f to "хвилин"
        TimeUnit.HOURS -> 1f..24f to "годин"
        TimeUnit.DAYS -> 1f..30f to "днів"
    }
    Column {
        // Вибір одиниці часу
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeUnit.values().forEach { unit ->
                val isSelected = unit == timeUnit
                Text(
                    text = when (unit) {
                        TimeUnit.MINUTES -> "Хв"
                        TimeUnit.HOURS -> "Год"
                        TimeUnit.DAYS -> "Дні"
                    },
                    modifier = Modifier
                        .clickable { onUnitChange(unit) }
                        .background(
                            color = if (isSelected) Color.LightGray else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Тривалість аукціону (в днях):",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.Companion.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
        )
        Slider(
            value = selectedTime.toFloat(),
            onValueChange = { onTimeChange(it.toInt()) },
            valueRange = valueRange,
            steps = valueRange.endInclusive.toInt() - 1,
            modifier = Modifier.Companion.padding(horizontal = 16.dp)
        )
        Text(
            text = "Вибрано: $selectedTime $unitText",
            modifier = Modifier.Companion.padding(start = 16.dp)
        )
    }
}