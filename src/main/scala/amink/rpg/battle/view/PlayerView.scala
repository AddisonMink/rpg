package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*

final case class PlayerView(
    creature: Creature
):
  import PlayerView.*
  import ComponentUtils.AlignmentV.*
  import Style.FontStyle

  def component: Component =
    val alignment = creature.row match
      case Row.Front => Top
      case Row.Back  => Bottom

    ComponentUtils.box(statBlock, width, height, alignV = alignment)

  private def statBlock: Component =
    val name = Style.text(
      creature.name,
      FontStyle.Header(blockInnerWidth),
      highlighted = false
    )

    val hp = Style.text(s"HP: ${creature.hp}", highlighted = false)
    val col = Style.column(List(name, hp))
    Style.borderBox(col, blockInnerWidth, blockInnerHeight, false)

object PlayerView:
  val blockInnerWidth: Int = 100
  val blockInnerHeight: Int = 100
  val rowOffset: Int = 50

  val (width, blockHeight): (Int, Int) =
    Style.boxSize(blockInnerWidth, blockInnerHeight)

  val height: Int = blockHeight + rowOffset
