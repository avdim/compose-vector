package lib.vector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.intellij.getClipboardImage
import lib.vector.intercept.interceptCubicBezier
import lib.vector.intercept.nearestBezierPoint
import lib.vector.utils.indexOfFirstOrNull
import lib.vector.utils.toByteArray
import java.awt.Point
import java.awt.image.BufferedImage

const val CURVE_PRECISION = 25f
const val CLOSE_DISTANCE = 5.0

sealed class DrawOptions {
  class Edit: DrawOptions()
  data class Curve(val color: ULong = Color.Blue.value) : DrawOptions()
  data class Rect(val color: ULong = Color.Yellow.value) : DrawOptions()
  data class Img(val image: ImageBitmap? = null) : DrawOptions()
}

data class ControllerState(val name: String, val options: DrawOptions)

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun EditMode(modifier: Modifier, lambda: GeneratedScope.() -> Unit) {
  val initState = initializeByGeneratedScope(lambda)
  // State
  var mapIdToPoint: Map<Id, Pt> by remember { mutableStateOf(initState.mapIdToPoint) }
  var savedElements: List<Element> by remember { mutableStateOf(initState.savedElements) }

  var controllers by remember {
    mutableStateOf(
      listOf(
        ControllerState("Edit", DrawOptions.Edit()),
        ControllerState("Curve", DrawOptions.Curve()),
        ControllerState("Rectangle", DrawOptions.Rect()),
        ControllerState("Image", DrawOptions.Img()),
      )
    )
  }
  var selectedControllerIndex by remember { mutableStateOf(controllers.indexOfFirstOrNull { it.options is DrawOptions.Curve } ?: 0) }
  val controllerState: ControllerState = controllers[selectedControllerIndex]
  fun replaceChangeOptions(lambda: () -> DrawOptions) {
    controllers = controllers.toMutableList().also {
      it[selectedControllerIndex] = it[selectedControllerIndex].copy(
        options = lambda()
      )
    }
  }

  fun addPoint(pt: Pt): Id {
    val id = getNextPointId()
    mapIdToPoint = mapIdToPoint + Pair(id, pt)
    return id
  }

  // UI
  var editPanelIsOpen by remember { mutableStateOf(true) }
  if (editPanelIsOpen) {
    Window(
      state = WindowState(width = 400.dp, height = 800.dp, position = WindowPosition(0.dp, 0.dp)),
      onCloseRequest = {
        editPanelIsOpen = false
      }) {
      Row {
        Column {
          TxtButton("Clear") {
            // todo Are you sure?
            savedElements = emptyList()
            mapIdToPoint = emptyMap()
          }
          controllers.forEachIndexed { index, it ->
            fun onClick() {
              selectedControllerIndex = index
            }
            Row(Modifier.clickable {
              onClick()
            }) {
              RadioButton(controllerState == it, onClick = {
                onClick()
              })
              Text(it.name)
            }
          }
        }
        Column {
          Text("${controllerState.name} settings:")
          val options = controllerState.options
          when (options) {
            is DrawOptions.Curve -> {
              ColorPicker(options.color) {
                replaceChangeOptions { options.copy(color = it) }
              }
            }
            is DrawOptions.Rect -> {
              ColorPicker(options.color) {
                replaceChangeOptions { options.copy(color = it) }
              }
            }
            is DrawOptions.Img -> {
              TxtButton("get clipboard image") {
                val img: BufferedImage? = getClipboardImage()
                if (img != null) {
                  replaceChangeOptions { options.copy(image = img.toComposeImageBitmap()) }
                }
              }
              options.image?.let {
                Image(
                  BitmapPainter(it),
                  contentDescription = null,
                  modifier = Modifier.size(it.width.dp, it.height.dp),
                  contentScale = ContentScale.Crop
                )
              }
            }
          }
        }
      }
    }
  }

  var currentPoints by remember { mutableStateOf<List<Id>>(emptyList()) }
  val currentElement: Element? by derivedStateOf {
    when (controllerState.options) {
      is DrawOptions.Edit -> null
      is DrawOptions.Curve -> {
        val fullLength = currentPoints.windowed(2).sumOf { (a: Id, b: Id) -> a.pt(mapIdToPoint) distance b.pt(mapIdToPoint) }
        val threshold = fullLength / CURVE_PRECISION

        class PointWithNeighbours(var prev: Int, val current: Int, var next: Int) {
          val currentPoint = currentPoints[current].pt(mapIdToPoint)
          val prevDistance get() = currentPoints[prev].pt(mapIdToPoint) distance currentPoint
          val nextDistance get() = currentPoints[next].pt(mapIdToPoint) distance currentPoint
          val sumDistance get() = prevDistance + nextDistance
        }

        val temp = currentPoints.indices.windowed(3).map { (prev, current, next) ->
          PointWithNeighbours(prev, current, next)
        }.toMutableList()

        fun getTempByIndex(index: Int) = temp.firstOrNull { it.current == index }
        temp.sortBy { it.sumDistance }
        while (temp.isNotEmpty() && temp.first().sumDistance < threshold) {
          val removed = temp.removeFirst()
          getTempByIndex(removed.prev)?.next = removed.next
          getTempByIndex(removed.next)?.prev = removed.prev
          temp.sortBy { it.sumDistance }
        }
        Element.Curve(
          color = controllerState.options.color,
          points = currentPoints.takeOrSmaller(1) +
            currentPoints.filterIndexed { i, _ -> temp.any { it.current == i } } +
            currentPoints.takeLastOrSmaller(1),
          bezierRef = emptyMap()
        )
      }
      is DrawOptions.Rect -> {
        if (currentPoints.size >= 2) {
          Element.Rectangle(controllerState.options.color, currentPoints.first(), currentPoints.last())
        } else {
          null
        }
      }
      is DrawOptions.Img -> {
        val img = controllerState.options.image
        if (img != null && currentPoints.isNotEmpty()) {
          Element.Bitmap(
            currentPoints.last(),
            byteArray = img.toByteArray()
          )
        } else {
          null
        }
      }
    }
  }

  fun pointerStart(pt: Pt) {}
  fun pointerMove(pt: Pt) {}
  fun pointerEnd(pt: Pt) {
    if (currentPoints.isNotEmpty()) {
      val e = currentElement
      if (e != null) {
        savedElements = savedElements + e
      }
    }
  }

  DisplayMode(
    modifier = modifier.pointerInput(Unit) {
      while (true) {
        val event = awaitPointerEventScope { awaitPointerEvent() }
        val point = event.awtEvent.point
        if (event.buttons.areAnyPressed || event.changes.lastOrNull()?.type == PointerType.Touch) {
          val previousPoints: List<Id> = currentPoints
          if (previousPoints.isEmpty()) {
            currentPoints = listOf(addPoint(point.pt))
            pointerStart(point.pt)
          } else {
            currentPoints = previousPoints + addPoint(point.pt)
            pointerMove(point.pt)
          }
        } else {
          if (event.keyboardModifiers.isShiftPressed.not()) {
            pointerEnd(point.pt)
            currentPoints = emptyList()

            //Iterate and remove unused points
            val usedIds = mutableSetOf<Id>()
            savedElements.forEach { e ->
              when (e) {
                is Element.Curve -> {
                  usedIds.addAll(e.points)
                  usedIds.addAll(e.bezierRef.values.flatMap { listOfNotNull(it.startRef, it.endRef) })
                }
                is Element.Rectangle -> {
                  usedIds.add(e.start)
                  usedIds.add(e.end)
                }
                is Element.Bitmap -> {
                  usedIds.add(e.topLeft)
                }
              }
            }
            mapIdToPoint = mapIdToPoint.filterKeys { usedIds.contains(it) }
          }
        }
      }
    }
  ) {
    (savedElements + listOfNotNull(currentElement)).forEach { e ->
      when (e) {
        is Element.Curve -> {
          drawCurve(e.color, e.points.pts(mapIdToPoint), e.bezierRef.pt(mapIdToPoint))
        }
        is Element.Rectangle -> drawRect(e.color, e.start.pt(mapIdToPoint), e.end.pt(mapIdToPoint))
        is Element.Bitmap -> drawBitmap(e.topLeft.pt(mapIdToPoint), e.byteArray)
      }
    }
  }

  if (controllerState.options is DrawOptions.Edit) {
    //Selection
    var currentSelectedId: Id? by remember { mutableStateOf(null) }
    var currentMousePoint: Pt by remember { mutableStateOf(Pt(0,0)) }
    val bezierPoints: List<BezierPt> by derivedStateOf {
      buildList {
        savedElements.filterIsInstance<Element.Curve>().forEach { curve ->
          val points = curve.points
          points.toLineSegments().forEach { s ->
            val idStart = curve.bezierRef[s.start]?.startRef
            val idEnd = curve.bezierRef[s.end]?.endRef
            val result = s.map { it.pt(mapIdToPoint) }.bezierSegment(startRef = idStart?.pt(mapIdToPoint), idEnd?.pt(mapIdToPoint))
            add(
              BezierPt(result.refStart, idStart, curve, s.start, true)
            )
            add(
              BezierPt(result.refEnd, idEnd, curve, s.end, false)
            )
          }
        }
      }
    }

    class InterceptedPoint(
      val pt: Pt,
      val curve1: Element.Curve,
      val t1: Float,
      val pointIndex1: Int,
      val curve2: Element.Curve,
      val t2: Float,
      val pointIndex2: Int
    )

    val allSegments by derivedStateOf {
      val elements = savedElements
      elements.filterIsInstance<Element.Curve>().flatMap { curve ->
        curve.points.toLineSegments().mapIndexed { i: Int, it ->
          CurveSegment(
            curve = curve,
            pointIndex = i,
            bezierSegment = it.map { it.pt(mapIdToPoint) }.bezierSegment(
              startRef = curve.bezierRef[it.start]?.startRef?.pt(mapIdToPoint),
              endRef = curve.bezierRef[it.end]?.endRef?.pt(mapIdToPoint),
            )
          )
        }
      }
    }

    val interceptedPoints: List<InterceptedPoint> by derivedStateOf {
      buildList<InterceptedPoint> {
        for (i in 0 until allSegments.size) {
          for (j in i + 1 until allSegments.size) {
            //todo N^2
            val a = allSegments[i]
            val b = allSegments[j]
            val interceptedPoints = interceptCubicBezier(a.bezierSegment, b.bezierSegment)
            interceptedPoints.relativePointsA.zip(interceptedPoints.relativePointsB).forEach { (ta, tb) ->
              a.bezierSegment.point(ta)
              b.bezierSegment.point(tb)
              val result = InterceptedPoint(a.bezierSegment.point(ta), a.curve, ta, a.pointIndex, b.curve, tb, b.pointIndex)
              if (mapIdToPoint.values.none { result.pt distance it < CLOSE_DISTANCE }) {
                add(result)
              }
            }
          }
        }

      }
    }

    Canvas(
      modifier.wrapContentSize(Alignment.Center)
        .fillMaxSize()
        .pointerInput(Unit) {
          while (true) {
            val event = awaitPointerEventScope { awaitPointerEvent() }
            val point = event.mouseEvent?.point
            if (point != null) {
              val mousePt = point.pt
              if(event.buttons.isPrimaryPressed) {
                if (currentSelectedId != null) {
                  mapIdToPoint = mapIdToPoint.toMutableMap().apply {
                    set(currentSelectedId!!, mousePt)
                  }
                } else {
                  val actions: MutableMap<Double, () -> Unit> = mutableMapOf()
                  fun regCandidate(distance:Double, action: () -> Unit) {
                    actions[distance] = action
                  }
                  val nearestCurvePoint = mapIdToPoint.minByOrNull { point.pt distance it.value }
                  if (nearestCurvePoint != null) {
                    regCandidate(mousePt distance nearestCurvePoint.value) {
                      currentSelectedId = nearestCurvePoint.key
                    }
                  }

                  val nearestBezierRef: BezierPt? = bezierPoints.minByOrNull { bezierPt ->
                    point.pt distance (bezierPt.id?.let { mapIdToPoint[it] } ?: bezierPt.refPt)
                  }
                  if (nearestBezierRef != null) {
                    regCandidate(mousePt distance nearestBezierRef.refPt) {
                      // create new pt
                      val id = nearestBezierRef.id
                      if (id == null) {
                        val newId = addPoint(point.pt)
                        savedElements = savedElements.toMutableList().apply {
                          val index = indexOf(nearestBezierRef.curve)
                          if (index >= 0) {
                            set(
                              index,
                              nearestBezierRef.curve.copy(
                                bezierRef = nearestBezierRef.curve.bezierRef.toMutableMap().apply {
                                  val previous = (get(nearestBezierRef.key) ?: BezierRefEdit(null, null))
                                  if (nearestBezierRef.isRefA) {
                                    set(nearestBezierRef.key, previous.copy(startRef = newId))
                                  } else {
                                    set(nearestBezierRef.key, previous.copy(endRef = newId))
                                  }
                                }
                              )
                            )
                          }
                        }
                        currentSelectedId = newId
                      } else {
                        currentSelectedId = id
                      }
                    }
                  }
                  val nearestIntercepted = interceptedPoints.minByOrNull {
                    point.pt distance (it.pt)
                  }
                  if (nearestIntercepted != null) {
                    regCandidate(mousePt distance nearestIntercepted.pt) {
                      val newId = addPoint(nearestIntercepted.pt)
                      savedElements = savedElements.toMutableList().apply {
                        val i1 = indexOf(nearestIntercepted.curve1)
                        val i2 = indexOf(nearestIntercepted.curve2)
                        if (i1 == -1 || i2 == -1) {
                          println("i1 == -1 || i2 == -1")
                        }
                        set(
                          i1,
                          (get(i1) as Element.Curve).copy(
                            points = (get(i1) as Element.Curve).points.toMutableList().apply {
                              add(nearestIntercepted.pointIndex1 + 1, newId)
                            }
                          )
                        )
                        set(
                          i2,
                          (get(i2) as Element.Curve).copy(
                            points = (get(i2) as Element.Curve).points.toMutableList().apply {
                              if (nearestIntercepted.curve1 == nearestIntercepted.curve2 && nearestIntercepted.pointIndex2 > nearestIntercepted.pointIndex1) {
                                add(nearestIntercepted.pointIndex2 + 2, newId)
                              } else {
                                add(nearestIntercepted.pointIndex2 + 1, newId)
                              }
                            }
                          )
                        )
                      }
                      currentSelectedId = newId
                    }
                  }
                  val nearestNew = findNearestNewPoint(mousePt, allSegments)
                  if (nearestNew != null) {
                    regCandidate((mousePt distance nearestNew.pt) + CLOSE_DISTANCE) {//todo отдаваит предпочтение существующим точкам
                      val newId = addPoint(nearestNew.pt)
                      savedElements = savedElements.toMutableList().apply {
                        val i1 = indexOf(nearestNew.curve1)
                        if (i1 == -1) {
                          println("i1 == -1")
                        }
                        set(
                          i1,
                          (get(i1) as Element.Curve).copy(
                            points = (get(i1) as Element.Curve).points.toMutableList().apply {
                              add(nearestNew.pointIndex + 1, newId)
                            }
                          )
                        )
                      }
                      currentSelectedId = newId
                    }
                  }
                  actions.minByOrNull { it.key }?.value?.invoke()
                }
              } else {// mouse up
                currentMousePoint = mousePt
                currentSelectedId = null
              }
            }
          }
        }
    ) {
      mapIdToPoint.values.forEach {
        drawCircle(Color.Red, 5f, center = it.offset)
      }
      bezierPoints.forEach {
        drawLine(Color.Black, start = it.originPt(mapIdToPoint).offset, end = it.refPt.offset, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f)))
        drawCircle(Color.Black, 3f, center = it.refPt.offset)
      }
      interceptedPoints.forEach {
        drawCircle(Color.Yellow, 5f, center = it.pt.offset)
      }
      findNearestNewPoint(currentMousePoint, allSegments)?.let {
        drawCircle(Color.Green, 3f, center = it.pt.offset)
      }
    }
  }

  Row() {
    TxtButton("Edit") {
      editPanelIsOpen = !editPanelIsOpen
    }
    TxtButton("Copy result to clipboard") {
      val result: String = generateCode(savedElements, mapIdToPoint)
      pasteToClipboard(result)
    }
  }

}

@OptIn(ExperimentalStdlibApi::class)
fun Map<Id, BezierRefEdit>.pt(mapIdToPoint: Map<Id, Pt>): Map<Pt, BezierRef> {
  val result = mutableMapOf<Pt, BezierRef>()
  entries.forEach {
    result.put(it.key.pt(mapIdToPoint), it.value.pt(mapIdToPoint))
  }
  return result
}

val Point.pt get() = Pt(x.toFloat(), y.toFloat())

@Composable
fun ColorPicker(currentColor: ULong, onChageColor: (ULong) -> Unit) {
  Column {
    TxtButton("Blue") {
      onChageColor(Color.Blue.value)
    }
    TxtButton("Red") {
      onChageColor(Color.Red.value)
    }
    TxtButton("Yellow") {
      onChageColor(Color.Yellow.value)
    }
  }
}

inline fun Id.pt(map: Map<Id, Pt>): Pt = map[this]!!
inline fun Collection<Id>.pts(map: Map<Id, Pt>): List<Pt> = map { it.pt(map) }
inline fun BezierRefEdit.pt(map: Map<Id, Pt>): BezierRef = BezierRef(startRef = startRef?.pt(map), endRef = endRef?.pt(map))

inline fun findNearestNewPoint(mouse: Pt, allSegments1: List<CurveSegment>): NearestPointOnCurve? {
  return allSegments1.mapIndexed { i, segment ->
    val nearestT = nearestBezierPoint(segment.bezierSegment, mouse)
    NearestPointOnCurve(
      pt = segment.bezierSegment.point(nearestT),
      curve1 = segment.curve,
      pointIndex = i
    )
  }.minByOrNull {
    mouse distance (it.pt)
  }
}

class NearestPointOnCurve(
  val pt: Pt,
  val curve1: Element.Curve,
  val pointIndex: Int,
)
class CurveSegment(val curve: Element.Curve, val pointIndex: Int, val bezierSegment: BezierSegment)
class BezierPt(val refPt: Pt, val id: Id?, val curve: Element.Curve, val key: Id, val isRefA: Boolean)
fun BezierPt.originPt(mapIdToPoint: Map<Id, Pt>): Pt = mapIdToPoint[key]!!
