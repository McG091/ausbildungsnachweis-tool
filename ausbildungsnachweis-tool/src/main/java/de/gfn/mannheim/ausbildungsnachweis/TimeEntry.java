package de.gfn.mannheim.ausbildungsnachweis;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity // JPA-Entity: wird als Tabelle in MySQL gespeichert
@Table(name = "time_entry")
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-Increment in MySQL
    private Long id;

    // Datum des Eintrags (z.B. 2026-03-11)
    private LocalDate datum;

    // Start- und Endzeit (z.B. 08:30 / 16:30)
    private LocalTime startzeit;
    private LocalTime endzeit;

    // Pause in Minuten (z.B. 30)
    private Integer pauseMin;

    // Kurze Tätigkeitsbeschreibung
    @Column(length = 500)
    private String taetigkeit;

    // Zeitstempel (für Nachvollziehbarkeit)
    private LocalDateTime createdAt;

    @PrePersist // wird automatisch beim ersten Speichern gesetzt
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getter/Setter (einfach, ohne Lombok) ---
    public Long getId() { return id; }

    // Wird beim Bearbeiten (Edit) benötigt – setzt die ID manuell damit
// JPA den bestehenden Eintrag aktualisiert und keinen neuen erstellt
    public void setId(Long id) { this.id = id; }

    public LocalDate getDatum() { return datum; }
    public void setDatum(LocalDate datum) { this.datum = datum; }

    public LocalTime getStartzeit() { return startzeit; }
    public void setStartzeit(LocalTime startzeit) { this.startzeit = startzeit; }

    public LocalTime getEndzeit() { return endzeit; }
    public void setEndzeit(LocalTime endzeit) { this.endzeit = endzeit; }

    public Integer getPauseMin() { return pauseMin; }
    public void setPauseMin(Integer pauseMin) { this.pauseMin = pauseMin; }

    public String getTaetigkeit() { return taetigkeit; }
    public void setTaetigkeit(String taetigkeit) { this.taetigkeit = taetigkeit; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // Verknüpfung mit dem Benutzer – jeder Eintrag gehört einem Benutzer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    // Netto-Minuten = (Ende - Start) - Pause
    public long getNettoMinuten() {
        if (startzeit == null || endzeit == null) return 0;
        long minutes = java.time.Duration.between(startzeit, endzeit).toMinutes();
        int pause = (pauseMin != null) ? pauseMin : 0;
        return Math.max(0, minutes - pause);
    }

    // Anzeige in Stunden (z.B. 7.5)
    public double getNettoStunden() {
        return getNettoMinuten() / 60.0;
    }

}