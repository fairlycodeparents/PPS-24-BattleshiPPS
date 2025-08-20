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

La modellazione dei giocatori è affidata ad un `trait` `Player`, 
che fornisce un’interfaccia uniforme sia per i giocatori umani che per i bot.
Questa scelta permette di definire un contratto uniforme per tutte le tipologie di giocatori 
e di rendere semplice l’estensione del sistema con nuove varianti.

Attraverso il metodo `makeAttack` viene eseguito un attacco sulla board avversaria,
restituendo la board aggiornata e l’`AttackResult`.

Il `trait` viene concretamente implementato da due `case class`:
```scala
case class HumanPlayer(name: String = "player", strategy: AttackStrategy = HumanAttackStrategy()) extends Player:
  override def makeAttack(
    playerBoard: PlayerBoard,
    position: Option[Position]
    ): (PlayerBoard, Either[String, AttackResult]) = strategy.execute(playerBoard, position)
  override def isABot: Boolean = false

case class BotPlayer(strategy: AttackStrategy) extends Player:
  override def makeAttack(
    playerBoard: PlayerBoard,
    position: Option[Position]
    ): (PlayerBoard, Either[String, AttackResult]) = strategy.execute(playerBoard, position)
  override def isABot: Boolean = true
```

La creazione dei giocatori è centralizzata in una factory, 
che semplifica l’inizializzazione e incapsula le decisioni sulle strategie da assegnare ai giocatori:
- `createHumanPlayer(name: String): Player = HumanPlayer(name)`
- `createBotPlayer(strategy: AttackStrategy = RandomBotAttackStrategy()): Player = BotPlayer(strategy)`

L’aspetto centrale è che ogni giocatore delega la logica di attacco a un `AttackStrategy`, 
secondo lo Strategy Pattern. 

In questo modo la responsabilità della scelta della mossa non è all’interno del giocatore, 
ma affidata a una strategia intercambiabile. 
Questo rende il sistema modulare, facilmente estendibile con nuove strategie e più semplice da mantenere.

Il `trait` `AttackStrategy` espone un metodo `execute(playerBoard: PlayerBoard, position: Option[Position]): (PlayerBoard, Either[String, AttackResult])`
che definisce il contratto per ogni strategia.
Per i bot, `position` è `None`, mentre per l’umano, `position` deve essere specificata.

Le strategie implementate sono:
- `HumanAttackStrategy`: il giocatore fornisce la posizione manualmente.
- `RandomBotAttackStrategy`: il bot sceglie una cella casuale nella board.
```scala
override def execute(
  playerBoard: PlayerBoard,
  position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = position match
    case Some(pos) => (playerBoard, Left("Position should not be required for a bot attack"))
    case None      => ShipAttack.attack(playerBoard, generateRandomPosition)
```
- `TargetAlreadyHitStrategy`(mixin): non è una strategia autonoma, ma un modulo riutilizzabile 
che privilegia le celle adiacenti a quelle già colpite. Viene pensato per essere combinato ad altre strategie, 
incrementandone l’intelligenza senza duplicare codice.
```scala
abstract override def execute(
    playerBoard: PlayerBoard,
    position: Option[Position]
): (PlayerBoard, Either[String, AttackResult]) = position match
  case Some(pos) => (playerBoard, Left("Position should not be required for a bot attack"))
  case None =>
    getAdjacentToAttack(playerBoard) match
      case Some(value) =>
        ShipAttack.attack(playerBoard, value)
      case None =>
        super.execute(playerBoard, position)
``` 
- `AverageBotAttackStrategy`: arricchisce la strategia casuale (`extends RandomBotAttackStrategy`) con il mixin citato (`with TargetAlreadyHitStrategy`).
  Questo permette al bot di “seguire” un colpo andato a segno, rendendo l’attacco più intelligente.
- `UniformDistributionStrategy`
- `AdvancedBotAttackStrategy`

Questa tecnica ha permesso di comporre comportamenti complessi a partire da strategie più semplici,
evitando duplicazioni e mantenendo un codice leggibile ed estendibile.
L’implementazione delle strategie basate sui mixin è stata sviluppata in _pair programming_ con Mirco Terenzi, 
suddividendo i task in base al livello di intelligenza del bot:
- io mi sono occupata della realizzazione del bot medio (`AverageBotAttackStrategy`), 
    che combina la logica casuale con il targeting delle posizioni adiacenti già colpite;
- Mirco ha invece sviluppato il bot avanzato (`AdvancedBotAttackStrategy`), 
  che integra la distribuzione uniforme dei colpi (`UniformDistributionStrategy`) con lo stesso targeting intelligente.

  
### TDD

### Contributi nella GUI
