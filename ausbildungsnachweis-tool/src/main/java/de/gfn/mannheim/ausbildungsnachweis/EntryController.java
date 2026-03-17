package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

// Controller: verarbeitet Anfragen vom Browser und gibt HTML-Seiten zurück
@Controller
public class EntryController {

    // Datenbankzugriff über Spring Data JPA
    private final TimeEntryRepository repo;

    // Spring setzt das Repository automatisch ein (Constructor Injection)
    // Wir müssen das Objekt nicht selbst erstellen – Spring macht das für uns
    public EntryController(TimeEntryRepository repo) {
        this.repo = repo;
    }

    // ─── NEUER EINTRAG ──────────────────────────────────────────────────────

    // Zeigt das leere Formular an (GET = nur anschauen, nichts speichern)
    @GetMapping("/entry/new")
    public String newEntryForm(Model model) {
        TimeEntry entry = new TimeEntry();
        entry.setDatum(LocalDate.now()); // Standardmäßig heutiges Datum vorausfüllen
        entry.setPauseMin(0);            // Pause standardmäßig auf 0 setzen
        model.addAttribute("entry", entry);
        return "entry-form"; // templates/entry-form.html laden
    }

    // Speichert den neuen Eintrag nach dem Absenden (POST = Daten schicken)
    @PostMapping("/entry/new")
    public String saveEntry(@ModelAttribute("entry") TimeEntry entry, Model model) {
        // Erst validieren – wenn Fehler gefunden, Formular erneut anzeigen
        if (!validate(entry, model)) return "entry-form";
        repo.save(entry); // Eintrag in MySQL speichern
        return "redirect:/entries"; // Nach dem Speichern zur Liste weiterleiten
    }

    // ─── EINTRAG BEARBEITEN ─────────────────────────────────────────────────

    // Zeigt das Formular mit den vorhandenen Daten zum Bearbeiten
    // {id} in der URL wird automatisch als Parameter übergeben
    @GetMapping("/entry/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        // Eintrag aus DB laden – wenn ID nicht existiert, Fehler werfen
        TimeEntry entry = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ungültige ID: " + id));
        model.addAttribute("entry", entry);
        return "entry-form"; // Dasselbe Formular wie beim Erstellen verwenden
    }

    // Speichert die geänderten Daten des bestehenden Eintrags
    @PostMapping("/entry/edit/{id}")
    public String updateEntry(@PathVariable Long id,
                              @ModelAttribute("entry") TimeEntry entry,
                              Model model) {
        if (!validate(entry, model)) return "entry-form";
        // ID manuell setzen – so weiß JPA dass es updaten soll, nicht neu einfügen
        entry.setId(id);
        repo.save(entry);
        return "redirect:/entries";
    }

    // ─── EINTRAG LÖSCHEN ────────────────────────────────────────────────────

    // POST statt GET – damit man nicht aus Versehen durch einen Link löscht
    @PostMapping("/entry/delete/{id}")
    public String deleteEntry(@PathVariable Long id) {
        repo.deleteById(id); // Direkt aus der Datenbank löschen
        return "redirect:/entries";
    }

    // ─── ALLE EINTRÄGE ANZEIGEN ─────────────────────────────────────────────

    @GetMapping("/entries")
    public String listEntries(Model model) {
        // findAll() lädt alle Einträge aus der DB
        model.addAttribute("entries", repo.findAll());
        return "entries"; // templates/entries.html laden
    }

    // ─── MONATSÜBERSICHT ────────────────────────────────────────────────────

    // required = false bedeutet: Parameter sind optional
    // Wenn nichts angegeben wird, nehmen wir den aktuellen Monat
    @GetMapping("/month")
    public String monthView(@RequestParam(required = false) Integer year,
                            @RequestParam(required = false) Integer month,
                            Model model) {

        LocalDate today = LocalDate.now();
        int y = (year != null) ? year : today.getYear();
        int m = (month != null) ? month : today.getMonthValue();

        // Ersten und letzten Tag des Monats berechnen
        LocalDate from = LocalDate.of(y, m, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        // Nur Einträge für diesen Monat aus der DB holen
        List<TimeEntry> entries = repo.findByDatumBetween(from, to);

        // Netto-Minuten aller Einträge zusammenzählen
        long sumMin = 0;
        for (TimeEntry e : entries) sumMin += e.getNettoMinuten();

        model.addAttribute("entries", entries);
        model.addAttribute("year", y);
        model.addAttribute("month", m);
        model.addAttribute("sumMin", sumMin);
        model.addAttribute("sumHours", sumMin / 60.0); // In Stunden umrechnen
        return "month"; // templates/month.html laden
    }

    // ─── VALIDIERUNG ────────────────────────────────────────────────────────

    // Separate Methode damit ich nicht denselben Code zweimal schreiben muss
    // (wird sowohl bei "neu" als auch bei "bearbeiten" verwendet)
    // Gibt true zurück wenn alles OK ist, false wenn ein Fehler gefunden wurde
    private boolean validate(TimeEntry entry, Model model) {

        if (entry.getDatum() == null) {
            model.addAttribute("error", "Bitte Datum auswählen.");
            return false;
        }
        if (entry.getStartzeit() == null || entry.getEndzeit() == null) {
            model.addAttribute("error", "Bitte Startzeit und Endzeit eingeben.");
            return false;
        }
        // Endzeit muss nach Startzeit liegen – sonst ergibt die Berechnung keinen Sinn
        if (!entry.getEndzeit().isAfter(entry.getStartzeit())) {
            model.addAttribute("error", "Endzeit muss nach Startzeit liegen.");
            return false;
        }
        int pause = (entry.getPauseMin() != null) ? entry.getPauseMin() : 0;
        long dauerMin = Duration.between(entry.getStartzeit(), entry.getEndzeit()).toMinutes();
        // Pause darf nicht größer als die Gesamtzeit sein
        if (pause < 0 || pause > dauerMin) {
            model.addAttribute("error", "Pause ist ungültig (zu groß oder negativ).");
            return false;
        }
        if (entry.getTaetigkeit() == null || entry.getTaetigkeit().trim().isEmpty()) {
            model.addAttribute("error", "Bitte eine Tätigkeit eingeben.");
            return false;
        }
        return true; // Alles korrekt – Eintrag kann gespeichert werden
    }
}