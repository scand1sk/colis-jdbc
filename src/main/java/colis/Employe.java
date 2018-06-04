package colis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Employe {
    private final int idEmploye;
    private String nom;

    public Employe(int idEmploye, String nom) {
        this.idEmploye = idEmploye;
        this.nom = nom;
    }

    public static Optional<Employe> load(Connection con, int idEmploye) throws SQLException {
        String sql = "SELECT * FROM Employe WHERE idEmploye = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmploye);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadOne(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public static Employe loadOne(ResultSet rs) throws SQLException {
        String nom = rs.getString("nom");
        int idEmploye = rs.getInt("idEmploye");
        return new Employe(idEmploye, nom);
    }

    public int getIdEmploye() {
        return idEmploye;
    }

    @Override
    public String toString() {
        return "Employe{" +
                "idEmploye=" + idEmploye +
                ", nom='" + nom + '\'' +
                '}';
    }
}
