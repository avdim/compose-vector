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

  size.height
  val sksl =
"""
uniform float iTime;
float2 iResolution = float2(${size.width}, ${size.height});
            
// The iResolution uniform is always present and provides
// the canvas size in pixels. 

float rnd (vec2 uv) {
 return fract(sin(dot(uv.xy , vec2(12.9898,78.233))) * 43758.5453);
}

half4 main(float2 fragCoord) {
  vec2 r = vec2(0,0);//random seed
  float brightness = 0;
  float3 result = float3(0,0,0);
  for (int i = 0; i < 30; i++) {
    r = r + 1.0;//next random
    float2 p = float2(iResolution.x*rnd(r + 0), iResolution.y*rnd(r + 1));
    float distanceX = length(fragCoord.x - p.x);
    float distanceY = length(fragCoord.y - p.y);
    float distance = length(fragCoord - p);
    float pulse = 1.0 + 0.45*sin(rnd(r+3)*100 + iTime*(4 + 2*rnd(r+4)));
    brightness = 2.1 * pulse * 1/((distanceX+0.1)*(distanceY+0.1)*distance);
    
    float3 color = float3(0.4 + rnd(r + 5), 0.3 + rnd(r+6), 1.0 + rnd(r+7));
    result = result + color * brightness;
  }
  
//  float d = length(fragCoord - float2(0,0));
//  float3 northenLightColor = float3(1.0,1.0,1.0)/d;
//  result = result + northenLightColor;
  return half4(3.0 * result, 1.0);
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
        val delta = nanosPassed / 1_000_000_000f
        if (previousNanos > 0.0f) {
          timeUniform -= delta
        }
        previousNanos = frameTimeNanos
      }
    }
  }

}
