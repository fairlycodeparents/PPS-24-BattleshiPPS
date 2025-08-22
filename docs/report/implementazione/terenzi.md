---
title: Mirco Terenzi
nav_order: 3
parent: Implementazione
---

# Implementazione - Terenzi Mirco

## Panoramica dei Contributi

Il mio contributo al progetto si è focalizzato sulle seguenti aree:

* [Configurazione della plancia di gioco](#plancia-di-gioco): `PlayerBoard`, `PlayerBoardBuilder`, `BoardFactory`,
`BoardCoordinates`, `ShipPlacementDSL`.
* [Validazione della configurazione scelta dall'utente](#gestione-della-configurazione): `GameConfig`,
`ConfigurationValidator`, `MaxOccupancyValidator`, `NotEmptyValidator`, `ConfigurationManager`.
* [Interfaccia grafica e interazione con l'utente](#contributi-nellinterfaccia-utente): `SetupView`,
`DifficultySelection`, `GameSetup`.
* [Strategie d'attacco del bot](#strategie-dattacco-del-bot): `AdvancedBotAttackStrategy`, `PositionWeighting`,
`MinDistanceWeighting`, `MaxWeightStrategy` e, in collaborazione con Giangiulli Chiara, `TargetAlreadyHitStrategy`.
* [Classi di supporto](#creazione-della-plancia): `Position`.
* [Classi di testing](#testing): `PositionTest`, `PlayerBoardTest`, `PlayerBoardBuilderTest`,
  `ConfigurationValidatorTest`, `MaxWeightStrategyTest`.

---

## Gestione della configurazione

Per avviare una partita, la configurazione scelta dall'utente deve rispettare determinate regole, come garantire un
numero minimo di navi e, al tempo stesso, evitare che la plancia sia troppo piena, il che limiterebbe la libertà di
posizionamento delle navi e renderebbe il gioco meno interessante.

Per questo motivo, è stato definito il trait `ConfigurationValidator`, che si occupa di validare la configurazione di
gioco. Ogni configurazione è rappresentata da una classe `GameConfig`, che incapsula le informazioni sulle navi e le
loro quantità.

```scala
/** This validator ensures the total number of ship cells does not exceed a predefined percentage of the board.
 * @param maxOccupancy The maximum percentage of board cells that can be occupied by ships.
 */
class MaxOccupancyValidator(val maxOccupancy: Double) extends ConfigurationValidator:

  def validate(config: GameConfig): GameConfig =
    val boardCells     = PlayerBoard.size * PlayerBoard.size
    val maxCells       = (boardCells * maxOccupancy).toInt
    val totalShipCells = config.ships.map((shipType, amount) => shipType.length * amount).sum
    
    if totalShipCells <= maxCells then config
    else
      val sortedShips = ShipType.values.sortBy(-_.length)
      val correctedShips = sortedShips.foldLeft((maxCells, Map.empty[ShipType, Int])) {
        case ((remainingCells, acc), shipType) =>
          val maxCount          = config.ships.getOrElse(shipType, 0)
          val maxFit            = remainingCells / shipType.length
          val actualCount       = math.min(maxCount, maxFit)
          val newRemainingCells = remainingCells - actualCount * shipType.length
          (newRemainingCells, acc + (shipType -> actualCount))
      }._2
      GameConfig(correctedShips)
```
Un esempio di validazione è rappresentato da `MaxOccupancyValidator`, il cui ruolo è garantire che il numero di celle
occupate non superi una soglia predefinita, evitando così che la plancia sia eccessivamente piena. Se la configurazione
iniziale è già conforme al limite, il validatore la accetta e la restituisce senza apportare modifiche. Se, invece,
le navi superano il numero massimo di celle consentite, il validatore interviene per "correggere" la configurazione.
Per farlo, genera una nuova istanza di `GameConfig` come segue: le navi vengono ordinate in ordine decrescente in base
alla loro lunghezza e il validatore scorre la lista, mantenendo le navi finché il numero di celle disponibili non si
esaurisce. In questo modo, garantisce che le navi più grandi rimangano nella configurazione, rimuovendo prima quelle
più corte.

Analogamente, il `NotEmptyValidator` verifica che la configurazione includa almeno una nave; se necessario, la corregge
aggiungendo una nave di tipo *Frigate*.

Infine, il `ConfigurationManager` si occupa di applicare tutti i validatori in sequenza, restituendo una configurazione
finale conforme alle regole del gioco.

## Strategie d'attacco del bot

Tra i requisiti del progetto, era prevista la possibilità di scegliere tra varie difficoltà per la partita in modalità
single-player. Queste sono state implementate attraverso diverse strategie di attacco del bot, basate sul `trait`
`AttackStrategy`. Questo modulo di codice è stato sviluppato in parte in *pair programming* con Giangiulli Chiara, che
si è occupata della strategia intermedia, mentre io mi sono concentrato su quella avanzata.

Le strategie sono state definite con le seguenti caratteristiche:
* **Semplice**: il bot attacca in modo completamente casuale.
* **Intermedia**: il bot attacca in modo casuale, ma sfrutta i colpi andati a buon fine per colpire le celle adiacenti
e affondare la nave.
* **Avanzata**: il bot usa una strategia più complessa, combinando la ricerca delle posizioni adiacenti (descritta al
punto precedente) con un sistema di punteggio per determinare la cella migliore da attaccare, basato sulla distribuzione
uniforme dei colpi sulla plancia.

Considerando che le strategie intermedia e avanzata condividono una logica comune, il codice è stato rifattorizzato per
utilizzare un approccio basato su *mixin* e composizione delle strategie. Ciò ha permesso di creare un sistema modulare,
facilitando future estensioni e rendendo il codice più chiaro e manutenibile.

In particolare, ho realizzato `AdvancedBotAttackStrategy` e ho collaborato con Giangiulli per allineare
`TargetAlreadyHitStrategy` e `AverageBotAttackStrategy` al mixin. In questo modo, le strategie possono essere combinate
senza modificare il codice esistente e la logica comune può essere riutilizzata. Ad esempio, la strategia avanzata viene
definita come mostrato di seguito:

```scala
/** An advanced bot attack strategy that combines uniform distribution with targeting already hit positions. */
class AdvancedBotAttackStrategy
  extends MaxWeightStrategy(MinDistanceWeighting())
  with TargetAlreadyHitStrategy
```

Inizialmente, ho implementato una classe `UniformDistributionStrategy` che si occupasse di selezionare la miglior cella
da colpire come è stato precedentemente descritto, distribuendo i colpi sulla mappa in modo da selezionare la cella in
modo da massimizzare la distanza minima dai colpi già sferrati. Successivamente, la strategia è stata sostituita da una
`MaxWeightStrategy`, la quale si occupa solamente di scegliere la cella con il peso (punteggio) maggiore. Questa
modifica ha permesso di delegare l'assegnazione dei pesi a una classe esterna, passata come parametro nel metodo `apply`,
facilitando eventuali estensioni del codice o modifiche. In particolare, questo processo è stato notevolmente
semplificato dai test esistenti, che hanno permesso di verificare il corretto funzionamento dell' implementazione in
ogni momento. 

```scala
/** An attack strategy that uses a uniform distribution to select positions based on their weights.
 * It calculates the weight of each position based on the provided PositionWeighting strategy and chooses the
 * position with the highest weight.
 * @param positionWeighting the strategy to calculate the weight of positions
 */
class MaxWeightStrategy(positionWeighting: PositionWeighting) extends AttackStrategy:

  override def execute(
    playerBoard: PlayerBoard,
    position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) =
    
    /* ... */

    val allPositions =
      for
        x <- 0 until PlayerBoard.size
        y <- 0 until PlayerBoard.size
      yield Position(x, y)

    val unhitPositions = allPositions.filterNot(playerBoard.hits.contains)

    if unhitPositions.isEmpty then
      (playerBoard, Left("No positions left to attack"))
    else
      val weights = unhitPositions.map(pos =>
        positionWeighting.calculateWeight(pos, playerBoard.hits, PlayerBoard.size)
      )
      val maxWeight      = weights.max
      val bestPositions  = unhitPositions.zip(weights).filter(_._2 == maxWeight).map(_._1)
      val chosenPosition = bestPositions(Random.nextInt(bestPositions.length))
      ShipAttack.attack(playerBoard, chosenPosition)
```

La classe `MaxWeightStrategy` sfrutta una strategia di assegnazione pesi, la quale aderisce al trait `PositionWeighting`,
per classificare ciascuna cella non ancora colpita. Nel progetto attuale, il calcolo si basa sulla distanza dai colpi già
effettuati (`MinDistanceWeighting`). In caso di parità di punteggio tra più celle, il target viene scelto in modo
casuale tra quelle con il punteggio più elevato.

```scala
/** A position weighting strategy that calculates the weight based on the minimum distance to existing hits. */
class MinDistanceWeighting extends PositionWeighting:

  override def calculateWeight(pos: Position, hits: Set[Position], boardSize: Int): Int =
    if hits.isEmpty then
      Int.MaxValue
    else
      hits.map(pos.distanceTo).min
```

## Plancia di gioco

La plancia di gioco è uno degli elementi più importanti dello stato della partita. È stata implementata con la classe
`PlayerBoard`, che gestisce la posizione delle navi, le celle occupate e i colpi subiti. In particolare, la plancia
è modellata tramite un trait che ne definisce le operazioni principali, come il posizionamento delle navi e la
registrazione dei colpi, mentre l'effettiva implementazione è realizzata tramite un companion object, il quale funge da
*factory method* e sfrutta una *case class* privata per rappresentare lo stato della plancia.

Questo approccio garantisce la coerenza e l'integrità dello stato, poiché, in linea con i principi di programmazione
funzionale, la plancia è immutabile. Ciò significa che ogni operazione di modifica (come lo spostamento di una nave o la
registrazione di un colpo) non altera la plancia esistente, ma ne crea una nuova istanza con i campi aggiornati. Questo
approccio garantisce che lo stato del gioco sia prevedibile e coerente.

## Creazione della plancia

Un aspetto di grande importanza all'interno del progetto è stata la realizzazione di test quanto più comprensibili
possibili, in modo da aumentare la loro leggibilità. Per questo motivo, per la creazione della plancia di gioco è stato
aggiunto un Domain-Specific Language (DSL) che semplifica la definizione delle posizioni delle navi e delle celle
occupate.

Questo linguaggio minimale è stato implementato attraverso la classe `ShipPlacementDSL` ed è stato progettato per
poter coprire tutte le operazioni possibili per il posizionamento delle navi, includendo il tipo di imbarcazione, la
posizione di partenza e la direzione (orizzontale o verticale). Inoltre, il DSL consente di definire le coordinate in
modo alternativo, utilizzando una notazione "classica", composta da una lettera per la colonna e un numero per la riga,
come ad esempio `C(5)` per indicare la colonna C e la riga 5. In questo modo, la posizione `Position(2, 4)` può essere
rappresentata in modo più leggibile come `C(5)`.

```scala
object BoardCoordinates:

  /** A mapping of letters A through J to columns 0 through 9. */
  private val letterToColumn: Map[Char, Int] = ('A' to 'J').zipWithIndex.toMap

  /** Converts a letter and row number to a Position. */
  private object column:

    /** Creates a Position from a letter and row number.
      * @param letter the letter representing the column (A-J)
      * @param row the row number (1-10)
      * @return a Position corresponding to the letter and row
      * @throws IllegalArgumentException if the letter or row is invalid
      */
    def apply(letter: Char)(row: Int): Position =
      letterToColumn.get(letter.toUpper) match
        case Some(col) if row >= 1 && row <= 10 => Position(col, row - 1)
        case _ => throw new IllegalArgumentException(s"Invalid coordinate: ${letter.toUpper}$row")

  /** A trait for column objects to share the apply method. */
  private trait ColumnObject extends (Int => Position)

  /** Creates a column object for a given character. */
  private def createColumnObject(char: Char): ColumnObject =
    (row: Int) => column(char)(row)

  /** Helpers to define readable coordinates, e.g. C(5). */
  val A: Int => Position = createColumnObject('A')
  val B: Int => Position = createColumnObject('B')
  val C: Int => Position = createColumnObject('C')
  val D: Int => Position = createColumnObject('D')
  val E: Int => Position = createColumnObject('E')
  val F: Int => Position = createColumnObject('F')
  val G: Int => Position = createColumnObject('G')
  val H: Int => Position = createColumnObject('H')
  val I: Int => Position = createColumnObject('I')
  val J: Int => Position = createColumnObject('J')
```

Per la gestione delle coordinate, ho implementato un approccio che sfrutta dei *metodi factory* per ogni colonna (ad
esempio `val C`, `val D`, ecc.), i quali restituiscono elementi del tipo `Int => Position`. Questo, combinato con l'uso
del *currying* all'interno del metodo `apply` dell'oggetto `column`, consente di chiamare la funzione in due "passi":
sfruttando prima la lettera per individuare la colonna e poi il numero di riga come valore di input della funzione
restituita.

In questo modo, è stato possibile definire le posizioni delle navi all'interno della plancia con una sintassi simile
alla seguente:

```scala
board(
  place a Carrier at A(1) horizontal,
  place a Submarine at A(2) horizontal,
  place a Frigate at J(4) vertical
)
```

Inoltre, anche la gestione degli insiemi di celle (le posizioni sulla plancia) è stata semplificata, grazie all'uso di
un *companion object* della classe `Position`, che permette di definire intervalli di celle orizzontali o verticali,
tramite overloading del metodo `apply` della seconda. Ad esempio, è possibile definire un intervallo di celle
orizzontali con `Position(9, 6 to 9)`, che rappresenta le celle dalla colonna 6 alla colonna 9 nella riga 9.

Infine, combinando tutti gli aspetti descritti in questa sezione, è stato possibile scrivere i test in modo chiaro
e intuitivo. Ad esempio, di seguito viene riportato un test che verifica la corretta gestione del posizionamento
di una nave verticale:

```scala
it should "support placement of a vertical ship" in:
  board(
    place a Destroyer at B(2) vertical
  ).positions shouldEqual Position(1, 1 to 4)
```

## Contributi nell'interfaccia utente

Per quanto riguarda l'interfaccia utente, ho lavorato principalmente sulla schermata di configurazione della partita,
implementando la classe `SetupView`, che consente di selezionare la difficoltà e di configurare le navi da
utilizzare, gestendo le interazioni con l'utente in caso di errori tramite messaggi di errore.

# Testing
Come già accennato nella sezione precedente, i test sono stati fondamentali per garantire la qualità del codice e per
facilitare il processo di sviluppo, specialmente durante la rifattorizzazione, e sono stati scritti con particolare
attenzione alla leggibilità.

Di seguito viene riportato un breve esempio di test, per illustrare l'approccio utilizzato nella loro scrittura.

```scala
  it should "handle a placement at the board's edge (bottom-right)" in:
    board(place a Destroyer at J(7) vertical).positions shouldEqual Position(9, 6 to 9)

  it should "throw RuntimeException if ships overlap" in:
    a[RuntimeException] should be thrownBy board(
      place a Carrier at A(1) horizontal,
      place a Submarine at A(1) horizontal
    )

  it should "throw RuntimeException if a ship goes out of bounds" in:
    a[RuntimeException] should be thrownBy board(
      place a Carrier at J(1) horizontal
    )

  it should "throw RuntimeException with an invalid coordinate" in:
    a[RuntimeException] should be thrownBy board(
      place a Carrier at A(11) horizontal
    )
```