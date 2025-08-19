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
* **Strategie d'attacco del bot**: `AdvancedBotAttackStrategy`, `PositionWeighting`, `MaxMinPositionWeighting`,
  `UniformDistributionStrategy` e, insieme a Giangiulli Chiara, `TargetAlreadyHitStrategy`
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
per renderli più semplici e leggibili, è stato implementato un **Domain-Specific Language (DSL)** che permettesse di
definire le posizioni delle navi in modo chiaro e intuitivo.

Il `ShipPlacementDSL` utilizza un approccio fluido, permettendo di concatenare le operazioni di posizionamento delle
navi e di definire le celle occupate in modo chiaro e intuitivo. Ad esempio, tramite `ShipPlacementDSL` è possibile
aggiungere una nave con una sintassi simile a:
```scala
  place a Cruiser at C(5) horizontal
```
Inoltre, il DSL semplifica anche la definizione delle coordinate delle celle, utilizzando la classe `BoardCoordinates`,
che consente di descrivere le posizioni delle navi in modo "classico", specificando le cordinate delle celle tramite
una lettera e un numero, come ad esempio `C(5)` per indicare la colonna C e la riga 5.

Infine, anche la gestione degli insiemi di celle è stata semplificata grazie all'uso di un DSL, che consente di definire
le posizioni come _range_ di celle orizzontali o verticali, ad esempio `Position(9, 6 to 9)`. In questo modo, combinando
le funzionalità del DSL con le classi di supporto come `Position`, è stato possibile scrivere i test in modo chiaro e
intuitivo, garantendo una buona copertura e una facile comprensione del codice.
```scala
it should "handle a placement at the board's edge (bottom-right)" in:
    board(place a Destroyer at J(7) vertical).positions shouldEqual Position(9, 6 to 9)
```
