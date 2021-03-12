package de.dhbw.foodcoop.warehouse.application.LagerService;

import de.dhbw.foodcoop.warehouse.domain.entities.Kategorie;
import de.dhbw.foodcoop.warehouse.domain.repositories.KategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LagerResourceService {
    private final KategorieRepository kategorieRepository;

    @Autowired
    public LagerResourceService(KategorieRepository kategorieRepository) {
        this.kategorieRepository = kategorieRepository;
    }

    public List<Kategorie> getAllKategories() {
        return kategorieRepository.alleKategorienAbrufen();
    }
}
