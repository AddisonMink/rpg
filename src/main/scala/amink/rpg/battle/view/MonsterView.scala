package amink.rpg.battle.view

import amink.canvasui.*

import amink.rpg.battle.model.*

final case class MonsterView(
    monster: Creature,
    sprite: Sprite,
    selected: Boolean = false
):
  import MonsterView.*
  import ComponentUtils.AlignmentH
  import ComponentUtils.AlignmentV

  def component: Component =
    Style.column(List(marker, image))

  private def marker: Component =
    val content = selected match
      case false => Component.Empty
      case true  => Style.text("V")

    ComponentUtils.box(
      content,
      width,
      markerHeight,
      alignH = AlignmentH.Center,
      alignV = AlignmentV.Center
    )

  private def image: Component =
    val alignV = monster.row match
      case Row.Front => AlignmentV.Bottom
      case Row.Back  => AlignmentV.Top

    val realSprite = monster.row match
      case Row.Front =>
        sprite.copy(
          canvasFrameWidth = sprite.canvasFrameWidth * 2,
          canvasFrameHeight = sprite.canvasFrameHeight * 2
        )
      case Row.Back => sprite

    val frame = ComponentUtils.spriteFrame(realSprite, (0, 0))

    ComponentUtils.box(
      frame,
      width,
      spriteBoxHeight,
      alignH = AlignmentH.Center,
      alignV = AlignmentV.Center
    )

object MonsterView:
  val width: Int = 60
  val spriteBoxHeight: Int = 100
  val markerHeight: Int = 25

  val height: Int = spriteBoxHeight
    + markerHeight
    + Style.columnMargin

  def make(state: State, id: Id): MonsterView =
    val creature = state.creatureMap(id)
    val sprite = state.sprites(creature.species.name)
    MonsterView(creature, sprite)
