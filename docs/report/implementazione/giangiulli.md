---
title: Chiara Giangiulli
nav_order: 1
parent: Implementazione
---

# Implementazione - Chiara Giangiulli

### Navi
Il primo contributo ha riguardato la modellazione delle navi (file `Ship`).
L’obiettivo era quello di fornire un’astrazione che permettesse di:
- distinguere le diverse tipologie di navi (Frigate, Submarine, Destroyer, Carrier), attraverso una `enum`, ognuna con una lunghezza predefinita, immediatamente disponibile.
  In questo modo le tipologie di navi sono finite e non estendibili dall’esterno, garantendo sicurezza e coerenza interna:
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

Le navi vengono istanziate direttamente a partire dal tipo, attraverso dei factory methods definiti all’interno della `enum`.
Questo rende immediata e sicura la creazione di istanze di nave, evitando errori di configurazione e facilitando la leggibilità del codice:
```scala
def at(position: Position, orientation: Orientation = Orientation.Horizontal): Ship = 
  ShipImpl(this, position, orientation)
def at(x: Int, y: Int): Ship           = at(Position(x, y))
def verticalAt(pos: Position): Ship    = at(pos, Orientation.Vertical)
def verticalAt(x: Int, y: Int): Ship   = at(Position(x, y), Orientation.Vertical)
def horizontalAt(pos: Position): Ship  = at(pos, Orientation.Horizontal)
def horizontalAt(x: Int, y: Int): Ship = at(Position(x, y), Orientation.Horizontal)
```

### Attacco
Un secondo importante contributo ha riguardato l'implementazione della logica di attacco e di fine partita
(file `ShipAttack`).

I possibili esiti di un attacco sono stati formalizzati attraverso una `enum`:
```scala
enum AttackResult:
  case Miss
  case Hit(ship: Ship)
  case Sunk(ship: Ship)
  case EndOfGame(ship: Ship)
  case AlreadyAttacked
```
`AttackResult` definisce un ADT chiuso che rappresenta in modo esaustivo e sicuro tutti i possibili esiti di un colpo.
In questo modo il codice client deve sempre gestire uno di questi casi, senza possibilità di stati imprevisti.

L'entry point dell'attacco è il metodo `attack`:
```scala
def attack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
  validateAttack(board, position)
    .map(_ => processValidAttack(board, position))
    .getOrElse(handleInvalidAttack(board, position))
```
Si occupa di:
- validare la posizione scelta (`validateAttack`), cioè controllare che sia nel tabellone e non sia già stata attaccata;
- gestire la situazione, in caso di errore, con un risultato appropriato (`handleInvalidAttack`);
- aggiornare lo stato della board, in caso di colpo valido, e calcolare il risultato (`processValidAttack`).

```scala
private def processValidAttack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
  val newBoard = board.hit(position)
  val result = board.shipAtPosition(position)
    .map(attackShip(newBoard, position))
    .getOrElse(Right(AttackResult.Miss))
  (newBoard, result)
```
In caso di colpo valido, quindi, viene aggiornata la board attraverso `hit(position)`.
Se c’è una nave in quella posizione (`shipAtPosition`), allora viene calcolato il nuovo stato della nave 
e si decide se è `Hit`, `Sunk` o `EndOfGame`, attraverso il metodo `attackShip`:
```scala
private def attackShip(board: PlayerBoard, position: Position)(ship: Ship): Either[String, AttackResult] =
  for
    damagedShip <- Right(findDamagedShip(board, ship))
    updatedShip <- damagedShip.hit(position).toRight("Invalid attack")
  yield determineAttackResult(updatedShip, board)
```
Se invece non c’è nessuna nave, ritorna `Miss`.

Per gestire lo stato dei danni di una nave è stata sfruttata una struttura immutabile `DamagedShip`, 
che rappresenta una nave con lo stato dei colpi ricevuti (`hitPositions`).
Contiene un metodo `isSunk` per confrontare le posizioni colpite con quelle della nave 
e permettere di stabilire se la nave è affondata, e un metodo `hit` che aggiorna lo stato aggiungendo un colpo, 
solo se la posizione appartiene alla nave.
La scelta di creare una struttura separata, invece di mutare la nave stessa,
rispetta il principio di immutabilità e di separazione delle responsabilità, mantenendo il design modulare:
la nave resta un’entità astratta, mentre lo stato dei danni è un’informazione calcolata a runtime.

Il metodo `attack`, infine, ritorna sia la nuova board aggiornata, sia il risultato dell'attacco, permettendo 
una gestione sicura degli errori: con `Either[String, AttackResult]` vengono distinti errori veri 
(posizione invalida) da casi di gioco (colpo già effettuato).

### Player e strategia di attacco

### TDD

### Contributi nella GUI
