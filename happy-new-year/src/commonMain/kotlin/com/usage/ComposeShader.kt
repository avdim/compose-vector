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

// https://github.com/Mishkun/ataman-intellij
// https://www.pushing-pixels.org/2021/09/22/skia-shaders-in-compose-desktop.html
// https://shaders.skia.org/?id=%40iMouse

@Composable
fun StarsAndSky(size:Size) {

  size.height
  val sksl =
"""
uniform float iTime;
float2 iResolution = float2(${size.width}, ${size.height});
            
// The iResolution uniform is always present and provides
// the canvas size in pixels. 
// The iResolution uniform is always present and provides
// the canvas size in pixels. 

// The iResolution uniform is always present and provides
// the canvas size in pixels. 

float rnd (vec2 uv) {
 return fract(sin(dot(uv.xy , vec2(12.9898,78.233))) * 43758.5453);
}

half4 main(float2 fragCoord) {
  float fx = fragCoord.x;
  float fy = fragCoord.y;
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
  
  float ANIMATION_SPEED1 = 3.0;
  float bottomY = 400;
  float yellowY = 360 + 40*sin(fx*0.05 + iTime*0.9*ANIMATION_SPEED1 + 3.0) + 40*sin(-fx*0.03 + iTime*0.4*ANIMATION_SPEED1 + 2.0);
  float greenY =  300 + 40*sin(-fx*0.02 - iTime*0.3*ANIMATION_SPEED1 + 2.0)  + 40*sin(fx*0.01 - iTime*0.7*ANIMATION_SPEED1 + 1.1);
  float blueY =   200 + 50*sin(-fx*0.04 + iTime*0.5*ANIMATION_SPEED1 + 2.5)  + 50*sin(fx*0.02 + iTime*0.5*ANIMATION_SPEED1 + 0.3);
  
  for(int i = 1; i < 8; i++) {
    r = r + 1.0;//next random
    float power = 0.4*(1.0 + rnd(r+10));
    float2 pt = float2(iResolution.x*rnd(r+11),bottomY);
    float dy = (pt.y - fragCoord.y);
    dy = dy / (1 + sign(dy));
    float dx = length(fragCoord.x - pt.x + 10*sin(fy*0.04 + iTime*(1+2*rnd(r+9))));
    
    float3 yellow = float3(0.7, 1.0, 0.0) * (1 - length(yellowY - fy)/yellowY);
    float3 green = float3(0.0, 1.0, 0.0) * (1 - length(greenY - fy)/greenY);
    float3 blue =  float3(0.0, 0.0, 1.0) * (1 - length(blueY - fy)/blueY);
    float3 northenLightColor = (yellow+green+blue)/(0.1 + sqrt(dy))/(2.0 + pow(dx, 0.25));
    northenLightColor = northenLightColor * power;
    result = result + northenLightColor;
  }
  
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
