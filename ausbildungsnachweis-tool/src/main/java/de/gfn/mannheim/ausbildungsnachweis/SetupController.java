package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Dieser Controller ermöglicht die Registrierung neuer Benutzer
// Erreichbar über den "Registrieren" Button auf der Login-Seite
@Controller
public class SetupController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public SetupController(AppUserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Zeigt die Registrierungsseite an
    @GetMapping("/setup")
    public String setupForm() {
        return "setup"; // templates/setup.html
    }

    // Speichert den neuen Benutzer nach dem Absenden
    @PostMapping("/setup")
    public String setupSave(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String passwordConfirm,
                            Model model) {

        // Prüfen ob beide Passwörter übereinstimmen
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Die Passwörter stimmen nicht überein.");
            return "setup";
        }

        // Mindestlänge prüfen
        if (username.trim().length() < 3) {
            model.addAttribute("error", "Benutzername muss mindestens 3 Zeichen haben.");
            return "setup";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "Passwort muss mindestens 6 Zeichen haben.");
            return "setup";
        }

        // Prüfen ob Benutzername bereits vergeben ist
        if (userRepo.existsByUsername(username.trim())) {
            model.addAttribute("error", "Dieser Benutzername ist bereits vergeben.");
            return "setup";
        }

        // Neuen Benutzer erstellen und Passwort verschlüsselt speichern
        AppUser user = new AppUser();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);

        // Nach erfolgreicher Registrierung zur Login-Seite
        return "redirect:/login?setup";
    }
}