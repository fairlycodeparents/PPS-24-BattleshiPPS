---
title: Processo di sviluppo
nav_order: 1
parent: Report
---

# Processo di sviluppo adottato
Processo di sviluppo adottato 
Il gruppo ha adottato una metodologia Agile per lo sviluppo del progetto. 
La scelta è ricaduta sul metodo Scrum per la sua flessibilità e capacità di adattarsi alle esigenze del team
e del progetto, producendo a ogni iterazione nuove funzionalità al sistema o miglioramenti a quelle esistenti.
Per la coordinazione del team e la gestione del progetto sono stato utlizzati:
- **Notion**: per la gestione delle attività e la pianificazione delle iterazioni
- **GitHub**: per il versionamento del codice e la gestione delle pull request a ogni fine sprint
- **Discord**: per la comunicazione via chiamata tra i membri del team
- **IntelliJ IDEA**: come ambiente di sviluppo integrato (IDE) per la scrittura del codice

## Modalità di divisione in itinere dei task
La suddivisione dei task è stata effettuata in modo collaborativo durante le riunioni di pianificazione degli sprint,
per permettere a tutti i membri del team di contribuire alla definizione delle attività da svolgere. Al primo incontro
sono stati definiti i task principali del progetto assegnandoli le priorità in base all'importanza delle
funzionalità. Quindi, durante le riunioni di pianificazione degli sprint, i task sono stati suddivisi in attività
più piccole per permettere una equa distribuzione del lavoro tra i membri del team e per facilitare la gestione
delle attività.
La nostra *Definition of done* prevede che una funzionalità sia considerata completata quando:
-   è stata implementata e testata con esito positivo
-   rispetta quanto richiesto dall'utente

## Meeting/interazioni pianificate
In una prima fase di analisi e modellazione il gruppo ha cooperato in un meeting iniziale con lo scopo di definire
l’architettura del progetto. Inoltre, nella stessa sede, sono state programmate la durata degli sprint e le successive
interazioni. Il team ha scelto di organizzare le iterazioni sulla base di sprint settimanali, a eccezione della prima,
della durata di due settimane per via delle configurazioni iniziali del progetto, e dell'ultima, influenzata
dalle festività e quindi il seguente prolungamento fino alla data di consegna. La scelta di sprint settimanali 
è stata fatta per garantire lo sviluppo di funzionalità in tempi brevi ottenere un feedback rapido da parte
dello stakeholder. Inoltre, il team, oltre agli incontri settimanali di pianificazione, prevedeva un incontro quotidiano 
per discutere dello stato di avanzamento del progetto e risolvere eventuali problemi.

## Modalità di revisione in itinere dei task
Per la revisione dei task si è optato per l’introduzione di un meccanismo a pull request. Nello specifico ogni feature
è stata implementata in un branch separato da *main* dove, a ogni sprint ultimato, è stata integrata tramite una pull
request; per avere successo, tale richiesta doveva ottenere l’approvazione di tutti i membri del gruppo. Lo scopo di
questa metodologia è quello di permettere a tutto il team di rimanere aggiornato, qual’ora la definizione di uno sprint
preveda molto lavoro parallelo, oltre che eventualmente aggiungere un controllo addizionale.

## Scelta degli strumenti di test/build/continuous integration
Per il testing si è scelto di utilizzare scalatest come strumento di automazione, essendo una tecnologia nota e facile
da integrare. Come build tool è stato scelto sbt, il quale nasce specificatamente per Scala. Vengono infatti usati
plugin dedicati come *scalafmtAll* che integrano perfettamente strumenti di formattazione e refactoring automatico.
L’intera relazione è stata gestita tramite GitHub.
