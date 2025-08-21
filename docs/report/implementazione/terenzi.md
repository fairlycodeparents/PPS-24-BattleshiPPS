---
title: Mirco Terenzi
nav_order: 3
parent: Implementazione
---

# Implementazione - Terenzi Mirco

## Panoramica dei Contributi

Il mio contributo al progetto è stato realizzato adottando un approccio ispirato al TDD, utilizzando i test per guidare
lo sviluppo delle funzionalità e assicurare la coerenza del codice in caso di modifiche. Tuttavia, per motivi di tempo,
questa metodologia è stata applicata alle sole classi principali e non a tutte le parti implementate.

In particolare, mi sono concentrato sulle seguenti aree:

* **Configurazione della plancia di gioco**: `PlayerBoard`, `PlayerBoardBuilder`, `BoardFactory`, `BoardCoordinates`,
`ShipPlacementDSL`.
* **Validazione della configurazione scelta dall'utente**: `GameConfig`, `ConfigurationValidator`,
`MaxOccupancyValidator`, `NotEmptyValidator`, `ConfigurationManager`.
* **Interfaccia grafica e interazione con l'utente**: `SetupView`, `DifficultySelection`, `GameSetup`.
* **Strategie d'attacco del bot**: `AdvancedBotAttackStrategy`, `PositionWeighting`, `MinDistanceWeighting`,
`MaxWeightStrategy` e, in collaborazione con Giangiulli Chiara, `TargetAlreadyHitStrategy`.
* **Classi di supporto**: `Position`.

## Gestione della configurazione

Per avviare una partita, la configurazione scelta dall'utente deve rispettare determinate regole, come garantire un
numero minimo di navi e, al tempo stesso, evitare che la plancia sia troppo affollata, il che limiterebbe la libertà di
posizionamento delle navi e renderebbe il gioco meno interessante.

Per questo motivo, è stato definito il trait `ConfigurationValidator`, che si occupa di validare la configurazione di
gioco. Ogni configurazione è rappresentata da una classe `GameConfig`, che incapsula le informazioni sulle navi e le
loro quantità.

```scala 3
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
Un esempio di validazione è rappresentato da `MaxOccupancyValidator`, che verifica che il numero di celle occupate dalle
navi non superi un certo limite, garantendo che la plancia non sia eccessivamente piena. Quando la configurazione è
valida, il validatore la restituisce; altrimenti, genera una nuova istanza di `GameConfig` riducendo il numero di navi
in modo da rispettare le regole. In particolare, le navi sono ordinate per lunghezza decrescente e il validatore cerca
di mantenere il numero di navi più lungo possibile, riducendo prima quelle più corte per ottimizzare lo spazio.

Analogamente, il `NotEmptyValidator` verifica che la configurazione includa almeno una nave; se necessario, la corregge
aggiungendo una nave di tipo *Frigate*.

Infine, il `ConfigurationManager` si occupa di applicare tutti i validatori in sequenza, restituendo una configurazione
finale conforme alle regole del gioco.

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

Successivamente, notando che le strategie intermedia e avanzata condividono una logica comune, ho rifattorizzato il
codice per utilizzare un approccio basato su *mixin* e composizione delle strategie. Ciò ha permesso di creare un
sistema modulare, facilitando future estensioni e rendendo il codice più chiaro e manutenibile.

In particolare, ho realizzato `AdvancedBotAttackStrategy` e `TargetAlreadyHitStrategy`, e ho adattato la
`AverageBotAttackStrategy` di Giangiulli, per allinearsi al mixin. In questo modo, le strategie possono essere
combinate senza modificare il codice esistente e la logica comune può essere riutilizzata. Ad esempio la strategia
avanzata viene definita come mostrato di seguito:

```scala 3
class AdvancedBotAttackStrategy
  extends MaxWeightStrategy(MinDistanceWeighting())
  with TargetAlreadyHitStrategy
```

Inizialmente, ho implementato una classe `UniformDistributionStrategy` che si occupasse di selezionare la miglior cella
da colpire come è stato precedentemente descritto, distribuendo i colpi sulla mappa in modo da selezionare la cella in
modo da massimizzare la distanza minima dai colpi già sferrati. Successivamente, la strategia è stata sostituita da una
`MaxWeightStrategy`, la quale si occupa solamente di scegliere la cella con il peso (punteggio) maggiore. Questa
modifica ha permesso di delegare l'assegnazione dei pesi a una classe esterna, passata come parametro al costruttore,
facilitando eventuali estensioni del codice o modifiche. In particolare, questo processo è stato notevolemnte
semplificato dai test esistenti, che hanno permesso di verificare il corretto funzionamento dell' implementazione in
ogni momemnto. 

```scala 3
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

La classe `MaxWeightStrategy` sfrutta positionWeighting, il quale aderisce al trait `PositionWeighting`, per assegnare
un punteggio a ciascuna cella non ancora colpita. Nell'implementazione, il calcolo si basa sulla distanza dai colpi già
effettuati (`MinDistanceWeighting`). In caso di parità di punteggio tra più celle, il target viene scelto in modo
casuale tra quelle con il punteggio più elevato.

```scala 3
class MinDistanceWeighting extends PositionWeighting:

  override def calculateWeight(pos: Position, hits: Set[Position], boardSize: Int): Int =
    if hits.isEmpty then
      Int.MaxValue
    else
      hits.map(pos.distanceTo).min
```
