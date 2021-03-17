package de.dhbw.foodcoop.warehouse.plugins.persistence;

import de.dhbw.foodcoop.warehouse.domain.entities.Kategorie;
import de.dhbw.foodcoop.warehouse.domain.repositories.KategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KategorieRepositoryImpl implements KategorieRepository {
    private final SpringDataKategorieRepository springDataKategorieRepository;

    @Autowired
    public KategorieRepositoryImpl(SpringDataKategorieRepository springDataKategorieRepository) {
        this.springDataKategorieRepository = springDataKategorieRepository;
    }

    @Override
    public List<Kategorie> alleKategorienAbrufen() {
        return springDataKategorieRepository.findAll();
    }

    @Override
    public Kategorie speichern(Kategorie kategorie) {
        return springDataKategorieRepository.save(kategorie);
    }
}
