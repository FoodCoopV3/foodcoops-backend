package de.dhbw.foodcoop.warehouse.adapters.representations.mappers;

import de.dhbw.foodcoop.warehouse.adapters.representations.KategorieRepresentation;
import de.dhbw.foodcoop.warehouse.adapters.representations.ProduktRepresentation;
import de.dhbw.foodcoop.warehouse.domain.entities.Kategorie;
import de.dhbw.foodcoop.warehouse.domain.entities.Produkt;
import de.dhbw.foodcoop.warehouse.domain.utils.TestUtils;
import de.dhbw.foodcoop.warehouse.domain.values.Lagerbestand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepresentationToKategorieMapperTest {
    @Mock
    private RepresentationToProduktMapper produktMapper;
    @InjectMocks
    private RepresentationToKategorieMapper toBeTested;

    @Test
    @DisplayName("Representation to Kategorie Mapper no Produkte Test")
    void applyNoProdukte() {
        KategorieRepresentation given = getKategorieRepresentation(null);

        Kategorie then = toBeTested.apply(given);
        Assertions.assertNotNull(then);
        Assertions.assertEquals(TestUtils.KATEGORIE_TEST_ID, then.getId());
        Assertions.assertEquals("Teigwaren", then.getName());
        Assertions.assertEquals(TestUtils.TEIGWARENICON, then.getIcon());
        Assertions.assertNotNull(then.getProdukte());
        Assertions.assertTrue(then.getProdukte().isEmpty());
    }

    @Test
    @DisplayName("Representation to Kategorie Mapper Ignores Produkte Test")
    void applyIgnoresProdukte() {
        ProduktRepresentation apfel = new ProduktRepresentation(
                TestUtils.PRODUKT_TEST_ID
                , "Apfel"
                , TestUtils.KATEGORIE_TEST_ID
                , new Lagerbestand());
        ProduktRepresentation birne = new ProduktRepresentation(
                TestUtils.PRODUKT_TEST_ID
                , "Birne"
                , TestUtils.KATEGORIE_TEST_ID
                , new Lagerbestand());
        KategorieRepresentation given = getKategorieRepresentation(List.of(apfel, birne));

        Kategorie then = toBeTested.apply(given);

        Assertions.assertNotNull(then);
        Assertions.assertEquals(TestUtils.KATEGORIE_TEST_ID, then.getId());
        Assertions.assertEquals("Teigwaren", then.getName());
        Assertions.assertEquals(TestUtils.TEIGWARENICON, then.getIcon());
        Assertions.assertNotNull(then.getProdukte());
        Assertions.assertEquals(0, then.getProdukte().size());
    }

    @Test
    @DisplayName("Map old Kategorie and new KategorieRepresentation to one Kategorie")
    public void udpateSuccessfully() {
        KategorieRepresentation updateKategorieInfo = new KategorieRepresentation(null
                , "undefined"
                , TestUtils.TEIGWARENICON
                , null);
        Kategorie originalKategorie = getKategorie();

        Kategorie then = toBeTested.update(originalKategorie, updateKategorieInfo);

        Assertions.assertNotNull(then);
        Assertions.assertEquals(TestUtils.KATEGORIE_TEST_ID, then.getId());
        Assertions.assertEquals("Teigwaren", then.getName());
        Assertions.assertEquals(TestUtils.TEIGWARENICON, then.getIcon());
        Assertions.assertNotNull(then.getProdukte());
        Assertions.assertEquals(0, then.getProdukte().size());

    }

    private KategorieRepresentation getKategorieRepresentation(List<ProduktRepresentation> produktRepresentations) {
        return new KategorieRepresentation(TestUtils.KATEGORIE_TEST_ID
                , "Teigwaren"
                , TestUtils.TEIGWARENICON
                , produktRepresentations);
    }

    private Kategorie getKategorie() {
        return new Kategorie(
                TestUtils.KATEGORIE_TEST_ID
                , "Teigwaren"
                , TestUtils.BASICICON
                , List.of());
    }
}