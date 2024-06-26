package de.dhbw.foodcoop.warehouse.domain.entities;

import de.dhbw.foodcoop.warehouse.domain.values.Einheit;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "frischbestand")
public class FrischBestand extends BestandEntity{

	
    @Column
    private String herkunftsland;
    @Column
    private float gebindegroesse;
    @ManyToOne
    @JoinColumn(name = "einheit_id")
    private Einheit einheit;
    
    @ManyToOne
    @JoinColumn(name = "kategorie_id")
    private Kategorie kategorie;
    
    @Column
    private String verband;
    
    @Column
    private boolean spezialfallBestelleinheit;


    public FrischBestand(String id, String name, boolean verfuegbarkeit, String herkunftsland, float gebindegroesse, Einheit einheit, Kategorie kategorie, float preis, String verband, boolean spezialfallBestelleinheit) {
        // Validate.notBlank(id);
        // Validate.notBlank(name);
        // Validate.notNull(verfuegbarkeit);
        // Validate.notNull(gebindegroesse);
        // Validate.notNull(preis);

    	this.spezialfallBestelleinheit = spezialfallBestelleinheit;
        this.id = id;
        this.name = name;
        this.verfuegbarkeit = verfuegbarkeit;
        this.herkunftsland = herkunftsland;
        this.gebindegroesse = gebindegroesse;
        this.einheit = einheit;
        this.kategorie = kategorie;
        this.preis = preis;
        this.verband = verband;
    }

    public FrischBestand(String name, boolean verfuegbarkeit, String herkunftsland, float gebindegroesse, Einheit einheit, Kategorie kategorie, float preis, String verband, boolean spezialfallBestelleinheit) {
        this(UUID.randomUUID().toString(), name, verfuegbarkeit, herkunftsland, gebindegroesse, einheit, kategorie, preis, verband, spezialfallBestelleinheit);
    }

    public FrischBestand() {

    }

    public String getVerband() {
		return verband;
	}

	public void setVerband(String verband) {
		this.verband = verband;
	}

	public String getHerkunftsland() {
        return herkunftsland;
    }

    public float getGebindegroesse() {
        return gebindegroesse;
    }

    public Einheit getEinheit() {
        return einheit;
    }

    public Kategorie getKategorie(){
        return kategorie;
    }

    
    public boolean isSpezialfallBestelleinheit() {
		return spezialfallBestelleinheit;
	}

	public void setSpezialfallBestelleinheit(boolean spezialfallBestelleinheit) {
		this.spezialfallBestelleinheit = spezialfallBestelleinheit;
	}

	public void setHerkunftsland(String herkunftsland) {
        this.herkunftsland = herkunftsland;
    }

    public void setGebindegroesse(float gebindegroesse) {
        this.gebindegroesse = gebindegroesse;
    }

    public void setEinheit(Einheit einheit) {
        this.einheit = einheit;
    }

    public void setKategorie(Kategorie kategorie){
        this.kategorie = kategorie;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrischBestand frischbestand = (FrischBestand) o;
        return id.equals(frischbestand.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FrischBestand{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", verfuegbarkeit= '" + verfuegbarkeit + '\'' +
                ", herkunftsland=" + herkunftsland + '\'' +
                ", gebindegroesse=" + gebindegroesse + '\'' +
                ", einheit=" + einheit.getName() + '\'' +
                ", kategorie=" + kategorie.getName() + '\'' +
                ", preis=" + preis +
                '}';
    }
}
