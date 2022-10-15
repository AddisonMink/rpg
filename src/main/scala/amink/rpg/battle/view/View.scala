package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*
import amink.canvasui.ComponentUtils.AlignmentH

final case class View(
    monsters: List[MonsterView],
    players: List[PlayerView],
    queue: QueueView
):
  import View.*

  def component: Component =
    val col = Style.column(List(monsterRow, playerRow))
    Style.row(List(col, queue.component))

  private def monsterRow: Component =
    val row = Style.row(monsters.map(_.component))
    ComponentUtils.box(
      row,
      minWidth = playerRowWidth,
      minHeight = MonsterView.height,
      alignH = AlignmentH.Center
    )

  private def playerRow: Component =
    val row = Style.row(players.map(_.component))
    ComponentUtils.box(
      row,
      minWidth = playerRowWidth,
      minHeight = PlayerView.height,
      alignH = AlignmentH.Center
    )

object View:
  val playerRowWidth: Int = 3 * PlayerView.width + 3 * Style.rowMargin
  val width: Int = playerRowWidth + QueueView.width + Style.rowMargin
  val height: Int = PlayerView.height + MonsterView.height + Style.columnMargin

  def make(state: State): View =
    val monsters = state.creatureMap.monsters
      .map(m => MonsterView.make(state, m.id))

    val players = state.creatureMap.players
      .map(PlayerView(_))

    val queue = QueueView(state.creatureMap.queue)

    View(monsters, players, queue)
