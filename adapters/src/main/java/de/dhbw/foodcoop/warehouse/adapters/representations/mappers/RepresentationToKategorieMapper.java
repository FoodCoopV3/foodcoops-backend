package de.dhbw.foodcoop.warehouse.adapters.representations.mappers;

import de.dhbw.foodcoop.warehouse.adapters.representations.KategorieRepresentation;
import de.dhbw.foodcoop.warehouse.domain.entities.Kategorie;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RepresentationToKategorieMapper implements Function<KategorieRepresentation, Kategorie> {

    @Override
    public Kategorie apply(KategorieRepresentation kategorieRepresentation) {

        return new Kategorie(
                kategorieRepresentation.getId(),
                kategorieRepresentation.getName(),
                kategorieRepresentation.isMixable());
    }

    public Kategorie update(Kategorie oldKategorie, KategorieRepresentation newKategorie) {

        return new Kategorie(
                oldKategorie.getId(),
                pickNewIfDefined(oldKategorie.getName(), newKategorie.getName()),
                		newKategorie.isMixable()
        );
    }

    private String pickNewIfDefined(String oldValue, String newValue) {
        return replaceNullWithUndefined(newValue).equals("undefined") ? oldValue : newValue;
    }

    private String replaceNullWithUndefined(String oldValue) {
        return oldValue == null ? "undefined" : oldValue;
    }
}
