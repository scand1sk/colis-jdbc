package colis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Lieu {
    private final int idLieu;
    private String adresse;

    public Lieu(int idLieu, String adresse) {
        this.idLieu = idLieu;
        this.adresse = adresse;
    }

    public static Optional<Lieu> load(Connection con, int idLieu) throws SQLException {
        String sql = "SELECT * FROM Lieu WHERE idLieu = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLieu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadOne(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public static Lieu loadOne(ResultSet rs) throws SQLException {
        String adresse = rs.getString("adresse");
        int idLieu = rs.getInt("idLieu");
        return new Lieu(idLieu, adresse);
    }

    public int getIdLieu() {
        return idLieu;
    }

    @Override
    public String toString() {
        return "Lieu{" +
                "idLieu=" + idLieu +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
