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

    val col = selected match
      case true =>
        val marker = Style.text("V", Style.FontStyle.SmallHeader(width))
        Style.column(List(marker, frame))
      case false => frame

    ComponentUtils.box(
      frame,
      width,
      height,
      alignH = AlignmentH.Center,
      alignV = AlignmentV.Center
    )

object MonsterView:
  val width: Int = 60
  val height: Int = 100

  def make(state: State, id: Id): MonsterView =
    val creature = state.creatureMap(id)
    val sprite = state.sprites(creature.species.name)
    MonsterView(creature, sprite)
