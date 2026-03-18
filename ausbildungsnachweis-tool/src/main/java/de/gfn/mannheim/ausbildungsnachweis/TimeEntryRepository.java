package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    // Alle Einträge eines bestimmten Benutzers laden
    List<TimeEntry> findByUser(AppUser user);

    // Einträge eines Benutzers zwischen zwei Daten (für Monatsübersicht)
    List<TimeEntry> findByUserAndDatumBetween(AppUser user, LocalDate from, LocalDate to);
}