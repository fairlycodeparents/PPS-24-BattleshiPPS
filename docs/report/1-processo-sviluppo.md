---
title: Processo di sviluppo
nav_order: 1
parent: Report
---

# Processo di sviluppo adottato
Il gruppo ha adottato una metodologia Agile per lo sviluppo del progetto.

In particolare, la scelta è ricaduta sul metodo Scrum per la sua flessibilità e capacità di adattarsi alle esigenze del team
e del progetto, producendo ad ogni iterazione nuove funzionalità del sistema o miglioramenti a quelle esistenti.

Per la coordinazione del team e la gestione del progetto sono stati utlizzati:
- **Notion**: per la gestione delle attività e la pianificazione dei task nelle iterazioni
- **GitHub**: per la collaborazione tra i membri, il versionamento del codice e la gestione delle pull request al termine di ogni sprint
- **Discord**: per le comunicazioni e le call tra i membri del team
- **IntelliJ IDEA**: come ambiente di sviluppo integrato (IDE) per la scrittura del codice

## Modalità di divisione in itinere dei task
La suddivisione dei task è stata gestita in modo collaborativo durante le riunioni di pianificazione degli sprint, 
per permettere a tutti i membri del team di contribuire alla definizione delle attività da svolgere. 

Nel primo incontro (_Sprint Planning_) sono stati individuati i task principali del progetto e sono state assegnate delle priorità 
in base alla rilevanza delle funzionalità, andando così a redigere il _Product Backlog_.
In questa fase è stata inoltre stabilita la *Definition of done* secondo cui una funzionalità può considerarsi 
completata quando:
-   è stata implementata e testata con esito positivo
-   rispetta quanto richiesto dall'utente

Nelle successive riunioni di pianificazione degli sprint, i task sono stati ulteriormente suddivisi in attività 
più piccole (_Sprint Backlog_) per permettere un'equa distribuzione del lavoro tra i membri del team e semplificare la gestione 
operativa delle attività.
Al termine di ogni riunione vengono inoltre redatti una _Sprint Review_ e una _Sprint Retrospective_, per valutare,
rispettivamente, sia il progresso a livello di funzionalità e soddisfazione del cliente, sia il processo di sviluppo,
individuando possibili aree di miglioramento.

## Meeting/iterazioni pianificate
In una prima fase di analisi e modellazione, il gruppo ha partecipato a un meeting iniziale con l’obiettivo 
di definire l’architettura del progetto. 
In quella stessa sede sono stati inoltre stabiliti la durata degli sprint e le modalità delle successive iterazioni.

Il team ha deciso di adottare sprint settimanali, a eccezione del primo,
della durata di due settimane per via delle configurazioni iniziali del progetto, e dell'ultimo, influenzato
dalle festività e prolungato, quindi, fino alla data di consegna.

La decisione di organizzare sprint brevi è stata motivata dall’esigenza di sviluppare funzionalità in tempi 
brevi e ottenere un feedback rapido dallo stakeholder.

Oltre alle riunioni settimanali di pianificazione, il team ha previsto un breve confronto quotidiano (_Daily Scrum_) 
per discutere dello stato di avanzamento e affrontare eventuali criticità o problemi.

## Modalità di revisione in itinere dei task
Per la revisione dei task è stato adottato un meccanismo basato sulle pull request.
Durante lo sprint, ogni _feature_ è stata implementata in un branch separato e poi integrata nel branch _develop_, 
permettendo al team di lavorare in parallelo senza interrompere il flusso principale. 

Al termine di ogni sprint, le modifiche consolidate in _develop_ sono state unite nel branch _main_ tramite pull request:
per avere successo, ogni pull request ha dovuto ottenere l’approvazione di tutti i membri del gruppo.
Questo approccio ha permesso sia l’aggiornamento costante del gruppo sulle attività in corso,
sia un ulteriore livello di controllo e validazione del codice prima della sua integrazione definitiva.

## Scelta degli strumenti di test/build/continuous integration
Per il testing si è scelto di utilizzare `ScalaTest` come strumento di automazione, essendo una tecnologia nota e facile
da integrare. Come build tool è stato scelto `sbt`, in quanto nasce specificatamente per Scala.
Sono stati usati plugin dedicati come _scalafmtAll_ che integrano perfettamente strumenti di formattazione e refactoring automatico.
L’intero progetto (inclusa la relazione) è stato gestito tramite GitHub.
