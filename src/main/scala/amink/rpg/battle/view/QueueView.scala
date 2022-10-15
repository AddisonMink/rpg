package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*

final case class QueueView(
    queue: List[Creature]
):
  import QueueView.*

  def component: Component =
    val entries = queue.map(entry)
    Style.column(entries)

  private def entry(creature: Creature): Component =
    val name = Style.text(creature.name)
    Style.borderBox(name, width, entryHeight)

object QueueView:
  val entryHeight: Int = 16
  val width: Int = 100
  val height: Int = 10 * entryHeight + 9 * Style.columnMargin
