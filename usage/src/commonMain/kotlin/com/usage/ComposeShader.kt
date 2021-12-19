package com.usage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import org.jetbrains.skia.Data
import org.jetbrains.skia.RuntimeEffect
import java.nio.ByteBuffer
import java.nio.ByteOrder

//https://github.com/Mishkun/ataman-intellij
//https://www.pushing-pixels.org/2021/09/22/skia-shaders-in-compose-desktop.html

@Composable
 fun ComposeShader(size:Size) {
  val sksl = """
            uniform float time;
            
            float f(vec3 p) {
                p.z += 10. + time;
                float a = p.z * .1;
                p.xy *= mat2(cos(a), sin(a), -sin(a), cos(a));
                return .1 - length(cos(p.xy) + sin(p.yz));
            }
            
            half4 main(vec2 fragcoord) { 
                vec3 d = .5 - fragcoord.xy1 / 500;
                vec3 p=vec3(0);
                for (int i = 0; i < 32; i++) p += f(p) * d;
                return ((sin(p) + vec3(2, 5, 9)) / length(p)).xyz1;
            }
        """

  val runtimeEffect = RuntimeEffect.makeForShader(sksl)
  val byteBuffer = remember { ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN) }
  var timeUniform by remember { mutableStateOf(0.0f) }
  var previousNanos by remember { mutableStateOf(0L) }

  val timeBits = byteBuffer.clear().putFloat(timeUniform).array()
  val shader = runtimeEffect.makeShader(
    uniforms = Data.makeFromBytes(timeBits),
    children = null,
    localMatrix = null,
    isOpaque = false
  )
  val brush = ShaderBrush(shader)

  Box(modifier = Modifier.fillMaxSize().drawBehind {
    drawRect(
      brush = brush, topLeft = Offset(0f, 0f), size = size
    )
  })

  LaunchedEffect(null) {
    while (true) {
      withFrameNanos { frameTimeNanos ->
        val nanosPassed = frameTimeNanos - previousNanos
        val delta = nanosPassed / 100000000f
        if (previousNanos > 0.0f) {
          timeUniform -= delta
        }
        previousNanos = frameTimeNanos
      }
    }
  }

}
