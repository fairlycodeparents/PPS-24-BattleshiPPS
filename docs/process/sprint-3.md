---
title: Sprint 3
nav_order: 3
parent: Processo di Sviluppo
---

# Sprint 3

## Obiettivo
L'obiettivo del terzo sprint è fornire all'utente la possibilità di giocare contro un altro utente sullo stesso dispositivo,
a turno, o contro un semplice bot che attacca in modo random. Inoltre, dovrà poter visulizzare e scegliere alcune impostazioni di gioco come, 
appunto, la modalità (multiplayer o vs bot) e il numero di navi da posizionare.

## Deadline
La scadenza dello sprint è il 04/08/2025.

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
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Studio dell'architettura</td>
      <td>Chiara</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Docs Setup</td>
      <td>Mirco</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td></td>
      <td>Mockup</td>
      <td>Come utente, vorrei avere una prima visualizzazione del layout dell’applicazione</td>
      <td></td>
      <td>Mirco</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td rowspan="6">3</td>
      <td rowspan="6">Posizionamento navi</td>
      <td rowspan="6">Come utente, vorrei poter posizionare le navi nella mappa</td>
    </tr>
    <tr>
      <td>Creazione delle navi</td>
      <td>Chiara</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Realizzazione della GUI</td>
      <td>Chiara</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Logica spostamento navi (selezione nave da spostare, 
        muovere la nave nella nuova posizione, 
        ruotare la nave)</td>
      <td>Dilaver</td>
      <td>4</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Posizionamento iniziale</td>
      <td>Dilaver</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Refactor player board</td>
      <td>Mirco</td>
      <td>4</td>
      <td>4</td>
      <td>2</td>
    </tr>
    <tr>
      <td rowspan="3">4</td>
      <td rowspan="3">Posizionamento random</td>
      <td rowspan="3">Come utente, vorrei la possibilità di schierare in modo automatico le mie navi</td>
    </tr>
    <tr>
      <td>Posizionamento iniziale automatico</td>
      <td>Dilaver</td>
      <td>1</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Posizionamento randomico ripetuto</td>
      <td>Dilaver</td>
      <td>3</td>
      <td>0</td>
      <td>0</td>
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
      <td>4</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
    </tr>
    <tr>
      <td rowspan="5">6</td>
      <td rowspan="5">Fase d'attacco</td>
      <td rowspan="5">Come utente, vorrei poter scegliere una casella dell’avversario da colpire</td>
    </tr>
    <tr>
      <td>Logica di attacco e fine partita</td>
      <td>Chiara</td>
      <td>15</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Feedback all'utente per il risultato dell'attacco</td>
      <td>Dilaver</td>
      <td>10</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Estensione della board per supportare la logica di attacco</td>
      <td>Mirco</td>
      <td>9</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Refactor del game controller</td>
      <td>Dilaver</td>
      <td>5</td>
      <td>5</td>
      <td>3 </td>
    </tr>
    <tr>
      <td rowspan="3">7</td>
      <td rowspan="3">Modalità multiplayer</td>
      <td rowspan="3">Come utente, vorrei poter giocare
            contro un altro giocatore sul mio dispositivo</td>
    </tr>
    <tr>
      <td>Logica del player umano</td>
      <td>Chiara</td>
      <td>7</td>
      <td>7</td>
      <td>7</td>
    </tr>
     <tr>
      <td>Gestione dei turni e del loop di gioco</td>
      <td>Dilaver</td>
      <td>10</td>
      <td>10</td>
      <td>2</td>
    </tr>
    <tr>
      <td rowspan="3">8</td>
      <td rowspan="3">Modalità vs bot</td>
      <td rowspan="3">Come utente, vorrei poter giocare contro un bot nel mio dispositivo</td>
    </tr>
    <tr>
      <td>Logica del player bot (random)</td>
      <td>Chiara</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
    <tr>
      <td>Gestione dei turni e del loop di gioco</td>
      <td>Dilaver</td>
      <td>5</td>
      <td>5</td>
      <td>0</td>
    </tr>
    <tr>
      <td>9</td>
      <td>Impostazioni della partita</td>
      <td>Come utente, vorrei poter scegliere alcune regole di gioco come il numero di barche schierabili 
            e il tipo di partita (multiplayer o bot)</td>
      <td>Logica e GUI delle impostazioni di gioco</td>
      <td>Mirco</td>
      <td>9</td>
      <td>9</td>
      <td>0</td>
    </tr>
    <tr>
      <td>10</td>
      <td>Scelta della difficoltà del bot</td>
      <td>Come utente, vorrei avere la possibilità di scegliere la difficoltà di gioco</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>11</td>
      <td>Ostacoli</td>
      <td>Come utente, vorrei una mappa diversa, 
        con ostacoli che impediscono il posizionamento delle navi</td>
      <td></td>
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
    </tr>
  </tbody>
</table>

## Sprint Review
Lo stakeholder è complessivamente soddisfatto del risultato ottenuto 
poiché ha potuto visualizzare una demo completa di tutte le funzionalità principali dell'applicazione richiesta. 
Tuttavia, sono stati portati alla luce alcuni possibili miglioramenti riguardanti la _usability_ del prodotto:
un maggiore delay tra un turno e un altro, per poter meglio visualizzare il risultato del proprio attacco, 
una visualizzazione più precisa del risultato del colpo effettuato dal bot (nella versione vs bot) e una
spiegazione chiara per il posizionamento delle navi del primo player, così come avviene quando è il turno del secondo.

## Sprint Retrospective

La suddivisione dei task all'interno dello sprint si è rivelata efficace,
consentendo ai membri del team di lavorare in modo autonomo. 

Gli obiettivi prefissati sono stati raggiunti; 
tuttavia, è stata rilevata la possibilità di apportare alcune rifattorizzazioni 
su porzioni di codice che, nel frattempo, hanno acquisito una certa complessità.
