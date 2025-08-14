---
title: Mirco Terenzi
nav_order: 3
parent: Implementazione
---

# Implementazione - Terenzi Mirco

## Panoramica dei Contributi
Il mio lavoro si è concentrato sullo sviluppo, cercando di seguire un approccio TDD-like, sulle seguenti aree e 
funzionalità:

* **Configurazione Plancia**: `PlayerBoard`, `PlayerBoardBuilder`, `BoardFactory`, `BoardCoordinates`, `ShipPlacementDSL`.
* **Validazione della Configurazione**: `GameConfig`, `ConfigurationValidator`, `MaxOccupancyValidator`,
`NotEmptyValidator`, `ConfigurationManager`.
* **GUI e Interazione Utente**: `SetupView`, `DifficultySelection`, `GameSetup`.
* **Strategie di Attacco del Bot**: `AdvancedBotAttackStrategy`, `PositionWeighting`, `MaxMinPositionWeighting`, 
 `UniformDistributionStrategy` e, insieme a Giangiulli Chiara, `TargetAlreadyHitStrategy` 
* **Classi di Supporto**: `Placement`.

## Gestione della Configurazione del Gioco
La configurazione del gioco si occupa di definire le regole e le condizioni da rispettare per poter iniziare una partita.
Queste includono l'analisi di caratteristiche come il numero di navi e le loro dimensioni. Per garantire che la 
configurazione sia valida e coerente, ho implementato un sistema di validazione che segue i principi di **immutabilità** 
e utilizza il **Design Pattern Strategy**.

La configurazione del gioco è rappresentata dalla `case class GameConfig`, che è immutabile per garantire che le modifiche
avvengano in modo controllato e prevedibile. Le modifiche alla configurazione generano una nuova istanza di `GameConfig`, 
evitando modifiche accidentali allo stato esistente.
Per validare e correggere la configurazione, ho creato un insieme di strategie, rappresentate dal trait `ConfigurationValidator`.
Le implementazioni concrete, come `MaxOccupancyValidator` e `NotEmptyValidator`, definiscono regole specifiche, come il 
numero massimo di celle occupate dalle navi e la presenza di almeno una nave. Questo approccio modulare consente di aggiungere 
facilmente nuove regole senza modificare il codice esistente.
La gestione di queste regole è orchestrata da `ConfigurationManager`, un oggetto che applica sequenzialmente tutti i validatori
a una configurazione specifica, restituendo un risultato finale che rispetta tutte le condizioni.

## Creazione della Plancia
La creazione della plancia di gioco è un aspetto cruciale per garantire un'esperienza di gioco coerente e valida. Ho 
implementato un sistema che consente di costruire la plancia in modo flessibile e sicuro, utilizzando il **Design 
Pattern Builder**.
La plancia di gioco è rappresentata dalla classe `PlayerBoard`, che contiene le navi posizionate e le celle occupate.
Per costruire una plancia, ho creato il `PlayerBoardBuilder`, che consente di aggiungere navi e celle in modo sequenziale, 
garantendo che ogni modifica rispetti le regole di gioco definite nella configurazione.
Il builder utilizza un approccio fluido, permettendo di concatenare le operazioni di posizionamento delle navi e di 
definire le celle occupate in modo chiaro e intuitivo. Ad esempio, è possibile aggiungere una nave con una sintassi simile a:
```scala
place a Cruiser at C(5) horizontal
```
Questa sintassi è resa possibile grazie all'uso di un **Domain-Specific Language (DSL)**, che semplifica la 
definizione dei posizionamenti delle navi e rende il codice più leggibile e meno soggetto a errori. Il DSL è implementato 
all'interno di `ShipPlacementDSL` e `BoardCoordinates`, consentendo di descrivere le posizioni delle navi in modo
declarativo e intuitivo.

## Strategia di Attacco del Bot con Mixin
La parte di strategia è stata implementata parzialmente in pair programming con Giangiulli Chiara. In particolare, io mi
sono occupato della parte di strategia di attacco del bot, utilizzando un approccio basato su **mixin** e **composizione**,
mentre Giangiulli ha lavorato sul pattern strategy.
Le strategie di attacco del bot sono state progettate per essere modulari e facilmente estendibili. Ho implementato
diverse strategie di attacco, come `AdvancedBotAttackStrategy`, `PositionWeighting`, `MaxMinPositionWeighting` e 
`UniformDistributionStrategy`, che possono essere combinate o sostituite senza modificare il codice esistente.
Queste strategie sono state progettate per essere indipendenti e riutilizzabili, consentendo di creare un sistema di 
attacco del bot flessibile e scalabile. Ad esempio, `PositionWeighting` calcola un punteggio per ogni cella in base alla
probabilità di colpire una nave, mentre `MaxMinPositionWeighting` utilizza un approccio di massimizzazione e 
minimizzazione per determinare la cella migliore da attaccare. La strategia `UniformDistributionStrategy` distribuisce 
gli attacchi in modo uniforme su tutta la plancia, garantendo una copertura equilibrata.

## Gestione della Plancia
La plancia di gioco è gestita attraverso la classe `PlayerBoard`, che rappresenta la plancia di un giocatore.
Questa classe contiene le navi posizionate e le celle occupate, e fornisce metodi per interagire con la plancia, 
come l'aggiunta di navi, la verifica delle celle occupate e l'attacco a una cella specifica.
La classe `Placement` è utilizzata per rappresentare le posizioni delle navi sulla plancia, fornendo un modo per
definire le coordinate delle celle e le direzioni in cui le navi sono posizionate. Questa classe è 
essenziale per garantire che le navi siano posizionate correttamente e che le regole del gioco siano rispettate.
La gestione della plancia include anche la validazione delle posizioni delle navi e il controllo delle celle occupate, 
per garantire che le navi siano posizionate in modo valido e che non ci siano sovrapposizioni tra di esse.
Questo è realizzato attraverso l'uso di metodi specifici nella classe `PlayerBoard`, che verificano le condizioni 
necessarie per una configurazione valida della plancia.