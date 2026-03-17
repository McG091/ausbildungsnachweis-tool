# Ausbildungsnachweis-Tool

Eine Webanwendung zur digitalen Erfassung und Verwaltung von Ausbildungsnachweisen.
Entwickelt im Rahmen des Lernfelds 12A an der GFN Mannheim.

## Warum dieses Projekt?

In unserem Kurs wurden Lern- und Arbeitszeiten bisher manuell in Excel oder Word 
erfasst und am Monatsende mühsam zusammengeführt. Ich wollte das vereinfachen – 
also habe ich ein Tool gebaut, das die Erfassung strukturiert, Stunden automatisch 
berechnet und den fertigen Nachweis als PDF exportiert.

## Was kann die App?

- Einträge erfassen (Datum, Zeiten, Pause, Tätigkeit)
- Automatische Berechnung der Netto-Arbeitsstunden
- Einträge bearbeiten und löschen
- Monatsübersicht mit Stundenübersicht
- PDF-Export des monatlichen Ausbildungsnachweises
- Login/Logout

## Technologien

| Bereich | Technologie |
|---|---|
| Backend | Java 17, Spring Boot 3.5 |
| Datenbank | MySQL, Spring Data JPA |
| Sicherheit | Spring Security |
| Frontend | Thymeleaf, Bootstrap 5 |
| PDF | OpenPDF |
| Build | Maven |

## Setup (lokal)

1. MySQL-Datenbank erstellen: `ausbildungsnachweis`
2. Datei `application-local.properties` anlegen:
```
spring.datasource.url=jdbc:mysql://localhost:3306/ausbildungsnachweis
spring.datasource.username=DEIN_USER
spring.datasource.password=DEIN_PASSWORT
```
3. Anwendung starten – Tabellen werden automatisch erstellt
4. Browser: `http://localhost:8080`
5. Login: `azubi` / `azubi123`

## Status

In Entwicklung – Abgabe 30.03.2026
```
