package colis;

import java.sql.*;
import java.util.Optional;

public class Client {
    private final int idClient;
    private String email;
    private String password;

    public Client(int idClient, String email, String password) {
        this.idClient = idClient;
        this.email = email;
        this.password = password;
    }

    public static Client inscrire(Connection con, String email, String password) throws SQLException {
        String sql = "INSERT INTO Client(email, password) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idClient = rs.getInt("idClient");
                    return new Client(idClient, email, password);
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }

    public static Optional<Client> load(Connection con, int noClient) throws SQLException {
        String sql = "SELECT * FROM Client WHERE idClient = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, noClient);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    return Optional.of(new Client(noClient, email, password));
                } else {
                    return Optional.empty();
                }
            }
        }
    }


    public int getIdClient() {
        return idClient;
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
