package amink.rpg

import org.scalajs.dom.*
import org.scalajs.dom.HTMLCanvasElement

import amink.canvasui.*

@main def hello: Unit =
  val canvas = document
    .querySelector("canvas")
    .asInstanceOf[HTMLCanvasElement]
  canvas.width = 500
  canvas.height = 500

  val renderer = Renderer(canvas)

  val init = {
    import battle.model.*
    import util.*
    val sprite = Sprite("goblin", null, 32, 32, 16, 16, 32, 32)
    val src = "images/goblin.png"
    val weapon = Species.goblinBow
    val player1 = Creature.make(0, "1", Species.Fighter(weapon), Row.Front)
    val player2 = Creature.make(1, "2", Species.Fighter(weapon), Row.Back)
    val cMap = Map(0 -> player1, 1 -> player2)
    val sprites = Map("goblin" -> sprite)
    val sources = Map("goblin" -> src)
    State.LoadingSprites(Seed.Cycle(Nil), cMap, sprites, sources)
  }

  val engine = {
    import battle.*
    BattleEngine(init, renderer)
  }

  engine.startIO()
