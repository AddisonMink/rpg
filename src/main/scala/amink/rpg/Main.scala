package amink.rpg

import org.scalajs.dom.*

import amink.canvasui.*
import amink.rpg.battle.view.View
import org.scalajs.dom.HTMLImageElement

@main def hello: Unit =
  val canvas = document
    .querySelector("canvas")
    .asInstanceOf[HTMLCanvasElement]
  canvas.width = View.width
  canvas.height = View.height

  val goblinImage = document
    .createElement("img")
    .asInstanceOf[HTMLImageElement]

  val renderer = Renderer(canvas)

  val init = {
    import battle.model.*
    import util.*
    val sprite = Sprite("goblin", goblinImage, 32, 32, 16, 16, 32, 32)
    val src = "images/goblin.png"
    val weapon = Weapon("sword", Range.Close, 1, 0, 1)
    val player1 = Creature.make(0, "1", Species.Fighter(weapon), Row.Front)
    val player2 = Creature.make(1, "2", Species.Fighter(weapon), Row.Back)
    val monster1 = Creature.make(2, "1", Species.Goblin, Row.Front)
    val monster2 = Creature.make(3, "2", Species.Goblin, Row.Back)
    val cMap = Map(0 -> player1, 1 -> player2, 2 -> monster1, 3 -> monster2)
    val sprites = Map("goblin" -> sprite)
    val sources = Map("goblin" -> src)
    val seed = Seed.Rand(scala.util.Random().nextInt)
    State.LoadingSprites(seed, cMap, sprites, sources)
  }

  val engine = {
    import battle.*
    BattleEngine(init, renderer)
  }

  engine.startIO()
