package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Datenbankzugriff für den Benutzer – Spring Data JPA erstellt alles automatisch
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // Sucht einen Benutzer anhand des Benutzernamens
    // Wird von Spring Security beim Login verwendet
    Optional<AppUser> findByUsername(String username);

    // Prüft ob bereits ein Benutzer existiert
    // Wird beim ersten Start verwendet um zur Einrichtungsseite weiterzuleiten
    boolean existsByUsername(String username);
}
