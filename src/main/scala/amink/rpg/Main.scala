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

  val model = {
    import battle.model.*
    Creature.make(0, "1", Species.Goblin, Row.Back)
  }

  val view = {
    import battle.view.*
    PlayerView(model)
  }

  val component = view.component
  canvas.width = component.width
  canvas.height = component.height
  component.renderIO(0, 0)(renderer)

def msg = "I was compiled by Scala 3. :)"
