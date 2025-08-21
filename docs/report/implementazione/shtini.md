---
title: Dilaver Shtini
nav_order: 2
parent: Implementazione
---

# Implementazione - Dilaver Shtini
Il codice prodotto dal sottoscritto durante la realizzazione del progetto riguarda i file: `BattleHandler`, `PositioningHandler`, `DelayedExecutor`, `TurnLogic`, `BotTurnHandler`, `GameStateManager`, `BattleLogic`, `ShipPositioning`, `ButtonFactory`, `DialogFactory`, `GridManager`, `ClickHandler`, `TurnDialogHandler`, `ButtonRenderer`, `ColorScheme`, e in parte `GameController` e `GameView`, suddivisi nelle seguenti parti:

## Gestione del posizionamento delle navi nella board
Affidato durante il primo dei quattro sprint, è stato realizzato seguendo l'approccio *TDD* (Test Driven Development). Questa fase ha portato alla realizzazione del file di test `ShipPositioningTest` per verificare il corretto funzionamento del posizionamento delle navi. La logica è stata successivamente implementata in `ShipPositioning`.
La classe `ShipPositioning` si occupa quindi di gestire il posizionamento delle navi, la loro rotazione e lo spostamento, garantendo che le operazioni siano valide tramite controlli di possibili sovrapposizioni o fuori dai limiti della board. Permette inoltre di posizionare le navi in modo casuale ma con un numero massimo di tentativi per evitare loop infiniti
Ho cercato di applicare il più possibile gli aspetti della programmazione funzionale, sfruttando i seguenti principi:
-   Immutabilità: tutte le funzioni sono *pure*, non modificano mai lo stato esistente, ma restituiscono una nuova istanza della board;
```scala
def moveShip(board: PlayerBoard, ship: Ship, position: Position): Either[String, PlayerBoard]
```

-   Errore Handling con Either Monad: l'utilizzo di `Either` per la gestione degli errori e l'uso del `for-comprehension` per concatenare operazioni che potrebbero fallire;
```scala
override def validateShipPlacement(board: PlayerBoard, ship: Ship): Either[String, Unit] =
  for
    _ <- checkBounds(ship)
    _ <- checkOverlap(board, ship)
  yield ()
```

-   Higher-Order Functions: per aderire al riutilizzo del codice ed evitare la ripetizioni di funzioni simili. Nel mio caso l'utilizzo della funzione `shift` permette di generalizzare l'azione da applicare sulla nave, nel caso di *movimento* oppure di *rotazione*;
```scala
private def ShiftShip(
    board: PlayerBoard,
    ship: Ship,
    shift: Ship => Ship
): Either[String, PlayerBoard]
```

-   Ricorsione Tail-Recursive: l'utilizzo del `@tailrec` annotation per garantire ottimizzazione lì dove il costo dell'operazione può diventare elevato. Evitiamo così il rischio di stack overflow e di loop imperativi;
```scala
@tailrec
def tryPlaceShips(playerBoard: PlayerBoard, remaining: List[Ship], attempts: Int): Either[String, PlayerBoard] = {
  if remaining.isEmpty then Right(playerBoard)
  else if attempts > maxAttempts then Left("Failed to place all ships after maximum attempts.")
  else
    ....
    tryPlaceShips(updatedBoard, remaining.tail, 0)
}
```

-   Pattern Matching e ADT (Algebraic Data Types): il compilatore garantisce la gestione di tutti i casi, evitando così possibili casi edge.
```scala
playerBoard.addShip(movedShip) match
  case Right(updatedBoard) => tryPlaceShips(updatedBoard, remaining.tail, 0)
  case Left(error)         => tryPlaceShips(playerBoard, remaining, attempts + 1)
```

Per generalizzare la fase di validazione, nel caso in cui in futuro bisogni aggiungere altri controlli, come ad esempio l'aggiunta di ostacoli nella mappa, ho scelto di isolare ogni responsabilità, per poi creare le validazioni tramite l'utilizzo del  *for-comprehension*, sfruttando così il principio `fail-fast`, così che alla prima validazione fallita viene interotta la catena.
```scala
private def checkBounds(ship: Ship): Either[String, Unit]
private def checkOverlap(board: PlayerBoard, ship: Ship): Either[String, Unit]
```

## Gestione del feedback a seguito di azioni dell'utente
La classe `GameView` si occupa di presentare all'utente lo stato del gioco e fornire feedback visivo.
Gestito da tutti i membri del team in momenti differenti, mi sono occupato principalmente della gestione del *GameOver*, della visualizzazione del cambio del turno tramite la funzione *updateTurnLabel* e dell'aggiornamento dello stato del game nella funzione *update*.

Il sistema aggiorna la board da visualizzare in base al turno del player utilizzando la classe `GridManager` per creare a ogni *update* una griglia di bottoni aggiornati (colore e testo) che rappresentano il nuovo stato della board di gioco e tramite il `ButtonFactory` che si occupa di creare i bottoni della mappa e il bottone per iniziare la partita.
Viene inoltre delegata la gestione del click dei bottoni al `ClickHandler`, che si occupa di gestire i diversi tipi di click dell'utente, come il *SingleClick* e il *DoubleClick*.

### GridManager
Per evitare side-effect indesiderati la funzione `createButtons` non modifica lo stato di gioco, ma costruisce una nuova sequenza di bottoni a partire dagli input:
```scala
def createButtons(state: GameState, turn: Turn): IndexedSeq[Button] =
    for {
      y <- 0 until PlayerBoard.size
      x <- 0 until PlayerBoard.size
    } yield {
      val pos = Position(x, y)
      val btn = ButtonFactory.createGridButton(pos, state, turn)

      listenTo(btn)
      reactions += {
        case ButtonClicked(`btn`) =>
          val now                        = System.currentTimeMillis()
          val (newClickState, clickType) = ClickHandler.processClick(pos, now, clickState)
          clickState = newClickState
          ClickHandler.handleClick(clickType, controller)
      }
      btn
    }
```
La board viene costruita tramite il `for-comprehension`, evitando così l'uso di un doppio for imperativo, migliorando la leggibilità del codice.
La separazione delle responsabilità ha portato alla creazione del `ButtonFactory` per creare i bottoni, che incapsula la logica di rendering, mentre la gestione del click è delegata al `ClickHandler`:

### ButtonFactory
```scala
object ButtonFactory:
  def createGridButton(pos: Position, state: GameState, turn: Turn): Button =
    new Button(ButtonRenderer.getText(pos, state, turn)) {
      opaque = true
      background = ButtonRenderer.getColor(pos, state, turn)
    }
```

### ButtonRenderer
```scala
object ButtonRenderer:
  def getColor(pos: Position, state: GameState, turn: Turn): java.awt.Color = {
    state.gamePhase match
      case GamePhase.Positioning                 => positioningColor(pos, state, turn)
      case GamePhase.Battle | GamePhase.GameOver => battleColor(pos, state, turn)
  }
```

In base alla fase della partita, sfruttato grazie al *Pattern matching*, viene separata la logica chiamando funzioni specializzate. Viene inoltre garantito il *type safety* gestendo tutti i casi tramite l'*exhaustive matching*.

### Gestione State-Based del rendering
```scala
private def battleText(pos: Position, state: GameState, turn: Turn): String =
  turn match {
    case Turn.FirstPlayer =>
      state.attackResult.get(pos) match
        case Some(AttackResult.Miss) => "X"
        case Some(AttackResult.Hit(_) | AttackResult.Sunk(_)) => "O"
        case None => ""
    case Turn.SecondPlayer =>
      state.enemyAttackResult.get(pos) match
        ....
  }
```

### ColorScheme
```scala
object ColorScheme:
  val UNOCCUPIED: Color    = java.awt.Color.CYAN
  val SELECTED_SHIP: Color = java.awt.Color.YELLOW
  val OCCUPIED: Color      = java.awt.Color.BLACK
```

I colori sono definiti all'interno di un file dedicato per organizzare le costanti

## Gestione della battaglia e turnazione
A seguìto della creazione dei bottoni della mappa, ogni interazione con essa produce diversi tipi di *click*, definiti all'interno di `ClickType`. In questa versione dell'applicazione abbiamo due tipi di click: *SingleClick* e *DoubleClick*. La prima, gestisce i singoli click inerenti alla mappa, attraversando ogni fase della partita: *Posizionamento*, *Battaglia* e *Game Over*, fasi definite come `enum` all'interno del `GameController`. Il secondo tipo di *click* riguarda solamente la fase di *Posizionamento*, e permette di ruotare la nave attorno alla sua *ancora* se cliccata rapidamente (entro un numero prestabilito di millisecondi).
La fase iniziale di *posizionamento* viene gestita tramite il bottone creato dal `ButtonFactory` in caso di multiplayer, altrimenti appena il player umano termina la sua disposizione delle navi, la partita ha inizio. 
Successivamente, durante la fase di *battaglia*, a ogni azione dell'utente (player umano o bot) verrà visualizzato dapprima il risultato dell'attacco nella mapp, e in seguito un *dialog* che permetterà la corretta gestione del turno. Tutti i messaggi di *dialog* sono gestiti tramite il `TurnDialogHandler`, che sfrutta il `DialogFactory` per racchiudere tutti i messaggi utilizzati nell'applicazione.

### Gestione dei Click e degli Eventi
Il file `ClickHandler` implementa la logica per gestire i diversi tipi di click dell'utente descritti precedentemente.
```scala
sealed trait ClickType
object ClickType:
  case class SingleClick(pos: Position) extends ClickType
  case class DoubleClick(pos: Position) extends ClickType
```
L'uso del *sealed trait* garantisce che tutti i casi siano noti a compile-time. Inoltre, è garantita l'immutabilità perché le *case class* sono immutabili per design. Il *Pattern matching exhaustive* permette infine al compilatore di verificare che tutti i casi siano gestiti.

L'uso del *pattern matching* permette di controllare il flusso dichiarativo invece di if-else imperativi.
```scala
def handleClick(clickType: ClickType, controller: GameController): Unit =
  clickType match
    case ClickType.SingleClick(pos) => controller.onCellClick(pos)
    case ClickType.DoubleClick(pos) => controller.onCellDoubleClick(pos)
```

### Sistema di Gestione della battaglia
#### BattleHandler
Il `BattleHandler coordina la logica di battaglia senza implementarla direttamente, delegando gli attacchi, umani e bot, a `BattleLogic`.
Le funzioni sono il risultato della combinazione di risultati di altre funzioni, sfruttando così la delegazione della logica a moduli specializzati.

```scala
object BattleHandler:
  case class BattleResult(newState: GameState, messages: List[String], gameOver: Boolean)
  
  def processHumanAttack(gameState: GameState, player: Player, turn: Turn, position: Position): BattleResult
  def processBotAttack(gameState: GameState, botPlayer: Player, turn: Turn): BattleResult
```

```scala
  def processHumanAttack(
      gameState: GameState,
      player: Player,
      turn: Turn,
      position: Position
  ): BattleResult =
    val clickResult = BattleLogic.processBattleClick(gameState, player, turn, Some(position))
    val gameOver    = clickResult.newState.gamePhase == GamePhase.GameOver
    BattleResult(clickResult.newState, clickResult.messages, gameOver)

  def processBotAttack(gameState: GameState, botPlayer: Player, turn: Turn): BattleResult =
    val clickResult = BattleLogic.processBattleClick(gameState, botPlayer, turn, None)
    val gameOver    = clickResult.newState.gamePhase == GamePhase.GameOver
    BattleResult(clickResult.newState, clickResult.messages, gameOver)
```

`BattleLogic` contiene la logica core di battaglia, e questa separazione, permette testabilità e manutenibilità, isolando le modifiche a singole responsablità.
Viene definita una *case class* per il risultato della battaglia, che incapsula lo stato aggiornato, i messaggi da mostrare e un flag per indicare se il turno deve essere cambiato.
Questa gestione del risultato permette così i restituire più informazioni necessarie in un'unica operazione, mantenendo l'immutabilità.
```scala
case class BattleClickResult(newState: GameState, messages: List[String], shouldChangeTurn: Boolean)
```

La funzione per eseguire l'attacco del player umano è delegata a `player.makeAttack()` che ritorna il risultato dell'attacco e la board aggiornata.
```scala
private def performHumanAttack(
    player: Player,
    board: PlayerBoard,
    position: Position
): (PlayerBoard, Either[String, AttackResult])  =
    val (updatedBoard, result) = player.makeAttack(board, Some(position))
    
    result match
    case Right(AttackResult.AlreadyAttacked) =>
    (board, Left(s"Position $position already attacked. Please choose another position."))
    case _ =>
      (updatedBoard, result)
```
Anche in questo caso ho utilizzato `Either` per la gestione degli errori:
permettendo cosi la composizione e una gestione esplicita degli errori, evitando errori a compile-time.

Nel caso in cui l'attacco del player non sia valido, ad esempio attacca una posizione già attaccata restituendo quindi come risultato dell'attacco *AlreadyAttack*, l'applicazione permetterà di ripetere l'attacco finché non si ottiene un valido risultato.
La gestione della battaglia, e quindi anche di queste casistiche è gestita nella funzione `processBattleClick`, che richiama `performHumanAttack` o `performBotAttack` a seconda del tipo di player che sta attaccando.
Inoltre è responsabile anche di aggiornare le board dei giocatori, tramite l'utilizzo di `.copy()` per mantenere l'immutabilità dello stato di gioco.
Infine viene delegato alla funzione `processAttackResult` il risultato dell'attacco, che si occupa di controllare se una nave è affondata o se la partita è finita, altrimenti aggiorna lo stato per la visualizzazione della bord tramite `.copy()`.
```scala
def processBattleClick(
    state: GameState,
    player: Player,
    turn: Turn,
    pos: Option[Position]
): BattleClickResult =
    val (targetBoard, isAttackingSecondBoard) = turn match
    case Turn.FirstPlayer  => (state.enemyBoard, true)
    case Turn.SecondPlayer => (state.board, false)

    val (updatedBoard, attackResult) = if player.isABot then
      performBotAttack(player, targetBoard)
    else
      pos match
        case Some(position) => performHumanAttack(player, targetBoard, position)
        case None           => (targetBoard, Left("Position required for human player"))

    attackResult match
      case Right(result) =>
        val newState = if isAttackingSecondBoard then
          state.copy(enemyBoard = updatedBoard)
        else
          state.copy(board = updatedBoard)
        val attackPosition = pos.getOrElse {
          findAttackedPosition(targetBoard, updatedBoard) match {
            case Some(position) => position
            case _ =>
              updatedBoard.hits.headOption.getOrElse {
                throw new IllegalStateException("No attacked position found and no fallback available")
              }
          }
        }
        val (finalState, message) = processAttackResult(turn, newState, attackPosition, result)
        BattleClickResult(finalState, List(message), shouldChangeTurn = true)
      case Left(errorMessage) =>
        BattleClickResult(state, List(errorMessage), shouldChangeTurn = false)
```

Come nel `ShipPositioning`, anche qui viene usata l'annotazione @tailrec per gestire l'attacco del bot fino al raggiungimento di un risultato valido, ovvero diverso dal *AlreadyAttacked*.
```scala
@tailrec
def attemptAttack(currentBoard: PlayerBoard, retriesLeft: Int): (PlayerBoard, Either[String, AttackResult]) =
  if (retriesLeft <= 0)
    return (currentBoard, Left("Bot failed to find valid attack position after maximum retries"))

  val (updatedBoard, result) = player.makeAttack(currentBoard, None)

  result match
    case Right(AttackResult.AlreadyAttacked) =>
      attemptAttack(currentBoard, retriesLeft - 1)
    case _ =>
      (updatedBoard, result)

attemptAttack(board, maxRetries)
```

```scala
private def updateSunkShipResult(turn: Turn, state: GameState, ship: Ship, sunkResult: AttackResult): GameState =
  turn match
    case Turn.FirstPlayer =>
      val updatedAttackResults = ship.positions.foldLeft(state.attackResult) { (results, position) =>
        results + (position -> sunkResult)
      }
      val updatedCellColors = ship.positions.foldLeft(state.cellColors) { (colors, position) =>
        colors + (position -> ColorScheme.SUNK)
      }
      state.copy(attackResult = updatedAttackResults, cellColors = updatedCellColors)
```
L'utilizzo di `foldLeft` permette la trasformazione aggregata di collezioni, quindi posso applicare le trasformazioni ad ogni posizione della nave

### Sistema di turnazione asincrona
Il `BotTurnHandler` gestisce la turnazione del bot utilizzando effetti temporali e asincroni. Mostra
dialog di attesa durante il turno del bot e mostra i risultati con timing appropriato.
```scala
def scheduleBotMove(state: GameState, view: GameView, turn: Turn, firstPlayer: Player, secondPlayer: Player): Unit =
  controller.dialogHandler.foreach(_.showWaitingDialog())

    DelayedExecutor.runLater(1500) {
      val result = handleBotTurn(state, turn, firstPlayer, secondPlayer)

      controller.state = result.newState
      controller.turn = result.newTurn

      Swing.onEDT {
        view.update(turn)
      }

      DelayedExecutor.runLater(500) {
        controller.dialogHandler.foreach(_.hideCurrentDialog())

        val attackMessage = result.messages.headOption.getOrElse("Attack completed")

        controller.dialogHandler.foreach(_.showBotResultDialog(
          attackMessage,
          () => {
            controller.endBotTurn()
          }
        ))
      }
    }
```
Mantiene l'immutabilità delle operazioni, creando un nuovo stato a ogni operazione, e sfrutta le *high-order functions*, in `DelayedExecutor.runLater`, che accetta funzioni come parametri per poterli eseguire. 
Viene gestito il turno del bot dove in una funzione separata privata, *handleBotTurn*, viene gestito la fase d'attacco del bot, con successiva stampa dei messaggi di dialog, tutto coordinato tramite i timer e le interazioni con i dialog.

Il `DelayExecutor` incapsula la gestione asincrona:
```scala
def runLater(delayMs: Int = 1000)(action: => Unit): Unit =
    Timer(delayMs)(action)
```
Viene usato il *default parameters* per fornire un valore di default per eseguire azioni dopo un delay specificato, e inoltre, tramite le *lazy evaluation*, le azioni sono valutate solo quando eseguite.

Il `TunDialogHandler` gestisce la visualizzazione di dialog per la turnazione e feedback:
Sono gestiti diversi tipi di dialog, come il dialog per il cambio turno, per la gestione del feedback per azioni del bot e per l'attesa durante l'elaborazione di azioni.
Viene usato l'*Option type* per gestire in sicurezza lo stato del dialog. Inoltre, l'utilizzo del *DialogFactory* ha permesso di separare la creazione dalla gestione dei dialog.
```scala
class TurnDialogHandler(gui: GameView):
  private var currentDialog: Option[JDialog] = None
  
  def showTurnDialog(playerName: String): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createTurnDialog(gui, playerName)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)
```

Il `GameStateManager` coordina le transizioni di stato del gioco nelle diverse fasi.
Inoltre gestisce anche le transizioni tra la modalità *single player* e *multi player*

Viene utilizzato l'`ADT` per le azioni di dialog, beneficiando di vari aspetti:
-   tutti i casi devono essere gestiti
-   è impossibile creare azioni non valide
-   ogni azione ha un significato preciso
```scala
enum DialogAction:
  case ShowTurnDialog(playerName: String)
  case ShowWaitingDialog
  case RetryAttack
  case HideDialog
  case ShowBotResultDialog(result: String)
```

Ho realizzato un *case class* per la gestione di risultati compositi, visto che ogni azione di gioco genera più valori come il nuovo stato di gioco, il nuovo turno, eventuali messaggi da mostrare e un'azione opzionale da mostrare nella GUI,
aggiungendo un metodo helper `withDialog` per aggiungere azioni di dialog ai risultati.
```scala
case class GameActionResult(newState: GameState, newTurn: Turn, messages: List[String], showDialog: Option[DialogAction] = None):
  def withDialog(dialog: DialogAction): GameActionResult = copy(showDialog = Some(dialog))
```
Per la gestione dell'avvio del gioco viene invocato `handleStartGame` che gestisce tre scenari:
-   partita in modalità *single player* con bot e turno di posizionamento del bot
-   partita in modalità *multi player* con gestione del posizionamento dei tue player tramite dialog
-   avvio della battaglia dopo il posizionamento delle navi in entrambe le modalità

Ogni funzione ritorna un nuovo *GameActionResult*. Per la fase di posizionamento, l'azione è delegata al *PositioningHandler*, ma il risultato viene incapsulato sempre in un *GameActionResult*, come si può notare per le funzioni *handlePositioningClick*, *handlePositioningDoubleClick*, *handleRandomizePositions*.
Anche per la fase d'attacco, nel caso di un player umano, in *handleBattleClick*, viene restituito il *GameActionResult*, dove in caso di fine partita, ritorna lo stato finale, altrimenti controlla se cambiare il turno o riproporre l'attacco per un attacco invalido precedente.
Nel caso in cui sia il turno d'attacco del bot, l'attacco viene effettuato automaticamente, e in caso di non vittoria, viene aggiornato il turno.
```scala
def handlePositioningClick(
      gameState: GameState,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameActionResult =
    val newState = PositioningHandler.handlePositioningClick(gameState, position, turn, positioning)
    GameActionResult(newState, turn, List())
```
```scala
def handlePositioningDoubleClick(
      gameState: GameState,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameActionResult =
    val newState = PositioningHandler.handlePositioningDoubleClick(gameState, position, turn, positioning)
    GameActionResult(newState, turn, List())
```
```scala
def handleRandomizePositions(
      gameState: GameState,
      ships: List[Ship],
      turn: Turn,
      positioning: ShipPositioning
  ): GameActionResult =
    val newState = PositioningHandler.handleRandomizePositions(gameState, ships, turn, positioning)
    GameActionResult(newState, turn, List())
```

### PositioningHandler
Gestisce le interazioni dell'utente durante la fase di posizionamento.
```scala
object PositioningHandler:
  def handlePositioningClick(gameState: GameState, position: Position, turn: Turn, positioning: ShipPositioning): GameState
  def handlePositioningDoubleClick(gameState: GameState, position: Position, turn: Turn, positioning: ShipPositioning): GameState
```
Ogni operazione restituisce un nuovo stato, e viene sfruttato il *pattern matching* per la gestione dichiarativa delle diverse situazioni.
Infine, la composizione permette di eseguire operazioni complesse composte da operazioni semplici, evitando così possibili *side effects*.


```scala
def selectShipAt(gameState: GameState, position: Position, turn: Turn, positioning: ShipPositioning): GameState =
  val currentBoard = getBoardForTurn(gameState, turn)
  positioning.getShipAt(currentBoard, position) match
    case Right(ship) => gameState.selectShip(ship)
    case Left(_)     => gameState
```
Tramite il *pattern matching su Either*, la gestione di successo/errore è più pulita e facilmente leggibile.

Per la gestione dei callbacks sono state usate *high-order functions*:
```scala
def showBotResultDialog(result: String, onDismiss: () => Unit): Unit =
  val dialog = DialogFactory.createBotResultDialog(gui, result, onDismiss)
```
ottenendo così *flessibilità*, dove diversi comportamenti posso essere passati come parametri e *composizione*, i callback possono essere composti funzionalmente.

La gestione dello stato opzionale utilizza operazioni monadiche per evitare null pointer exception:
```scala
controller.dialogHandler.foreach(_.showWaitingDialog())
controller.dialogHandler.foreach(_.hideCurrentDialog())
```

## Testing
Come anticipato nella sezione della *gestione del posizionamento delle navi nella baord*, ho utilizzato il *TDD* per 
la realizzazione della logica di posizionamento delle navi, creando il file `ShipPositioningTest` per poi andare a
implementare la logica in `ShipPositioning`. Purtroppo, non sono riuscito a garantire questo approccio anche per gli
altri file da me implementati, i quali hanno visto solo in seconda sede la realizzazione dei test, come ad esempio 
`BattleLogicTest` e `TurnLogicTest`. Questo è stato possibile solo grazie a un refactor che ha permesso 
di separare la logica di battaglia e turnazione, permettendo così di testare le singole responsabilità.
Per lo sviluppo dei test ho utilizzato ScalaTest

### ShipPositioningTest
In questo test viene verificato il corretto funzionamento della logica di posizionamento delle navi, con casi di test per:
-   recupero navi: verifica la capacità di trovare una nave in una posizione specifica
-   posizionamento: verifica il corretto posizionamento delle navi
```scala
"ShipPositioning" should "successfully return a ship at a given position" in:
    val testBoard = board(
        place a Frigate at A(1) vertical
    )
    val position = A(1)

    val result = shipPositioning.getShipAt(testBoard, position)

    result.isRight shouldBe true
    result.getOrElse(fail()).anchor shouldBe A(1)
```

È stato fatto uso del `Either` per gestire il risultato della ricerca della nave, dove in caso di successo viene restituita la nave, altrimenti un messaggio di errore.
Inoltre, l'uso del `DSL` per la creazione della board permette di rendere i test più leggibili e concisi, evitando la creazione manuale di ogni singolo elemento della board.

```scala
it should "return an error when no ship is found at the position" in:
    val testBoard = board()
    val position  = B(2)

    val result = shipPositioning.getShipAt(testBoard, position)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("No ship found at the selected position.")
```

```scala
it should "successfully place a ship on empty board" in:
    val ship  = Frigate.at(A(1), Vertical)
    val board = PlayerBoard()

    val result = shipPositioning.placeShip(board, ship)

    result.isRight shouldBe true
    val updatedBoard = result.getOrElse(fail())
    updatedBoard.ships should have size 1
    updatedBoard.ships.head.anchor shouldBe A(1)
```

```scala
it should "fail when ship overlaps with existing ship" in:
    val testBoard = board(
        place a Frigate at A(1) vertical
    )
    val newShip = Submarine.at(A(1), Vertical)

    val result = shipPositioning.placeShip(testBoard, newShip)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("overlap")
```

### BattleLogicTest
Il test `BattleLogicTest` verifica la logica di battaglia, assicurando che gli attacchi vengano gestiti correttamente e che le navi vengano affondate, mancate o colpite come previsto (Sunk/Miss/Hit).
Viene inoltre controllato il caso di attacco a una cella già attaccata in precedenza, creando quindi il messaggio *AlreadyAttack*. È presente anche la verifica della differenziazione tra i due player, i quali attaccano le rispettive board.
Viene gestito anche il caso di transizione a *Game over* e l'aggiornamento dei colori dei bottoni nella board.
Per l'implementazione dei test ho usato `pattern matching` per la gestione dei risultati di gioco, i `case class` per rappresentare lo stato immutabile del gioco,
e `foldLeft` per accumulare stati attraverso attacchi multipli.  

```scala
"BattleLogic.processBattleClick" should "handle human player miss attack correctly" in:
    val emptyEnemyBoard = PlayerBoard()
    val gameState       = initialGameState.copy(enemyBoard = emptyEnemyBoard)
    val missPosition    = Position(0, 0)

    val result = BattleLogic.processBattleClick(
      gameState,
      humanPlayer,
      Turn.FirstPlayer,
      Some(missPosition)
    )

    result.messages should have size 1
    result.messages.head should include("Miss")
    result.newState.attackResult should contain key missPosition
    result.newState.attackResult(missPosition) shouldBe AttackResult.Miss
    result.newState.cellColors(missPosition) shouldBe ColorScheme.MISS
```

Infine è stato possibile anche testare una sequenza di attacchi per verificare la corretta gestione di multipli attacchi:
```scala
it should "handle multiple consecutive attacks correctly" in:
    val gameState = initialGameState.copy(enemyBoard = emptyBoard)
    val positions = List(Position(0, 0), Position(1, 1), Position(2, 2))

    val finalResult = positions.foldLeft((gameState, List.empty[String])) {
      case ((currentState, messages), pos) =>
        val result = BattleLogic.processBattleClick(
          currentState,
          humanPlayer,
          Turn.FirstPlayer,
          Some(pos)
        )
        (result.newState, messages ++ result.messages)
    }

    val (finalState, allMessages) = finalResult

    allMessages should have size 3
    allMessages.foreach(_ should include("Miss"))
    finalState.attackResult should have size 3
    positions.foreach { pos =>
      finalState.attackResult should contain key pos
      finalState.cellColors should contain key pos
    }
```

### TurnLogicTest
In questo file viene verificata la tipologia del player, se human o bot e il meccanismo di turnazione.
Sono stati usati degli `enum` per stati, come `Turn.FirstPlayer`.

```scala
"TurnLogic.isBotTurn" should "return true when current player is a bot" in:
    TurnLogic.isBotTurn(Turn.FirstPlayer, botPlayer, humanPlayer) shouldBe true
    TurnLogic.isBotTurn(Turn.SecondPlayer, humanPlayer, botPlayer) shouldBe true
```

```scala
"TurnLogic.switchTurn" should "switch from FirstPlayer to SecondPlayer" in:
  TurnLogic.switchTurn(Turn.FirstPlayer) shouldBe Turn.SecondPlayer
```

