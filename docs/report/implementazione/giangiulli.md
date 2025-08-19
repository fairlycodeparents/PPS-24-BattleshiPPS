---
title: Chiara Giangiulli
nav_order: 1
parent: Implementazione
---

# Implementazione - Chiara Giangiulli
Il primo contributo ha riguardato la modellazione delle navi (file `Ship`).
L’obiettivo era quello di fornire un’astrazione che permettesse di:
- distinguere le diverse tipologie di navi (Frigate, Submarine, Destroyer, Carrier), attraverso una `enum`, ognuna con una lunghezza predefinita:
    ```scala
    enum ShipType(val length: Int):
      case Frigate   extends ShipType(2)
      case Submarine extends ShipType(3)
      case Destroyer extends ShipType(4)
      case Carrier   extends ShipType(5)
    ```
- gestire la posizione (o ancora, situata per ogni nave in alto a sinistra) e l'orientamento (`ShipOrientation`), 
orizzontale o verticale, della nave sul tabellone;
- supportare le operazioni di spostamento e rotazione attraverso i metodi `move(pos: Position): Ship`, `rotate: Ship`;
- calcolare dinamicamente le celle occupate, fornite dal metodo `positions: Set[Position]`.

Le navi sono create a partire dal tipo stesso, attraverso dei factory methods all’interno della `enum`.
Questo rende immediata e sicura la creazione di istanze di nave, evitando errori di configurazione:
```scala
def at(position: Position, orientation: Orientation = Orientation.Horizontal): Ship = 
  ShipImpl(this, position, orientation)
def at(x: Int, y: Int): Ship           = at(Position(x, y))
def verticalAt(pos: Position): Ship    = at(pos, Orientation.Vertical)
def verticalAt(x: Int, y: Int): Ship   = at(Position(x, y), Orientation.Vertical)
def horizontalAt(pos: Position): Ship  = at(pos, Orientation.Horizontal)
def horizontalAt(x: Int, y: Int): Ship = at(Position(x, y), Orientation.Horizontal)
```

