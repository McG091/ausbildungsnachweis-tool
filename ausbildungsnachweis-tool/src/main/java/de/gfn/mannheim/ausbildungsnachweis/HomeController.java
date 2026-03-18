package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final AppUserRepository userRepo;

    public HomeController(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Startseite – zeigt immer zuerst die Startseite an
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Login-Seite – wird von Spring Security verwendet
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
