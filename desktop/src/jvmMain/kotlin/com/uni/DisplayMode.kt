package com.uni

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@Composable
fun DisplayMode(lambda: GeneratedScope.() -> Unit) {
    Canvas(
        Modifier.wrapContentSize(Alignment.Center)
            .fillMaxSize()
    ) {
        val generatedScope = object : GeneratedScope {
            override fun drawCurve(points: List<Pt>) {
                if (points.isNotEmpty()) {
                    drawPath(
                        path = Path().apply {
                            val start = points[0]
                            moveTo(start.x, start.y)
                            (points.drop(1)).forEach {
                                lineTo(it.x, it.y)
                            }
                        },
                        Color.Blue,
                        style = Stroke(width = 2f)
                    )
                }
            }
        }
        generatedScope.lambda()
    }
}
