variables used in an effect should be added as a parameter of the effect composable, or use rememberUpdatedState.


High level
```Kotlin
val color = animateColorAsState(if (condition) Color.Green else Color.Red)
```
Low level
```Kotlin
val color = remember { Animatable(Color.Gray) }
LaunchedEffect(condition) {
    color.animateTo(if (condition) Color.Green else Color.Red)
}
```
