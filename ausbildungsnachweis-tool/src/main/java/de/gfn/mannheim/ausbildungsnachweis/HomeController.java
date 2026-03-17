package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controller: verarbeitet Anfragen vom Browser und gibt HTML-Seiten zurück
@Controller
public class HomeController {

    // Startseite – wird aufgerufen bei http://localhost:8080/
    @GetMapping("/")
    public String home() {
        // "index" bedeutet: templates/index.html wird geladen
        return "index";
    }

    // Login-Seite – Spring Security leitet hier hin wenn man nicht eingeloggt ist
    // Ohne diese Methode würde Spring Security einen Fehler werfen
    @GetMapping("/login")
    public String login() {
        // "login" bedeutet: templates/login.html wird geladen
        return "login";
    }
}