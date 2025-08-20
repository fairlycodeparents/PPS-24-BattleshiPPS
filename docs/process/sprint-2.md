---
title: Sprint 2
nav_order: 2
parent: Processo di Sviluppo
---

# Sprint 2

## Obiettivo
L’obiettivo di questo secondo Sprint è quello di realizzare la fase d'attacco del gioco, grazie alla quale l'utente potrà scegliere 
una casella da bersagliare nella mappa avversaria e ricevere un feedback relativo al colpo effettuato.

## Deadline
La scadenza dello sprint è il 28/07/2025.

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
    </tr>
    <tr>
      <td>Studio dell'architettura</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Docs Setup</td>
      <td>Mirco</td>
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
    </tr>
    <tr>
      <td rowspan="5">3</td>
      <td rowspan="5">Posizionamento navi</td>
      <td rowspan="5">Come utente, vorrei poter posizionare le navi nella mappa</td>
    </tr>
    <tr>
      <td>Creazione delle navi</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Realizzazione della GUI</td>
      <td>Chiara</td>
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
    </tr>
    <tr>
      <td>Posizionamento iniziale</td>
      <td>Dilaver</td>
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
    </tr>
    <tr>
      <td>Posizionamento randomico ripetuto</td>
      <td>Dilaver</td>
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
    </tr>
    <tr>
      <td>Estensione DSL per l'attacco</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="4">6</td>
      <td rowspan="4">Fase d'attacco</td>
      <td rowspan="4">Come utente, vorrei poter scegliere una casella dell’avversario da colpire</td>
    </tr>
    <tr>
      <td>Logica di attacco e fine partita</td>
      <td>Chiara</td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Feedback all'utente per il risultato dell'attacco</td>
      <td>Dilaver</td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>Estensione della board per supportare la logica di attacco</td>
      <td>Mirco</td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>7</td>
      <td>Modalità multiplayer</td>
      <td>Come utente, vorrei poter giocare
            contro un altro giocatore sul mio dispositivo</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>8</td>
      <td>Modalità vs bot</td>
      <td>Come utente, vorrei poter giocare contro un bot nel mio dispositivo</td>
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
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>10</td>
      <td>Scelta della difficoltà del bot</td>
      <td>Come utente, vorrei avere la possibilità di scegliere la difficoltà di gioco</td>
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
    </tr>
  </tbody>
</table>

## Sprint Review
Lo stakeholder è soddisfatto del risultato poichè sono stati raggiunti gli obiettivi prefissati. 
Nella demo è possibile attaccare su una mappa creata ad-hoc e visualizzare il risultato del proprio attacco fino al termine della simulazione, 
che avviene quando tutte le navi della mappa sono state affondate.


## Sprint Retrospective
La suddivisione dei task è risultata abbastanza bilanciata, evitando un carico eccessivo è stato possibile concentrarsi anche su uno studio più approfondito della programmazione funzionale.

Rispetto allo sprint precedente è stato notato un miglioramento dal punto di vista delle dipendenze tra task di ciascun membro del team,
ma è stato necessario aggiornare l'assegnamento di un task minore poichè è risultato più in linea con il lavoro di un componente del gruppo rispetto a quello del componente cui era stato originariamente assegnato.