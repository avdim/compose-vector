package lib.vector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
//import androidx.compose.ui.awt.awtEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.intellij.getClipboardImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import lib.vector.intercept.interceptCubicBezier
import lib.vector.intercept.nearestBezierPoint
import lib.vector.utils.indexOfFirstOrNull
import lib.vector.utils.toByteArray
import java.awt.Point
import java.awt.event.InputEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import kotlin.random.Random
import kotlin.random.nextUInt

const val CURVE_PRECISION = 25f * 100f
const val CLOSE_DISTANCE = 5.0
const val SHOW_INTERCEPTIONS = false
const val MOVE_NEAREST_BEZIER_REF = true
const val MOVE_NEAREST_NEW_POINT = false

sealed class DrawOptions {
  data class Dot(val namePrefix: String = "m") : DrawOptions()
  data class Curve(val color: ULong = Color.Blue.value, val fillPath:Boolean = false) : DrawOptions()
  data class Rect(val color: ULong = Color.Yellow.value) : DrawOptions()
  data class Img(val image: ImageBitmap? = null) : DrawOptions()
}

data class ControllerState(val name: String, val options: DrawOptions)

@OptIn(ExperimentalStdlibApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EditMode(modifier: Modifier, lambda: GeneratedScope.() -> Unit) {
  val initState = initializeByGeneratedScope(lambda)
  // State
  var mapIdToPoint: Map<Id, Pt> by remember { mutableStateOf(initState.mapIdToPoint) }
  var savedElements: List<Element> by remember { mutableStateOf(initState.savedElements) }

  var controllers by remember {
    mutableStateOf(
      listOf(
        ControllerState("Dot", DrawOptions.Dot()),
        ControllerState("Curve", DrawOptions.Curve()),
        ControllerState("Rectangle", DrawOptions.Rect()),
        ControllerState("Image", DrawOptions.Img()),
      )
    )
  }
  var selectedControllerIndex by remember { mutableStateOf(controllers.indexOfFirstOrNull { it.options is DrawOptions.Curve } ?: 0) }
  val controllerState: ControllerState by derivedStateOf { controllers[selectedControllerIndex] }
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
              Row {
                Checkbox(options.fillPath, onCheckedChange = { changedChecked->
                  replaceChangeOptions { options.copy(fillPath = changedChecked) }
                })
                Text("fill")
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
            is DrawOptions.Dot -> {
              Row {
                Text("list prefix:")
              }
              TextField(options.namePrefix, onValueChange = {
                replaceChangeOptions{ options.copy(namePrefix = it) }
              })
            }
          }
        }
      }
    }
  }

  var currentPoints by remember { mutableStateOf<List<Id>>(emptyList()) }
  val currentElement: Element? by derivedStateOf {
    val cs = controllerState
    when (cs.options) {
      is DrawOptions.Dot -> null
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
          color = cs.options.color,
          points = currentPoints.takeOrSmaller(1) +
            currentPoints.filterIndexed { i, _ -> temp.any { it.current == i } } +
            currentPoints.takeLastOrSmaller(1),
          bezierRef = emptyMap(),
          fillPath = cs.options.fillPath
        )
      }
      is DrawOptions.Rect -> {
        if (currentPoints.size >= 2) {
          Element.Rectangle(cs.options.color, currentPoints.first(), currentPoints.last())
        } else {
          null
        }
      }
      is DrawOptions.Img -> {
        val img = cs.options.image
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
        val event = awaitPointerEventScope {
          awaitPointerEvent()
        }
        val point = event.mouseEvent?.point ?: continue
        val nativeEvent = (event.mouseEvent as MouseEvent)
        val isAnyPressed = nativeEvent.modifiersEx and AnyButtonMask != 0
//        println(nativeEvent.modifiersEx)
        if (isAnyPressed /*|| event.buttons.areAnyPressed*/) {
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
          drawCurve(e.color, e.points.pts(mapIdToPoint), e.bezierRef.pt(mapIdToPoint), e.fillPath)
        }
        is Element.Rectangle -> drawRect(e.color, e.start.pt(mapIdToPoint), e.end.pt(mapIdToPoint))
        is Element.Bitmap -> drawBitmap(e.topLeft.pt(mapIdToPoint), e.byteArray)
      }
    }
  }

  val options = controllerState.options
  if (options is DrawOptions.Dot) {
    //Selection
    var currentSelectedId: Id? by remember { mutableStateOf(null) }
    var previousSelectedId: Id? by remember { mutableStateOf(null) }
    var currentMousePoint: Pt by remember { mutableStateOf(Pt(0, 0)) }

    LaunchedEffect(Unit) {
      globalKeyListener.collect {
        fun moveNamedPoint(dx:Int, dy:Int) {
          mapIdToPoint = mapIdToPoint.mapValues {
            if(it.key.name != null) {
              Pt(it.value.x + dx, it.value.y + dy)
            } else {
              it.value
            }
          }
        }
        when(it) {
          Key.Delete, Key.Backspace, Key.D -> {
            val deleteId = previousSelectedId
            if(deleteId != null) {
              println("delete deleteId: $deleteId")
              savedElements = savedElements.toMutableList().apply {
                for(i in this.indices.reversed()) {
                  val element = get(i)
                  when (element) {
                    is Element.Curve -> {
                      this[i] = element.copy(
                        points = element.points.filter { it != deleteId },
                        bezierRef = element.bezierRef.mapValues {
                          it.value.copy(
                            startRef = if (it.value.startRef == deleteId) null else it.value.startRef,
                            endRef = if (it.value.endRef == deleteId) null else it.value.endRef
                          )
                        }.toMutableMap().apply {
                          remove(deleteId)
                        }
                      )
                    }
                  }
                }
              }
              mapIdToPoint = mapIdToPoint.toMutableMap().apply {
                remove(deleteId)
              }
              previousSelectedId = null
            }
          }
          Key.DirectionLeft -> {
            moveNamedPoint(-1, 0)
          }
          Key.DirectionRight -> {
            moveNamedPoint(1, 0)
          }
          Key.DirectionDown -> {
            moveNamedPoint(0, 1)
          }
          Key.DirectionUp -> {
            moveNamedPoint(0, -1)
          }
        }
      }
    }

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
      if (SHOW_INTERCEPTIONS) {
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
      } else {
          emptyList()
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
              if (event.buttons.isPrimaryPressed && event.keyboardModifiers.isShiftPressed) {
                val nearestPoint = mapIdToPoint.minByOrNull { point.pt distance it.value }
                if(nearestPoint != null) {
                  mapIdToPoint = mapIdToPoint.toMutableMap().apply {
                    val oldKey = nearestPoint.key
                    val newKey = oldKey.copy(name = options.namePrefix + Random.nextUInt())
                    remove(oldKey)
                    set(newKey, nearestPoint.value)
                    savedElements = savedElements.map { e->
                      when (e) {
                        is Element.Curve -> {
                          e.copy(
                            points = e.points.map {
                              if (it == oldKey) newKey else it
                            },
                            bezierRef = e.bezierRef.toMutableMap().apply {
                              val moved = remove(oldKey)
                              if (moved != null) {
                                put(newKey, moved)
                              }
                            }.mapValues {
                              it.value.copy(
                                startRef = it.value.startRef.let { if (it == oldKey) newKey else it },
                                endRef = it.value.endRef.let { if (it == oldKey) newKey else it }
                              )
                            }
                          )
                        }
                        else -> TODO("change key in Bitmap and Rectangle")
                      }
                    }
                  }
                }
              } else if (event.buttons.isPrimaryPressed) {
                if (currentSelectedId != null) {
                  mapIdToPoint = mapIdToPoint.toMutableMap().apply {
                    set(currentSelectedId!!, mousePt)
                  }
                } else {
                  val actions: MutableMap<Double, () -> Unit> = mutableMapOf()
                  fun regCandidate(distance: Double, action: () -> Unit) {
                    actions[distance] = action
                  }

                  val nearestPoint = mapIdToPoint.minByOrNull { point.pt distance it.value }
                  if (nearestPoint != null) {
                    regCandidate(mousePt distance nearestPoint.value) {
                      currentSelectedId = nearestPoint.key
                    }
                  }

                  val nearestBezierRef: BezierPt? = bezierPoints.minByOrNull { bezierPt ->
                    point.pt distance (bezierPt.id?.let { mapIdToPoint[it] } ?: bezierPt.refPt)
                  }
                  if (MOVE_NEAREST_BEZIER_REF && nearestBezierRef != null) {
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
                  if (MOVE_NEAREST_NEW_POINT && nearestNew != null) {
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
                if (currentSelectedId != null) {
                  previousSelectedId = currentSelectedId
                  currentSelectedId = null
                }
              }
            }
          }
        }
    ) {
      mapIdToPoint.forEach {
        if(it.key.name != null) {
          drawCircle(Color.Green, 4f, center = it.value.offset)
          drawCircle(Color.Red, 2f, center = it.value.offset)
        } else {
          drawCircle(Color.Red, 5f, center = it.value.offset)
        }
      }
      if(MOVE_NEAREST_BEZIER_REF) {
        bezierPoints.forEach {
          drawLine(
            Color.Gray,
            start = it.originPt(mapIdToPoint).offset,
            end = it.refPt.offset,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f))
          )
          drawCircle(Color.Yellow, 3f, center = it.refPt.offset)
        }
      }
      interceptedPoints.forEach {
        drawCircle(Color.Yellow, 5f, center = it.pt.offset)
        drawCircle(Color.Red, 3f, center = it.pt.offset)
      }
      if(MOVE_NEAREST_NEW_POINT) {
        findNearestNewPoint(currentMousePoint, allSegments)?.let {
          drawCircle(Color.Green, 3f, center = it.pt.offset)
        }
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

val isMacOS get() = System.getProperty("os.name")?.contains("Mac OS") ?: false
val Point.pt get() =
  if(isMacOS) {
    Pt(x.toFloat()*2, y.toFloat()*2) //todo наверное из за высокого разрешения
  } else {
    Pt(x.toFloat(), y.toFloat())
  }

inline fun Id.pt(map: Map<Id, Pt>): Pt = map[this]!!
inline fun Collection<Id>.pts(map: Map<Id, Pt>): List<Pt> = map { it.pt(map) }
inline fun BezierRefEdit.pt(map: Map<Id, Pt>): BezierRef = BR(startRef = startRef?.pt(map), endRef = endRef?.pt(map))

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
val globalKeyListener:MutableSharedFlow<Key> = MutableSharedFlow()

const val AnyButtonMask =
  InputEvent.BUTTON1_DOWN_MASK or InputEvent.BUTTON2_DOWN_MASK or InputEvent.BUTTON3_DOWN_MASK

//val PointerButtons.areAnyPressed2: Boolean
//  get() = (packedValue and AnyButtonMask) != 0
