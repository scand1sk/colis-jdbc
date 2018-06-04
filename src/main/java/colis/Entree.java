package colis;

import java.sql.*;
import java.time.LocalDateTime;

public class Entree {
    private final LocalDateTime dateEntree;
    private final Colis colis;
    private Lieu lieu;
    private Employe employe;

    public Entree(LocalDateTime dateEntree, Colis colis, Lieu lieu, Employe employe) {
        this.dateEntree = dateEntree;
        this.colis = colis;
        this.lieu = lieu;
        this.employe = employe;
    }

    public LocalDateTime getDateEntree() {
        return dateEntree;
    }

    public Colis getColis() {
        return colis;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public Employe getEmploye() {
        return employe;
    }

    @Override
    public String toString() {
        return "Entree{" +
                "dateEntree=" + dateEntree +
                ", colis=" + colis +
                ", lieu=" + lieu +
                ", employe=" + employe +
                '}';
    }
}
