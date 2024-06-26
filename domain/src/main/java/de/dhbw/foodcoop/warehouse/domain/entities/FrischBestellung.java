package de.dhbw.foodcoop.warehouse.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="frischbestellung")
public class FrischBestellung extends BestellungEntity{

    @ManyToOne
    @JoinColumn(name = "frischbestand_id")
    private FrischBestand frischbestand;
    
 

    public FrischBestellung(String id, String personId, FrischBestand frischbestand, double bestellmenge, LocalDateTime datum, boolean isDone) {
        //Validate.notBlank(id);
        //Validate.notNull(datum);
    	super();
        this.id = id;
        this.personId = personId;
        this.done = isDone;
        this.frischbestand = frischbestand;
        this.bestellmenge = bestellmenge;
        this.datum = datum;
    }

    public FrischBestellung(String id, String personId, FrischBestand frischbestand, double bestellmenge, boolean isDone) {
        //Validate.notBlank(id);
        //Validate.notNull(datum);
    	super();
        this.id = id;
        this.done = isDone;
        this.personId = personId;
        this.frischbestand = frischbestand;
        this.bestellmenge = bestellmenge;
    }

    public FrischBestellung(String personId, FrischBestand frischbestand, double bestellmenge, LocalDateTime datum, boolean isDone) {
        this(UUID.randomUUID().toString(), personId, frischbestand, bestellmenge, datum, isDone);
    }

    public FrischBestellung(String personId, FrischBestand frischbestand, double bestellmenge, boolean isDone) {
        this(UUID.randomUUID().toString(), personId, frischbestand, bestellmenge, isDone);
    }

    public FrischBestellung() {
    	super();
    }

   
    public FrischBestand getFrischbestand(){
        return frischbestand;
    }

    public void setFrischbestand(FrischBestand frischbestand){
        this.frischbestand = frischbestand;
    }


}
