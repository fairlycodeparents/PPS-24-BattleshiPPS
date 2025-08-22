---
title: Sprint 1
nav_order: 1
parent: Processo di Sviluppo
---

# Sprint 1

## Obiettivo
L’obiettivo di questo primo Sprint è quello di ottenere una demo funzionante nella quale l’utente potrà posizionare, 
spostare e/o ruotare a proprio piacimento le proprie navi, a partire da una disposizione random delle navi nella mappa.

## Deadline
La scadenza dello sprint è il 21/07/2025.

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
      <th>Stima iniziale</th>
      <th>Stima Sprint 1</th>
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
      <td>3</td>
      <td>0</td>
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
      <td>5</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Realizzazione della GUI</td>
      <td>Chiara</td>
      <td>5</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Logica spostamento navi (selezione nave da spostare, 
        muovere la nave nella nuova posizione, 
        ruotare la nave)</td>
      <td>Dilaver</td>
      <td>15</td>
      <td>4</td>
    </tr>
    <tr>
      <td>Posizionamento iniziale</td>
      <td>Dilaver</td>
      <td>1</td>
      <td>0</td>
    </tr>
    <tr>
      <td>4</td>
      <td>Posizionamento random</td>
      <td>Come utente, vorrei la possibilità di schierare in modo automatico le mie navi</td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td rowspan="1">5</td>
      <td rowspan="1">Testing avanzato</td>
      <td>Il testing dovrà includere un DSL che permette di controllare 
        in modo chiaro ed efficace il corretto funzionamento del progetto</td>
      <td></td>
      <td>Mirco</td>
      <td></td>
      <td></td>
    </tr>
    <tr>
      <td>6</td>
      <td>Fase d'attacco</td>
      <td>Come utente, vorrei poter scegliere una casella dell’avversario da colpire</td>
      <td></td>
      <td></td>
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
Lo stakeholder è soddisfatto della demo funzionante poiché sono stati raggiunti gli obiettivi prefissati: è possibile 
creare la propria mappa e posizionare, spostare e/o ruotare un set predefinito di navi.

## Sprint Retrospective
Lo sprint ha avuto durata di due settimane per via delle configurazioni iniziali del progetto.
Particolare attenzione è stata posta sullo studio dei task da realizzare e sulla successiva suddivisione del lavoro.
La suddivisione complessiva dei task è risultata bilanciata tra i membri del team, ma per garantire una buona suddivisione
è stato necessario risolvere problemi iniziali relativi al coordinamento del lavoro tra i membri del team.
La suddivisione dei task ha, infatti, portato a inevitabili dipendenze tra i membri del team.

Le principali modifiche da apportare per il prossimo sprint sono:
- una suddivisione più accurata dei task, in modo da evitare dipendenze tra i membri del team
- una diminuzione della durata dello sprint, per ottenere maggiori e frequenti feedback da parte dello stakeholder.
