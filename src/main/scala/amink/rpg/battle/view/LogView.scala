package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*

final case class LogView(
    logs: List[Log]
):
  import LogView.*

  def component: Component =
    val entries = logs.map(entry)
    val col = Style.column(entries)
    Style.borderBox(col, innerWidth, innerHeight)

  private def entry(log: Log): Component =
    Style.text("- " + log.message)

object LogView:
  val width: Int = View.width
    - Style.columnMargin
    - QueueView.width

  val height: Int = 100

  val (innerWidth, innerHeight): (Int, Int) =
    Style.innerBoxSize(width, height)
