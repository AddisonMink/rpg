package amink.rpg.battle.view

import amink.canvasui.*

object Style:
  import ComponentUtils.AlignmentH
  import ComponentUtils.Padding
  import ComponentUtils.Border

  // Color
  val yellow = Color(r = 255, g = 255, b = 0)

  // Text Style
  enum FontStyle(val size: Int, val minWidth: Int, val alignment: AlignmentH) {
    case Normal extends FontStyle(16, 0, AlignmentH.Left)
    case Header(width: Int) extends FontStyle(20, width, AlignmentH.Center)
    case SmallHeader(width: Int) extends FontStyle(16, width, AlignmentH.Center)
  }

  def text(
      txt: String,
      style: FontStyle = FontStyle.Normal,
      highlighted: Boolean = false,
      color: Option[Color] = None
  ): Component =
    val c = color match
      case Some(c)             => c
      case None if highlighted => Color.black
      case None                => Color.white

    val t = ComponentUtils.text(
      txt,
      Font.Monospace(style.size),
      c,
      style.minWidth,
      style.alignment
    )
    if highlighted
    then ComponentUtils.box(t, color = Some(Color.white))
    else t

  // Box Style
  val padding: Padding = Padding(5, 5)
  val radius: Int = 10
  val border: Option[Border] = Some(Border(5, Color.white))

  def borderBox(
      component: Component,
      minWidth: Int = 0,
      minHeight: Int = 0,
      highlighted: Boolean = false
  ): Component =
    ComponentUtils.box(
      component,
      minWidth,
      minHeight,
      if highlighted then Some(Color.white) else None,
      border,
      padding,
      radius = radius
    )

  def boxSize(innerWidth: Int, innerHeight: Int): (Int, Int) =
    val c = ComponentUtils.box(
      Component.Empty,
      innerWidth,
      innerHeight,
      None,
      border,
      padding
    )
    (c.width, c.height)

  // Column Style
  val columnMargin: Int = 8

  def column(components: List[Component]): Component =
    ComponentUtils.column(columnMargin, components)

  // Row Style
  val rowMargin: Int = 8

  def row(components: List[Component]): Component =
    ComponentUtils.row(rowMargin, components)
