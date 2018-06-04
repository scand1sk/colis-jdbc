import colis.*;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("localhost"); // ou iutinfo-sgbd
        ds.setDatabaseName("colis"); // iutinfoXX
        ds.setUser("vion");
        ds.setPassword("vion");

        try (Connection con = ds.getConnection()) {
            // Client test = Client.inscrire(con, "test@mail.com", null);

            Client toto = Client.load(con, -1).get();
            Client titi = Client.load(con, -2).get();


            Colis colisTest = Colis.create(con, 20, 30, 10, 0.5, toto, titi);

            Lieu valenciennes = Lieu.load(con, -1).get();
            Lieu maubeuge = Lieu.load(con, -2).get();
            Lieu centreTri = Lieu.load(con, -3).get();
            colisTest.setDestination(con, maubeuge);

            Employe roger = Employe.load(con, -1).get();
            Employe gerard = Employe.load(con, -2).get();
            Employe jeanmi = Employe.load(con, -3).get();

            // System.out.println(colisTest);

            colisTest.addEntree(con, valenciennes, roger);
            colisTest.addEntree(con, centreTri, gerard);
            colisTest.addEntree(con, maubeuge, gerard);

            colisTest.livraison(con, jeanmi);

            System.out.println(colisTest);

            for (Entree e: colisTest.suivi(con)) {
                System.out.println(e.getDateEntree() + ": " + e.getLieu() + " par " + e.getEmploye());
            }
        }
    }
}
