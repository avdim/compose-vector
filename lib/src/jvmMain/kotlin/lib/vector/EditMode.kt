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
import lib.vector.utils.indexOfFirstOrNull
import lib.vector.utils.toByteArray
import java.awt.Point
import java.awt.image.BufferedImage

const val CURVE_PRECISION = 25f

sealed class DrawOptions {
  class Selection : DrawOptions()
  class BezierReference : DrawOptions()
  class InterceptedPoints : DrawOptions()
  data class Curve(val color: ULong = Color.Blue.value) : DrawOptions()
  data class Rect(val color: ULong = Color.Yellow.value) : DrawOptions()
  data class Img(val image: ImageBitmap? = null) : DrawOptions()
}

data class ControllerState(val name: String, val options: DrawOptions)

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun EditMode(modifier: Modifier, lambda: GeneratedScope.() -> Unit ) {
  // Init
  val generatedMapIdToPoint: MutableMap<Id, Pt> = mutableMapOf()
  val generatedElements: MutableList<Element> = mutableListOf()
  fun mapPtToId(pt: Pt): Id =
    if (pt is PtEdit) {
      pt.id
    } else {
      val id = getNextPointId()
      generatedMapIdToPoint[id] = pt
      id
    }

  val generatedScope = object : GeneratedScope {
    override fun mkPt(x: Float, y: Float): MakePt = MakePt { _, property ->
      val id = getNextPointId(property.name)
      val pt = PtEdit(x, y, id)
      generatedMapIdToPoint[id] = pt
      pt
    }

    override fun drawCurve(color: ULong, points: List<Pt>, bezieRef: Map<Pt, BezierRef>) {
      generatedElements.add(
        Element.Curve(
          color = color,
          points = points.map(::mapPtToId),
          emptyMap()//todo
        )
      )
    }

    override fun drawRect(color: ULong, start: Pt, end: Pt) {
      generatedElements.add(Element.Rectangle(color, mapPtToId(start), mapPtToId(end)))
    }

    override fun drawBitmap(pt: Pt, byteArray: ByteArray) {
      generatedElements.add(Element.Bitmap(mapPtToId(pt), byteArray))
    }
  }
  generatedScope.lambda()

  // State
  var mapIdToPoint: Map<Id, Pt> by remember { mutableStateOf(generatedMapIdToPoint) }
  var savedElements by remember { mutableStateOf<List<Element>>(generatedElements) }
  var controllers by remember {
    mutableStateOf(
      listOf(
        ControllerState("Selection", DrawOptions.Selection()),
        ControllerState("BezierReference", DrawOptions.BezierReference()),
        ControllerState("InterceptedPoints", DrawOptions.InterceptedPoints()),
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

  var currentPoints by remember { mutableStateOf<List<Id>?>(null) }
  val currentElement: Element? by derivedStateOf {
    when (controllerState.options) {
      is DrawOptions.Selection -> null
      is DrawOptions.BezierReference -> null
      is DrawOptions.InterceptedPoints -> null
      is DrawOptions.Curve -> {
        val points: List<Id> = currentPoints.orEmpty()
        val fullLength = points.windowed(2).sumOf { (a: Id, b: Id) -> a.pt(mapIdToPoint) distance b.pt(mapIdToPoint) }
        val threshold = fullLength / CURVE_PRECISION
        class PointWithNeighbours(var prev: Int, val current: Int, var next: Int) {
          val currentPoint = points[current].pt(mapIdToPoint)
          val prevDistance get() = points[prev].pt(mapIdToPoint) distance currentPoint
          val nextDistance get() = points[next].pt(mapIdToPoint) distance currentPoint
          val sumDistance get() = prevDistance + nextDistance
        }
        val temp = points.indices.windowed(3).map { (prev, current, next) ->
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
          points = points.takeOrSmaller(1) + points.filterIndexed { i, _ -> temp.any { it.current == i } } + points.takeLastOrSmaller(1),
          bezierRef = emptyMap()
        )
      }
      is DrawOptions.Rect -> {
        val points = currentPoints
        if (points != null && points.size >= 2) {
          Element.Rectangle(controllerState.options.color, points.first(), points.last())
        } else {
          null
        }
      }
      is DrawOptions.Img -> {
        val img = controllerState.options.image
        val points = currentPoints
        if (img != null && points != null && points.isNotEmpty()) {
          Element.Bitmap(
            points.last(),
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
    val e = currentElement
    if (e != null) {
      savedElements = savedElements + e
    }
  }

  DisplayMode(
    modifier = modifier.pointerInput(Unit) {
      while (true) {
        val event = awaitPointerEventScope { awaitPointerEvent() }
        val point = event.awtEvent.point
        if (event.buttons.areAnyPressed || event.changes.lastOrNull()?.type == PointerType.Touch) {
          val previousPoints: List<Id>? = currentPoints
          if (previousPoints == null) {
            currentPoints = listOf(addPoint(point.pt))
            pointerStart(point.pt)
          } else {
            currentPoints = previousPoints + addPoint(point.pt)
            pointerMove(point.pt)
          }
        } else {
          if (currentPoints != null && event.keyboardModifiers.isShiftPressed.not()) {
            pointerEnd(point.pt)
            currentPoints = null

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

  when (controllerState.options) {
    is DrawOptions.Selection -> {
      Canvas(
        modifier.wrapContentSize(Alignment.Center)
          .fillMaxSize()
          .pointerInput(Unit) {
            while (true) {
              val event = awaitPointerEventScope { awaitPointerEvent() }
              val point = event.mouseEvent?.point
              if (event.buttons.isPrimaryPressed && point != null) {
                val candidate = mapIdToPoint.minByOrNull { point.pt distance it.value }
                if (candidate != null) {
                  mapIdToPoint = mapIdToPoint.toMutableMap().apply {
                    set(candidate.key, point.pt)
                  }
                }
              }
            }
          }
      ) {
        mapIdToPoint.values.forEach {
          drawCircle(Color.Red, 5f, center = it.offset)
        }
      }
    }
    is DrawOptions.BezierReference -> {
      class BezierPt(override val x: Float, override val y: Float, val id: Id?, val curve: Element.Curve, val key: Id, val isRefA: Boolean) : Pt

      val bezierPoints: List<BezierPt> by derivedStateOf {
        buildList {
          savedElements.filterIsInstance<Element.Curve>().forEach { curve ->
            val points = curve.points
            points.toLineSegments().forEach { s ->
              val idStart = curve.bezierRef[s.start]?.startRef
              val idEnd = curve.bezierRef[s.end]?.endRef
              val result = s.map { it.pt(mapIdToPoint) }.bezierSegment(startRef = idStart?.pt(mapIdToPoint), idEnd?.pt(mapIdToPoint))
              add(
                BezierPt(result.refStart.x, result.refStart.y, idStart, curve, s.start, true)
              )
              add(
                BezierPt(result.refEnd.x, result.refEnd.y, idEnd, curve, s.end, false)
              )
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
              if (event.buttons.isPrimaryPressed && point != null) {

                val candidate = bezierPoints.minByOrNull { bezierPt ->
                  point.pt distance (bezierPt.id?.let { mapIdToPoint[it] } ?: bezierPt)
                }
                if (candidate != null) {
                  // create new pt
                  val id = candidate.id
                  if (id == null) {
                    val newId = addPoint(point.pt)
                    savedElements = savedElements.toMutableList().apply {
                      val index = indexOf(candidate.curve)
                      if (index >= 0) {
                        set(
                          index,
                          candidate.curve.copy(
                            bezierRef = candidate.curve.bezierRef.toMutableMap().apply {
                              val previous = (get(candidate.key) ?: BezierRefEdit(null, null))
                              if (candidate.isRefA) {
                                set(candidate.key, previous.copy(startRef = newId))
                              } else {
                                set(candidate.key, previous.copy(endRef = newId))
                              }
                            }
                          )
                        )
                      }
                    }
                  } else {
                    mapIdToPoint = mapIdToPoint.toMutableMap().apply {
                      set(id, point.pt)
                    }
                  }

                }
              }
            }
          }
      ) {
        bezierPoints.forEach {
          drawCircle(Color.Red, 5f, center = it.offset)
        }
      }
    }
    is DrawOptions.InterceptedPoints -> {
      class InterceptedPoint(val pt: Pt, val curve1: Element.Curve, val t1: Float, val pointIndex1: Int, val curve2: Element.Curve, val t2: Float, val pointIndex2: Int)
      val interceptedPoints:List<InterceptedPoint> = remember(savedElements) {
        val elements = savedElements
        buildList<InterceptedPoint> {
          class CurveSegment(val curve: Element.Curve, val pointIndex: Int, val bezierSegment: BezierSegment)
          val allSegments = elements.filterIsInstance<Element.Curve>().flatMap { curve ->
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

          for (i in 0 until allSegments.size) {
            for (j in i + 1 until allSegments.size) {
              //todo N^2
              val a = allSegments[i]
              val b = allSegments[j]
              val interceptedPoints = interceptCubicBezier(a.bezierSegment, b.bezierSegment)
              interceptedPoints.relativePointsA.zip(interceptedPoints.relativePointsB).forEach { (ta, tb) ->
                a.bezierSegment.point(ta)
                b.bezierSegment.point(tb)
                add(InterceptedPoint(a.bezierSegment.point(ta), a.curve, ta, a.pointIndex, b.curve, tb, b.pointIndex))
              }
            }
          }

        }
      }
      Canvas(
        modifier.wrapContentSize(Alignment.Center)
          .fillMaxSize()
          .pointerInput(savedElements) {
            while (true) {
              val event = awaitPointerEventScope { awaitPointerEvent() }
              if (event.type == PointerEventType.Press) {
                val point = event.mouseEvent?.point
                if (point != null) {
                  val candidate = interceptedPoints.minByOrNull {
                    point.pt distance (it.pt)
                  }
                  if (candidate != null) {
                    val newId = addPoint(candidate.pt)
                    savedElements = savedElements.toMutableList().apply {
                      val i1 = indexOf(candidate.curve1)
                      val i2 = indexOf(candidate.curve2)
                      if(i1 == -1 || i2 == -1 ) {
                        println("i1 == -1 || i2 == -1")
                      }
                      set(
                        i1,
                        (get(i1) as Element.Curve).copy(
                          points = (get(i1) as Element.Curve).points.toMutableList().apply {
                            add(candidate.pointIndex1 + 1, newId)
                          }
                        )
                      )
                      set(
                        i2,
                        (get(i2) as Element.Curve).copy(
                          points = (get(i2) as Element.Curve).points.toMutableList().apply {
                            if (candidate.curve1 == candidate.curve2 && candidate.pointIndex2 > candidate.pointIndex1) {
                              add(candidate.pointIndex2 + 2, newId)
                            } else {
                              add(candidate.pointIndex2 + 1, newId)
                            }
                          }
                        )
                      )
                    }
                  }

                }
              }
            }
          }
      ) {
        interceptedPoints.forEach {
          drawCircle(Color.Red, 5f, center = it.pt.offset)
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
