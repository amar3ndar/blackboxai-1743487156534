package com.example.todoapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

// Custom shapes for specific components
val TodoCardShape = RoundedCornerShape(
    topStart = 8.dp,
    topEnd = 8.dp,
    bottomStart = 8.dp,
    bottomEnd = 8.dp
)

val QuadrantShape = RoundedCornerShape(16.dp)

val DialogShape = RoundedCornerShape(24.dp)

val FloatingActionButtonShape = RoundedCornerShape(16.dp)