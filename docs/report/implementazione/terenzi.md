---
title: Mirco Terenzi
nav_order: 3
parent: Implementazione
---

# Implementazione - Terenzi Mirco

## Panoramica dei Contributi
Il mio contributo al progetto è stato realizzato seguendo un approccio TDD-like, utilizzando i test per guidare lo 
sviluppo delle funzionalità e mantenerle coerenti in caso di modifiche al codice. In particolare, ho mi sono concentrato 
principalmente sulle seguenti funzionalità e aree:

* **Configurazione della plancia di gioco**: `PlayerBoard`, `PlayerBoardBuilder`, `BoardFactory`, `BoardCoordinates`, 
`ShipPlacementDSL`.
* **Validazione della configurazione scelta dall'utente**: `GameConfig`, `ConfigurationValidator`, 
`MaxOccupancyValidator`, `NotEmptyValidator`, `ConfigurationManager`.
* **Interfaccia grafica e interazione con l'utente**: `SetupView`, `DifficultySelection`, `GameSetup`.
* **Strategie d'attacco del bot**: `AdvancedBotAttackStrategy`, `PositionWeighting`, `MinDistanceWeighting`,
  `MaxWeightStrategy` e, insieme a Giangiulli Chiara, `TargetAlreadyHitStrategy`
* **Classi di supporto**: `Position`.

## Gestione della configurazione
Per poter iniziare una partita, è necessario che la configurazione scelta dall'utente rispetti determinate regole e
condizioni, come garantire un numero minimo di navi e, al tempo stesso, evitare che la plancia sia troppo piena,
limitando la libertà di posizionamento delle navi e rendendo il gioco meno interessante.

Per questo motivo, è stato definita una classe `ConfigurationValidator` che si occupa di validare la configurazione
scelta dall'utente, assicurandosi che rispetti le regole del gioco. Ciascuna configurazione è rappresentata da una
classe `GameConfig`, che contiene le informazioni necessarie per la configurazione della plancia di gioco.

Un esempio di validazione della configurazione è rappresentato dal `MaxOccupancyValidator`, che verifica che il numero 
di celle occupate dalle navi non superi un certo limite, garantendo che la plancia non sia troppo piena.
```scala
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
Quando il validatore rileva che la configurazione è valida la restituisce, altrimenti genera una nuova istanza di
`GameConfig` riducendo il numero di navi in modo da rispettare le regole del gioco. In particolare, le navi sono ordinate
per lunghezza decrescente e il validatore cerca di mantenere il numero di navi più lungo possibile, riducendo
prima le navi più corte.

In modo analogo, il `NotEmptyValidator` si occupa di verificare che la configurazione contenga almeno una nave,
restituendo la configurazione originale se valida. Infine, il `ConfigurationManager` si occupa di orchestrare
l'applicazione di tutti i validatori, applicandoli in sequenza e restituendo una configurazione finale che rispetta
tutte le regole del gioco.

## Creazione della plancia
La plancia di gioco è uno degli elementi più importanti relativi allo stato della partita, che deve garantire una
corretta gestione delle navi posizionate, delle celle occupate e dei colpi inferti. Questo elemento è stato implementato 
con `PlayerBoard`, che rappresenta la plancia di un giocatore e contiene le navi posizionate e le celle occupate.

Durante l'utilizzo dell'applicativo, l'utente può scegliere la configurazione della plancia di gioco utilizzando
l'interfaccia grafica, che consente di selezionare il numero di navi e le loro posizioni. Per quanto riguarda i test,
per renderli più semplici e leggibili, è stato implementato un Domain-Specific Language (DSL) che permettesse di
definire le posizioni delle navi in modo chiaro e intuitivo.

Il `ShipPlacementDSL` utilizza un approccio fluido, permettendo di concatenare le operazioni di posizionamento delle
navi e di definire le celle occupate in modo chiaro e intuitivo. Ad esempio, è possibile aggiungere una nave con una
sintassi simile a:
```scala
  place a Cruiser at C(5) horizontal
```
Inoltre, il DSL semplifica anche la definizione delle coordinate delle celle, utilizzando la classe `BoardCoordinates`,
che consente di descrivere le posizioni delle navi in modo "classico", specificando le cordinate delle celle tramite
una lettera e un numero, come ad esempio `C(5)` per indicare la colonna C e la riga 5.

Infine, anche la gestione degli insiemi di celle è stata semplificata tramite il metodo apply della classe `Position`, 
che permette di definire le posizioni come _range_ di celle orizzontali o verticali, ad esempio `Position(9, 6 to 9)`.
In questo modo, combinando le funzionalità del DSL con le classi di supporto come `Position`, è stato possibile scriver
i test in modo chiaro e intuitivo, garantendo una buona copertura e una facile comprensione del codice.
```scala
it should "handle a placement at the board's edge (bottom-right)" in:
    board(place a Destroyer at J(7) vertical).positions shouldEqual Position(9, 6 to 9)
```

## Strategie d'attacco del bot
Tra i requisiti del progetto, nel caso di una partita singleplayer (contro il bot), vi è la possibilità di scegliere
tra diverse difficoltà. Queste sono state implementate attraverso l'utilizzo di diverse strategie di attacco del bot,
che determinano il suo comportamento durante la partita. Questa specifica porzione di codice è stata in parte implementata
in pair programming con Giangiulli Chiara. In particolare, Giangiulli si è occupata della strategia intermedia, mentre 
io mi sono concentrato sulla difficoltà avanzata.

Inizialmente le strategie sono state implementate come classi separate, basate sul trait `AttackStrategy`, e sono state
definite con le seguenti caratteristiche:
* **Semplice**: il bot attacca in modo casuale, scegliendo una cella a caso dalla plancia.
* **Intermedia**: il bot attacca in modo casuale, ma sfrutta la conoscenza dei colpi andati a buon fine, colpendo le celle 
  adiacenti in modo da affondare le navi.
* **Avanzata**: il bot utilizza una strategia più complessa, che combina la conoscenza dei colpi andati a buon fine,
  descritta nel punto precedente, con un sistema di punteggio per determinare la cella migliore da attaccare. Questa
  strategia si basa sul cercare di colpire in modo uniforme le celle della plancia.

In seguito, considerando che le strategie intermedia e avanzata condividono una logica comune, ho rifattorizzato il 
codice per utilizzare un approccio basato su mixin e composizione delle strategie. Ciò ha permesso di creare un sistema 
modulare, facilitando future estensioni e rendendo il codice più chiaro e manutenibile. In particolare, ho realizzato
`AdvancedBotAttackStrategy` e `TargetAlreadyHitStrategy` e adattato la `AverageBotAttackStrategy` realizzata da 
Giangiulli, per aderire al pattern decorator realizzato con il mixin. In questo modo, le strategie possono essere 
combinate o sostituite senza modificare il codice esistente e permettono il riutilizzo di logica comune, permettendo di 
definire le strategia nel seguente modo:
```scala
class AdvancedBotAttackStrategy
  extends MaxWeightStrategy(MinDistanceWeighting())
  with TargetAlreadyHitStrategy
```

La `MaxWeightStrategy` si occupa di distribuire gli attacchi in modo uniforme su tutta la plancia,
garantendo una copertura equilibrata. Dopo una prima implementazione, la classe è stata rifattorizzata per utilizzare
una logica di assegnazione del peso variabile, passata come parametro al costruttore. Questa operazione è stata
notevolmente semplificata dalla classe di test realizzata durante la prima implementazione, che ha permesso di
verificare in ogni momento il corretto funzionamento della strategia, facilitando la transizione verso un approccio
più modulare e flessibile.
```scala
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

    if unhitPositions.isEmpty then (playerBoard, Left("No positions left to attack"))
    else
      val weights = unhitPositions.map(pos =>
        positionWeighting.calculateWeight(pos, playerBoard.hits, PlayerBoard.size)
      )
      val maxWeight      = weights.max
      val bestPositions  = unhitPositions.zip(weights).filter(_._2 == maxWeight).map(_._1)
      val chosenPosition = bestPositions(Random.nextInt(bestPositions.length))
      ShipAttack.attack(playerBoard, chosenPosition)
```
La classe sfrutta la `PositionWeighting` per calcolare un punteggio per ogni cella non colpita, in questo caso in base
alla disposizione dei colpi sulla plancia. L'assegnamento del punteggio si basa sul calcolare la distanza minima 
rispetto ai colpi già sferrati. Infine, la cella _target_ viene scelta casualmente tra quelle con il punteggio massimo.
