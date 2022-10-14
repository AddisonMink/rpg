package amink.rpg.engine

import org.scalajs.dom.*

import amink.canvasui.Sprite

enum Command[+Message]:
  case Noop
  case Render
  case Send(msg: Message)
  case DelayedSend(msg: Message, millis: Int)
  case LoadSpriteAndSend(sprite: Sprite, src: String, msg: Message)