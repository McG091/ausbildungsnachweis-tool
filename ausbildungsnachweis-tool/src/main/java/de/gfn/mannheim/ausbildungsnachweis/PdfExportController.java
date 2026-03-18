package de.gfn.mannheim.ausbildungsnachweis;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

// Dieser Controller erzeugt kein HTML, sondern eine PDF-Datei zum Herunterladen
@Controller
public class PdfExportController {

    private final TimeEntryRepository repo;
    private final AppUserRepository userRepo;

    public PdfExportController(TimeEntryRepository repo, AppUserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    // Wird aufgerufen wenn der Benutzer auf "PDF herunterladen" klickt
    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(required = false) Integer year,
                          @RequestParam(required = false) Integer month,
                          @AuthenticationPrincipal UserDetails userDetails,
                          HttpServletResponse response) throws IOException {

        // Wenn kein Monat angegeben → aktuellen Monat nehmen
        LocalDate today = LocalDate.now();
        int y = (year != null) ? year : today.getYear();
        int m = (month != null) ? month : today.getMonthValue();

        // Ersten und letzten Tag des Monats berechnen
        LocalDate from = LocalDate.of(y, m, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        // Nur Einträge des eingeloggten Benutzers für diesen Monat laden
        AppUser currentUser = userRepo.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));
        List<TimeEntry> entries = repo.findByUserAndDatumBetween(currentUser, from, to);

        // Gesamtstunden berechnen
        long sumMin = entries.stream().mapToLong(TimeEntry::getNettoMinuten).sum();
        double sumHours = sumMin / 60.0;

        // Browser-Header: teilt dem Browser mit dass eine PDF-Datei kommt
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=Ausbildungsnachweis_" + y + "_"
                        + String.format("%02d", m) + ".pdf");

        // PDF-Dokument erstellen (DIN A4)
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // Schriftarten definieren
        Font titelSchrift  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalSchrift = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font fettSchrift   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        // Titel zentriert einfügen
        Paragraph titel = new Paragraph("Ausbildungsnachweis", titelSchrift);
        titel.setAlignment(Element.ALIGN_CENTER);
        doc.add(titel);

        // Benutzername und Zeitraum anzeigen
        Paragraph info = new Paragraph(
                "Benutzer: " + currentUser.getUsername() +
                        "     Zeitraum: " + String.format("%02d/%d", m, y), normalSchrift);
        info.setAlignment(Element.ALIGN_CENTER);
        info.setSpacingAfter(15f);
        doc.add(info);

        // Tabelle mit 5 Spalten erstellen
        PdfPTable tabelle = new PdfPTable(5);
        tabelle.setWidthPercentage(100); // Volle Seitenbreite
        tabelle.setWidths(new float[]{2f, 1.5f, 1.5f, 1f, 4f}); // Spaltenbreiten

        // Tabellenüberschriften (grau hinterlegt)
        for (String kopf : new String[]{"Datum", "Start", "Ende", "Netto (h)", "Tätigkeit"}) {
            PdfPCell zelle = new PdfPCell(new Phrase(kopf, fettSchrift));
            zelle.setBackgroundColor(new java.awt.Color(220, 220, 220));
            zelle.setPadding(5);
            tabelle.addCell(zelle);
        }

        // Eine Zeile pro Eintrag hinzufügen
        for (TimeEntry e : entries) {
            tabelle.addCell(new Phrase(e.getDatum().toString(), normalSchrift));
            tabelle.addCell(new Phrase(e.getStartzeit().toString(), normalSchrift));
            tabelle.addCell(new Phrase(e.getEndzeit().toString(), normalSchrift));
            tabelle.addCell(new Phrase(String.format("%.2f", e.getNettoStunden()), normalSchrift));
            tabelle.addCell(new Phrase(e.getTaetigkeit(), normalSchrift));
        }

        // Summenzeile am Ende der Tabelle
        PdfPCell summeLabel = new PdfPCell(new Phrase("Gesamt:", fettSchrift));
        summeLabel.setColspan(3); // Erstreckt sich über 3 Spalten
        summeLabel.setPadding(5);
        tabelle.addCell(summeLabel);

        PdfPCell summeWert = new PdfPCell(
                new Phrase(String.format("%.2f", sumHours), fettSchrift));
        summeWert.setPadding(5);
        tabelle.addCell(summeWert);
        tabelle.addCell(new Phrase("", normalSchrift));

        doc.add(tabelle);
        doc.close(); // PDF fertigstellen und an Browser senden
    }
}
