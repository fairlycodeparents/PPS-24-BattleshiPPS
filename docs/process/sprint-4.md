---
title: Sprint 4
nav_order: 4
parent: Processo di Sviluppo
---

# Sprint 4

## Obiettivo
L'obiettivo del quarto sprint è quello di sistemare complessivamente il gioco 
e introdurre funzionalità aggiuntive quali l'intelligenza del bot ed eventualmente la presenza di ostacoli nella mappa.

## Deadline
La scadenza dello sprint è il 22/08/2025.

## Backlog
<style>
table {
    border-collapse: collapse;
    width: 100%;
}
table th, table td {
    border: 1px solid #ddd;
}
table td[rowspan] {
    vertical-align: middle;
}
</style>
<table>
  <thead>
    <tr>
      <th>Priorità</th>
      <th>Nome</th>
      <th>Descrizione</th>
      <th>Sprint Task</th>
      <th>Volontario</th>
      <th>Stima Sprint 1</th>
      <th>Stima Sprint 2</th>
      <th>Stima Sprint 3</th>
      <th>Stima Sprint 4</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="3">1</td>
      <td rowspan="3">Organizzazione progetto</td>
      <td rowspan="3">Il progetto dovrà essere impostato per adattarsi alla metodologia agile SCRUM. 
            Gestito tramite un approccio Git Flow.</td>
      <td>Git Flow Setup</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Studio dell'architettura</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Docs Setup</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td></td>
      <td>Mockup</td>
      <td>Come utente, vorrei avere una prima visualizzazione del layout dell’applicazione</td>
      <td></td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="6">3</td>
      <td rowspan="6">Posizionamento navi</td>
      <td rowspan="6">Come utente, vorrei poter posizionare le navi nella mappa</td>
    </tr>
    <tr>
      <td>Creazione delle navi</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Realizzazione della GUI</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Logica spostamento navi (selezione nave da spostare, 
        muovere la nave nella nuova posizione, 
        ruotare la nave)</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Posizionamento iniziale</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Refactor player board</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="3">4</td>
      <td rowspan="3">Posizionamento random</td>
      <td rowspan="3">Come utente, vorrei la possibilità di schierare in modo automatico le mie navi</td>
    </tr>
    <tr>
      <td>Posizionamento iniziale automatico</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Posizionamento randomico ripetuto</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="3">5</td>
      <td rowspan="3">Testing avanzato</td>
      <td rowspan="3">Il testing dovrà includere un DSL che permette di controllare 
        in modo chiaro ed efficace il corretto funzionamento del progetto</td>
    </tr>
    <tr>
      <td>Creazione DSL</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Estensione DSL per l'attacco</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="6">6</td>
      <td rowspan="6">Fase d'attacco</td>
      <td rowspan="6">Come utente, vorrei poter scegliere una casella dell’avversario da colpire</td>
    </tr>
    <tr>
      <td>Logica di attacco e fine partita</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Feedback all'utente per il risultato dell'attacco</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Estensione della board per supportare la logica di attacco</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Refactor del game controller</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Miglioramento usabilità GUI</td>
      <td>Dilaver, Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="4">7</td>
      <td rowspan="4">Modalità multiplayer</td>
      <td rowspan="4">Come utente, vorrei poter giocare
            contro un altro giocatore sul mio dispositivo</td>
    </tr>
    <tr>
      <td>Logica del player umano</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Gestione dei turni e del loop di gioco</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Refactor del Game Controller</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="3">8</td>
      <td rowspan="3">Modalità vs bot</td>
      <td rowspan="3">Come utente, vorrei poter giocare contro un bot nel mio dispositivo</td>
    </tr>
    <tr>
      <td>Logica del player bot (random)</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Gestione dei turni e del loop di gioco</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>9</td>
      <td>Impostazioni della partita</td>
      <td>Come utente, vorrei poter scegliere alcune regole di gioco come il numero di barche schierabili 
            e il tipo di partita (multiplayer o bot)</td>
      <td>Logica e GUI delle impostazioni di gioco</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="3">10</td>
      <td rowspan="3">Scelta della difficoltà del bot</td>
      <td rowspan="3">Come utente, vorrei avere la possibilità di scegliere la difficoltà di gioco</td>
    </tr>
    <tr>
      <td>Intelligenza del bot media</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Intelligenza del bot avanzata</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="4">11</td>
      <td rowspan="4">Ostacoli</td>
      <td rowspan="4">Come utente, vorrei una mappa diversa, 
        con ostacoli che impediscono il posizionamento delle navi</td>
    </tr>
    <tr>
      <td>Creazione ostacoli</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Posizionamento ostacoli</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Aggiunta ostacoli nella configurazione (settings)</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>12</td>
      <td>Colpi speciali</td>
      <td>Come utente vorrei poter utilizzare colpi speciali, 
            come colpi ad area attivabili una volta per partita</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
  </tbody>
</table>

## Sprint Review
Al termine del quarto e ultimo sprint, lo stakeholder ha ottenuto la versione finale del gioco, nella quale è
stata di fatto realizzata una delle funzionalità opzionali: la possibilità di giocare contro un bot a diversi
livelli di difficoltà.
Non è stato invece possibile realizzare l’aggiunta di ostacoli nella mappa, 
prevista inizialmente tra i possibili obiettivi dello sprint.

Sono stati comunque introdotti miglioramenti dal punto di vista dell’usabilità, 
come suggerito al termine del terzo sprint. Lo stakeholder si ritiene, quindi, complessivamente 
soddisfatto del risultato finale.

## Sprint Retrospective
A differenza degli altri sprint, questa volta la durata è stata influenzata dalla settimana di Ferragosto, 
durante la quale si sono verificati giorni di inattività. 
Di conseguenza, la consueta durata di una settimana è stata estesa fino a coincidere con la scadenza del progetto.

Gli obiettivi dello sprint sono stati parzialmente raggiunti: l’unica funzionalità non completata è stata l’implementazione degli ostacoli.
