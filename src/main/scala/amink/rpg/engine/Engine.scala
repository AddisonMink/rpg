package amink.rpg.engine

import org.scalajs.dom.*
import scalajs.js.timers.*
import amink.canvasui.Sprite
import org.scalajs.dom.HTMLImageElement

class Engine[State, Message](
    decodeKey: String => Option[Message],
    executeMessage: (State, Message) => (State, Command[Message]),
    renderIO: State => Unit,
    private var state: State
):
  import Command.*

  private def startIO(): Unit =
    document.onkeypress = key =>
      val msgOpt = decodeKey(key.key)
      msgOpt.foreach(sendMessage)

  def stopIO(): Unit =
    document.onkeypress = _ => ()

  private def executeCommandIO(command: Command[Message]): Unit = command match
    case Noop                     =>
    case Render                   => renderIO(state)
    case Send(msg)                => sendMessage(msg)
    case DelayedSend(msg, millis) => setTimeout(millis)(sendMessage(msg))
    case LoadSpriteAndSend(sprite, src, msg) =>
      loadSpriteAndSendMessage(sprite, src, msg)

  private def sendMessage(msg: Message): Unit =
    val (newState, cmd) = executeMessage(state, msg)
    state = newState
    executeCommandIO(cmd)

  private def loadSpriteAndSendMessage(
      sprite: Sprite,
      src: String,
      msg: Message
  ): Unit =
    val img = document
      .createElement("img")
      .asInstanceOf[HTMLImageElement]

    img.onload = _ => sendMessage(msg)
    img.src = src
