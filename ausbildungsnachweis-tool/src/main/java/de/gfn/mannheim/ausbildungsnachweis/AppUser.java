package de.gfn.mannheim.ausbildungsnachweis;

import jakarta.persistence.*;

// Speichert den Benutzer (Username + verschlüsseltes Passwort) in der Datenbank
// So kann jeder Benutzer seine eigenen Zugangsdaten festlegen
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Benutzername – muss eindeutig sein
    @Column(unique = true, nullable = false)
    private String username;

    // Passwort – wird verschlüsselt gespeichert (niemals im Klartext)
    @Column(nullable = false)
    private String password;

    // Rolle – für dieses Projekt immer "ROLE_USER"
    private String role = "ROLE_USER";

    // --- Getter & Setter ---
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
