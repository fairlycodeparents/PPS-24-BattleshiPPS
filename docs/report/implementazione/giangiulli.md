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
Ciascuna delle parti citate è stata sviluppata cercando di seguire un approccio Test Driven:
prima sono stati scritti mano a mano i test per formalizzare i comportamenti attesi, 
successivamente è stata realizzata l’implementazione per soddisfarli, seguita da eveltuali refactor.
Per la scrittura dei test è stato utilizzato ScalaTest, con il mixin `should.Matchers`, per mantenere una sintassi leggibile, 
con descrizioni in linguaggio naturale e matcher espressivi (come ad esempio `should`, `shouldBe`, `matchPattern`).

I file di test interessati sono, quindi:
- `ShipTest`, per lo sviluppo delle navi:
  - verifica che le diverse tipologie di navi abbiano la lunghezza corretta
  - controlla la corretta creazione e rotazione delle navi
  
  ad esempio:
  ```scala
  it should "be able to rotate" in:
    val rotatedShip = ship.rotate
    rotatedShip.positions shouldBe Set(position, Position(2, 4))
  ```
  - assicura che le posizioni occupate e i movimenti siano calcolati correttamente
- `ShipAttackTest` per la logica di attacco
  - simula attacchi reali su una board
  - copre casi di Hit, Sunk, Miss, AlreadyAttacked e Invalid attack position
  
  ad esempio:
  ```scala
  it should "be sunk after all its positions are hit" in:
    val (_, result) = frigateSunk(board)
    result shouldBe Sunk(frigate)
  ```
  sfruttando degli _helper methods_, che effettuano sequenze di attacchi finché la nave non viene affondata:
  ```scala
  def frigateSunk(initialBoard: PlayerBoard): (PlayerBoard, AttackResult) =
    val (board1, _)      = attack(initialBoard, A(5))
    val (board2, result) = attack(board1, B(5))
    (board2, result)
  ```
  - valida la condizione di fine gioco (quando tutte le navi sono affondate)
- `PlayerTest` per lo sviluppo dei giocatori e la strategia di attacco:
  - verifica la creazione dei giocatori tramite factory (umani e bot)
  - controlla la corretta applicazione delle strategie (`HumanAttackStrategy`, `RandomBotAttackStrategy`, `AverageBotAttackStrategy`)
  - simula scenari complessi con bot di intelligenza media (attacchi adiacenti, capacità di affondare una nave, 
  ritorno al comportamento casuale dopo l'affondamento)
  
  ad esempio:
  ```scala
  "An average smart bot" should "be able to hit an adjacent position after a hit" in:
    val bot               = createBotPlayer(AverageBotAttackStrategy())
    val (updatedBoard, _) = ShipAttack.attack(enemyBoard, G(1))
    val expectedAdjacent  = List(F(1), H(1), G(2))
    val (finalBoard, res) = bot.makeAttack(updatedBoard)
    val attackedPosition  = finalBoard.hits.diff(updatedBoard.hits).headOption
    attackedPosition shouldBe defined
    expectedAdjacent should contain(attackedPosition.get)
  ```
  Anche in questo caso viene utilizzato un _helper method_, _tail-recursive_, 
che itera sugli attacchi finché una nave non è affondata.
Questo ha permesso di simulare una partita reale:
  ```scala
  def executeAttacksUntilSunk(
    bot: Player,
    initialBoard: PlayerBoard,
    targetShip: Ship,
    maxMoves: Int
  ): (PlayerBoard, Either[String, AttackResult]) =
  
    @scala.annotation.tailrec
    def loop(currentBoard: PlayerBoard, movesLeft: Int): (PlayerBoard, Either[String, AttackResult]) =
      if (movesLeft <= 0) return (currentBoard, Left(s"Failed to sink ship within $maxMoves moves"))
      val (newBoard, result) = bot.makeAttack(currentBoard)
      result match
        case Right(Sunk(ship)) if ship == targetShip => (newBoard, result)
        case Right(Sunk(otherShip))                  => loop(newBoard, movesLeft - 1)
        case Right(_)                                => loop(newBoard, movesLeft - 1)
        case Left(error)                             => (currentBoard, Left(error))
  
    loop(initialBoard, maxMoves)
  ```

### Contributi nella GUI
Infine, gli ultimi contribuiti apportati hanno riguardato alcune parti dell'interfaccia grafica, la cui
implementazione è stata suddivisa tra tutti i membri del team.

In particolare, il mio contributo ha riguardato una prima semplice implementazione iniziale, per il posizionamento
delle navi su una versione base della board e, nell'ultimo sprint, alcuni miglioramenti riguardanti l'usabilità, 
finalizzati a rendere l’esperienza dell’applicazione finale più fluida e intuitiva.
