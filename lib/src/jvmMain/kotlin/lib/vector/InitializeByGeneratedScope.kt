package lib.vector

data class EditState(
  val mapIdToPoint: Map<Id, Pt>,
  val savedElements: List<Element>
)

fun initializeByGeneratedScope(lambda: GeneratedScope.() -> Unit):EditState {
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
  return EditState(
    generatedMapIdToPoint, generatedElements
  )
}
