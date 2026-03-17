package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    // Einträge zwischen zwei Daten (für Monatsübersicht)
    List<TimeEntry> findByDatumBetween(LocalDate from, LocalDate to);
}
