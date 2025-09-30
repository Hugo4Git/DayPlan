package com.hdy.plan.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color
import java.time.LocalTime
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import kotlin.math.min
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@Composable
fun TimePicker(
    initial: LocalTime,
    onPicked: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var minutes by remember(initial) {
        mutableIntStateOf((initial.hour * 60 + initial.minute).roundToQuarterHour())
    }
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // full-screen
    ) {
        val haptics = LocalHapticFeedback.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .onGloballyPositioned { coords -> layoutSize = coords.size }
                .pointerInput(layoutSize) {
                    detectDragGestures(
                        onDragStart = { pos ->
                            val c = Offset(layoutSize.width / 2f, layoutSize.height / 2f)
                            val angle = ((atan2(pos.y - c.y, pos.x - c.x) + (PI / 2.0)) % (2 * PI))
                                .let { if (it < 0) it + 2 * PI else it }
                            val raw = (angle / (2 * PI) * 1440.0).roundToInt()
                            val next = raw.roundToQuarterHour()
                            if(next != minutes){
                                minutes = next
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        onDrag = { change, _ ->
                            val c = Offset(layoutSize.width / 2f, layoutSize.height / 2f)
                            val angle = ((atan2(change.position.y - c.y, change.position.x - c.x) + (PI / 2.0)) % (2 * PI))
                                .let { if (it < 0) it + 2 * PI else it }
                            val raw = (angle / (2 * PI) * 1440.0).roundToInt()
                            val next = raw.roundToQuarterHour()
                            if(next != minutes){
                                minutes = next
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            change.consume()
                        },
                        onDragEnd = {
//                            haptics.performHapticFeedback(HapticFeedbackType.LongPress) not sure about this yet
                            onPicked(LocalTime.of(minutes / 60, minutes % 60))
                        }
                    )
                }
        ) {
            Text(
                text = minutes.toHhMm(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 28.dp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontSize = 36.sp
            )

            val density = LocalDensity.current
            val diameterDp = with(density) {
                val w = layoutSize.width.toFloat()
                val h = layoutSize.height.toFloat()
                (min(w, h) * 0.72f).toDp()
            }
            ClockFace(
                minutes = minutes,
                diameter = diameterDp
            )

            Text(
                text = "Drag around the clock (15-min steps).",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ClockFace(
    minutes: Int,
    diameter: Dp
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(diameter)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rOuter = size.minDimension / 2f
            val rTick = rOuter - 10.dp.toPx()
            val rHand = rOuter - 28.dp.toPx()

            // Outer circle
            drawCircle(
                color = surfaceColor,
                radius = rOuter,
                style = Stroke(width = 3.dp.toPx())
            )

            // 24 ticks
            repeat(24) { i ->
                val a = (i / 24f) * 2f * PI.toFloat()
                val sx = cx + rTick * sin(a)
                val sy = cy - rTick * cos(a)
                val ex = cx + rOuter * sin(a)
                val ey = cy - rOuter * cos(a)
                drawLine(
                    color = onSurfaceColor,
                    start = Offset(sx, sy),
                    end = Offset(ex, ey),
                    strokeWidth = if (i % 3 == 0) 5f else 3f
                )
            }

            // Hand for selected time
            val angle = (minutes / 1440f) * 2f * PI.toFloat()
            val hx = cx + rHand * sin(angle)
            val hy = cy - rHand * cos(angle)
            drawLine(
                color = primaryColor,
                start = Offset(cx, cy),
                end = Offset(hx, hy),
                strokeWidth = 10f
            )

            // Center cap
            drawCircle(
                color = primaryColor,
                radius = 8.dp.toPx(),
                center = Offset(cx, cy)
            )
        }
    }
}

private fun Int.roundToQuarterHour(): Int {
    val q = 15
    val up = (this + q / 2) / q * q
    if(up == 1440) return 0
    return up.coerceIn(0, 1439)
}

private fun Int.toHhMm(): String {
    val h = this / 60
    val m = this % 60
    return String.format(Locale.US, "%02d:%02d", h, m)
}
