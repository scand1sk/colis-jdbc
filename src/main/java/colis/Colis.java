package colis;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Colis {
    private final int noColis;
    private int longueur;
    private int largeur;
    private int hauteur;
    private double poids;
    private LocalDateTime creation;
    private LocalDateTime livraison;
    private BigDecimal prix;

    private Client expediteur;
    private Client destinataire;
    private Lieu pointDestination;

    public Colis(int noColis, int longueur, int largeur, int hauteur, double poids, LocalDateTime creation, BigDecimal prix, Client expediteur, Client destinataire) {
        this.noColis = noColis;
        this.longueur = longueur;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.poids = poids;
        this.creation = creation;
        this.prix = prix;
        this.expediteur = expediteur;
        this.destinataire = destinataire;
    }

    public static Colis create(Connection con, int longueur, int largeur, int hauteur, double poids, Client expediteur, Client destinataire) throws SQLException {
        String sql = "INSERT INTO Colis (longueur, largeur, hauteur, poids, expediteur, destinataire) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, longueur);
            ps.setInt(2, largeur);
            ps.setInt(3, hauteur);
            ps.setDouble(4, poids);
            ps.setInt(5, expediteur.getIdClient());
            ps.setInt(6, expediteur.getIdClient());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int noColis = rs.getInt("noColis");
                    LocalDateTime creation = rs.getTimestamp("creation").toLocalDateTime();
                    BigDecimal prix = rs.getBigDecimal("prix");
                    return new Colis(noColis, longueur, largeur, hauteur, poids, creation, prix, expediteur, destinataire);
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }

    public void setDestination(Connection con, Lieu destination) throws SQLException {
        if (this.pointDestination != null) throw new IllegalStateException("La pointDestination est déjà renseignée: " + this.pointDestination);

        this.pointDestination = destination;
        String sql = "UPDATE Colis SET pointDestination = ? WHERE noColis = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, destination.getIdLieu());
            ps.setInt(2, noColis);
            ps.executeUpdate();
        }
    }

    public Entree addEntree(Connection con, Lieu lieu, Employe employe) throws SQLException {
        String sql = "INSERT INTO Entree (noColis, idLieu, idEmploye) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, noColis);
            ps.setInt(2, lieu.getIdLieu());
            ps.setInt(3, employe.getIdEmploye());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    LocalDateTime dateEntree = rs.getTimestamp("dateEntree").toLocalDateTime();
                    return new Entree(dateEntree, this, lieu, employe);
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }

    public List<Entree> suivi(Connection con) throws SQLException {
        String sql = "SELECT * FROM Entree JOIN Lieu USING (idLieu) JOIN Employe USING (idEmploye) WHERE noColis = ? ORDER BY dateEntree";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, noColis);
            try (ResultSet rs = ps.executeQuery()) {
                List<Entree> historique = new ArrayList<>();
                while (rs.next()) {
                    LocalDateTime dateEntree = rs.getTimestamp("dateEntree").toLocalDateTime();
                    Lieu lieu = Lieu.loadOne(rs);
                    Employe employe = Employe.loadOne(rs);
                    historique.add(new Entree(dateEntree, this, lieu, employe));
                }
                return historique;
            }
        }
    }

    @Override
    public String toString() {
        return "Colis{" +
                "noColis=" + noColis +
                ", longueur=" + longueur +
                ", largeur=" + largeur +
                ", hauteur=" + hauteur +
                ", poids=" + poids +
                ", creation=" + creation +
                ", livraison=" + livraison +
                ", prix=" + prix +
                ", expediteur=" + expediteur +
                ", destinataire=" + destinataire +
                ", pointDestination=" + pointDestination +
                '}';
    }

    public int getNoColis() {
        return noColis;
    }

    public void livraison(Connection con, Employe livreur) throws SQLException {
        if (this.livraison != null) throw new IllegalStateException("Le colis est déjà livré");
        String sql = "UPDATE Colis SET livraison = CURRENT_TIMESTAMP, livreur = ? WHERE noColis = ?";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, livreur.getIdEmploye());
            ps.setInt(2, noColis);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    this.livraison = rs.getTimestamp("livraison").toLocalDateTime();
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }
}
