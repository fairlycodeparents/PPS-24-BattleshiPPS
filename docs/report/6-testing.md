# Testing

## Tecnologie utilizzate
Per la fase di testing è stato adottato `ScalaTest` insieme a `Matchers`, che ha reso possibile scrivere asserzioni espressive e 
leggibili (`shouldBe`, `should`, `contain`, ecc.).
Questa scelta ha permesso di mantenere i test consistenti e facilmente comprensibili e manutenibili.
In alcuni casi, per scenari complessi, sono stati introdotti degli _helper methods_ per evitare duplicazioni e semplificare la 
validazione del comportamento.

## Metodologia adottata e grado di copertura
Durante lo sviluppo si è provato ad adottare un approccio ispirato al _Test Driven Development_ (TDD): 
scrivere test unitari prima dell’implementazione e seguire il ciclo _red-green-refactor_.

Tuttavia, questa metodologia non è stata seguita in maniera rigorosa in tutte le parti del progetto.
In alcuni casi i test sono stati effettivamente scritti prima del codice (soprattutto nella logica di base), 
mentre in altri sono stati introdotti successivamente.

I test hanno garantito una base solida di verifica, supportando sia la collaborazione all’interno del team sia le attività 
di refactoring. 

Hanno permesso di controllare le specifiche, la correttezza della logica implementata e la valutazione di casi limite o non validi.

Hanno inoltre svolto un ruolo importante anche come strumento di documentazione pratica, poiché mostrano in modo diretto 
come le diverse componenti devono essere utilizzate. 
Questo ha permesso, ad esempio, di rendere immediatamente comprensibile ogni nuovo contributo agli altri membri del gruppo, 
semplicemente osservando i test già scritti.

## Esempi rilevanti
Alcuni esempi significativi sono riportati nelle singole sezioni di [implementazione](5-implementazione.md) dei membri del team.