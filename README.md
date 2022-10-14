# JRPG Thingie
I played the first few Final Fantasy games recently and decided to make a tiny RPG of my own.

## Tech Stuff for Interested Parties
This is also kind of an FP demo that I can use for code samples. I'm using:
- Scala 3 with ScalaJS
- Cats Core and Cats MTL for monad stacks
- Monocle for optics
- CanvasUI, a library I made for creating UI's on the HTML5 canvas

## Dev Log
### 10/13/2022
Right now I'm focused on getting the battle system down. I'm using an Elm-like architecture to handle state management and user inputs, which is working out really well so far. ADT's are a very easy and pleasant way to manage state and I don't miss mutability at all thinks to Monocle.

I don't know how well this approach would work for a real-time game, but it works great for a menu-driven rpg.

