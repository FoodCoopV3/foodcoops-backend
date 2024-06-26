package de.dhbw.foodcoop.warehouse.plugins.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.dhbw.foodcoop.warehouse.domain.entities.FrischBestellung;

public interface SpringDataFrischBestellungRepository extends JpaRepository<FrischBestellung, String>{

    @Query("SELECT f FROM FrischBestellung f WHERE f.datum > :date AND f.personId = :person_id")
    List<FrischBestellung> findByDateAfterAndPerson(@Param("date") LocalDateTime date, @Param("person_id") String person_id);

    @Query("SELECT f FROM FrischBestellung f WHERE f.datum <= :date1 AND f.datum > :date2 AND f.personId = :person_id")
    List<FrischBestellung> findByDateBetween(@Param("date1") LocalDateTime date1, @Param("date2") LocalDateTime date2, @Param("person_id") String person_id);
    
    
    @Query("SELECT f FROM FrischBestellung f WHERE f.datum <= :date1 AND f.datum > :date2")
    List<FrischBestellung> findByDateBetween(@Param("date1") LocalDateTime date1, @Param("date2") LocalDateTime date2);
    
    
    @Query("SELECT new FrischBestellung(f.id, f.personId, f.frischbestand, SUM(f.bestellmenge), f.done) " +
            "FROM FrischBestellung f " +
            "WHERE f.datum > :date " +
            "GROUP BY f.frischbestand " +
            "ORDER BY f.frischbestand.kategorie.name"
    )
    List<FrischBestellung> findByDateAfterAndSum(@Param("date") LocalDateTime date);

    
    @Query("SELECT f FROM FrischBestellung f WHERE f.personId = :person_id")
    List<FrischBestellung> findAllFromPerson(@Param("person_id") String person_id);
    
    @Query("SELECT f FROM FrischBestellung f WHERE f.datum > :date")
    List<FrischBestellung> findAllAfter(@Param("date") LocalDateTime date);
}
