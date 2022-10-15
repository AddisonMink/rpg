package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*

final case class LogView(
    logs: List[String],
    selected: Option[Int]
):
  import LogView.*

  def component: Component =
    val entries = logs.zipWithIndex
      .map {
        case (l, i) if selected.contains(i) => selectedEntry(l)
        case (l, i)                         => entry(l)
      }

    val col = Style.column(entries)
    Style.borderBox(col, innerWidth, innerHeight)

  private def entry(log: String): Component =
    Style.text("- " + log)

  private def selectedEntry(log: String): Component =
    Style.text(log, highlighted = true)

object LogView:
  val width: Int = View.width
    - Style.columnMargin
    - QueueView.width

  val height: Int = 100

  val (innerWidth, innerHeight): (Int, Int) =
    Style.innerBoxSize(width, height)
