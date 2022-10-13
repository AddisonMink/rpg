package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*
import amink.canvasui.ComponentUtils.AlignmentH

final case class View(
    players: List[PlayerView]
):
  import View.*

  def component: Component =
    playerRow

  private def playerRow: Component =
    val row = Style.row(players.map(_.component))
    ComponentUtils.box(
      row,
      minWidth = playerRowWidth,
      minHeight = PlayerView.height,
      alignH = AlignmentH.Center
    )

object View:
  val playerRowWidth: Int = 4 * PlayerView.height + 3 * Style.rowMargin

  def make(state: State): View = View(
    players = state.creatureMap.players.map(PlayerView(_))
  )
