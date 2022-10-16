package amink.rpg.battle

import amink.canvasui.Renderer

import amink.rpg.engine.*
import model.*
import view.*
import amink.rpg.battle.control.MessageExecutor
import amink.canvasui.Color

class BattleEngine(init: State, r: Renderer):

  private val engine = Engine[State, Message](
    decodeKey,
    MessageExecutor.execute,
    renderIO(r),
    init
  )

  def startIO(): Unit = engine.startIO(Message.Start)
  def stopIO(): Unit = engine.stopIO()

  private def decodeKey(key: String): Option[Message] = key.toLowerCase match
    case "w"     => Some(Message.Up)
    case "a"     => Some(Message.Left)
    case "s"     => Some(Message.Down)
    case "d"     => Some(Message.Right)
    case "enter" => Some(Message.Confirm)
    case "'" => Some(Message.Cancel)
    case _       => None

  private def renderIO(r: Renderer)(state: State): Unit =
    r.clearIO(Color.black)
    View.make(state).component.renderIO(0, 0)(r)
