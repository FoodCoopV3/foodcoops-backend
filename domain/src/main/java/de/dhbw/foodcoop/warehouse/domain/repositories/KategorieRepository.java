package de.dhbw.foodcoop.warehouse.domain.repositories;

import de.dhbw.foodcoop.warehouse.domain.values.Kategorie;

public interface KategorieRepository {
    Kategorie kategorieAnlegen(Kategorie kategorie);

    Kategorie kategorieAktualisieren(Kategorie kategorie);

    Kategorie kategorieLoeschen(Kategorie kategorie);
}
