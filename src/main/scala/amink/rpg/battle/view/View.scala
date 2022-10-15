package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*
import amink.canvasui.ComponentUtils.AlignmentH

final case class View(
    log: LogView,
    monsters: List[MonsterView],
    players: List[PlayerView],
    queue: QueueView
):
  import View.*

  def component: Component =
    val col = Style.column(List(log.component, monsterRow, playerRow))
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

  val width: Int = playerRowWidth
    + QueueView.width
    + Style.rowMargin

  val height: Int = PlayerView.height
    + MonsterView.height
    + LogView.height
    + 2 * Style.columnMargin

  def make(state: State): View =
    val log = logView(state)

    val monsters = state.creatureMap.monsters
      .map(m => MonsterView.make(state, m.id))

    val players = state.creatureMap.players
      .map(PlayerView(_))

    val queue = QueueView(state.creatureMap.queue)

    View(log, monsters, players, queue)

  private def logView(state: State): LogView =
    val logs = state match
      case State.Logging(_, _, _, logs) => logs
      case _                            => Nil
    LogView(logs)
