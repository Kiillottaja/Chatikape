/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat;

import java.sql.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author teemupekkarinen
 */
public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws Exception {
        this.databaseAddress = databaseAddress;

        init();
    }

    private void init() {
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE Kayttaja;");
        lista.add("DROP TABLE Viesti;");
        lista.add("DROP TABLE Keskustelu;");
        lista.add("DROP TABLE Alue;");

        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Kayttaja(nimimerkki VARCHAR(50) PRIMARY KEY, salasana VARCHAR(20) NOT NULL, CONSTRAINT Tarkastus CHECK (LENGTH(nimimerkki) > 2 AND LENGTH(salasana > 2)));");
        lista.add("CREATE TABLE Viesti(id SERIAL PRIMARY KEY, nimimerkki VARCHAR(50), keskustelu_id integer, alue_id integer, teksti VARCHAR(160) NOT NULL, pvm TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, FOREIGN KEY (nimimerkki) REFERENCES Kayttaja (nimimerkki), FOREIGN KEY (keskustelu_id) REFERENCES Keskustelu (id), FOREIGN KEY (alue_id) REFERENCES Alue (id) );");
        lista.add("CREATE TABLE Keskustelu(id SERIAL PRIMARY KEY, alue_id integer, otsikko VARCHAR(30) NOT NULL, FOREIGN KEY (alue_id) REFERENCES Alue (id), CONSTRAINT Tarkastus CHECK(LENGTH(otsikko) > 2));");
        lista.add("CREATE TABLE Alue(id SERIAL PRIMARY KEY, nimi VARCHAR(30) NOT NULL);");

        lista.add("INSERT INTO Kayttaja(nimimerkki, salasana) VALUES ('Teme', 'teme666');");
        lista.add("INSERT INTO Kayttaja(nimimerkki, salasana) VALUES ('Late', 'late666');");
        lista.add("INSERT INTO Kayttaja(nimimerkki, salasana) VALUES ('Timppa','timppa666');");

        lista.add("INSERT INTO Alue(nimi) VALUES ('Talous');");
        lista.add("INSERT INTO Alue(nimi) VALUES ('Kemia');");
        lista.add("INSERT INTO Alue(nimi) VALUES ('Tietojenkäsittely');");

        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(1, 'Harmaa talous')");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(1, 'Harmaampi talous')");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(2, 'Kemistit')");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(2, 'Kemistikerho')");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(3, 'Tietokannat')");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(3, 'Chat-sovellus')");

        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 1, 1, 'Ei ole olemassa harmaata taloutta, kaikki on mustaa');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 2, 1, 'Hampaampaa on');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 3, 2, 'Kemistit on neroja!');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 4, 2, 'Mitenkäs tähän voisi liittyä?');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 5, 3, 'Tietokannat on kyl sekasin nyt');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 6, 3, 'Tuleekohan vaan valmiiksi....');");
        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Kayttaja(nimimerkki VARCHAR(50) PRIMARY KEY, salasana VARCHAR(20) NOT NULL, CONSTRAINT Tarkastus CHECK (LENGTH(nimimerkki) > 2 AND LENGTH(salasana > 2)));");
        lista.add("CREATE TABLE Viesti(id integer PRIMARY KEY, nimimerkki VARCHAR(50), keskustelu_id integer, alue_id integer, teksti VARCHAR(160) NOT NULL, pvm TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, FOREIGN KEY (nimimerkki) REFERENCES Kayttaja (nimimerkki), FOREIGN KEY (keskustelu_id) REFERENCES Keskustelu (id), FOREIGN KEY (alue_id) REFERENCES Alue (id));");
        lista.add("CREATE TABLE Keskustelu(id integer PRIMARY KEY, alue_id integer, otsikko VARCHAR(30) NOT NULL, FOREIGN KEY (alue_id) REFERENCES Alue (id), CONSTRAINT Tarkastus CHECK(LENGTH(otsikko) > 2));");
        lista.add("CREATE TABLE Alue(id integer PRIMARY KEY, nimi VARCHAR(30) NOT NULL);");

        lista.add("INSERT INTO Kayttaja(nimimerkki, salasana) VALUES ('Teme', 'teme666');");
        lista.add("INSERT INTO Kayttaja(nimimerkki, salasana) VALUES ('Late', 'late666');");
        lista.add("INSERT INTO Kayttaja(nimimerkki, salasana) VALUES ('Timppa', 'timppa666');");

        lista.add("INSERT INTO Alue(nimi) VALUES ('Talous');");
        lista.add("INSERT INTO Alue(nimi) VALUES ('Kemia');");
        lista.add("INSERT INTO Alue(nimi) VALUES ('Tietojenkäsittely');");

        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(1, 'Harmaa talous');");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(1, 'Harmaampi talous');");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(2, 'Kemistit');");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(2, 'Kemistikerho');");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(3, 'Tietokannat');");
        lista.add("INSERT INTO Keskustelu(alue_id, otsikko) VALUES(3, 'Chat-sovellus');");

        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 1, 1, 'Ei ole olemassa harmaata taloutta, kaikki on mustaa');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 2, 1, 'Hampaampaa on');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 3, 2, 'Kemistit on neroja!');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 4, 2, 'Mitenkäs tähän voisi liittyä?');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 5, 3, 'Tietokannat on kyl sekasin nyt');");
        lista.add("INSERT INTO Viesti(nimimerkki, keskustelu_id, teksti) VALUES('Teme', 6, 3, 'Tuleekohan vaan valmiiksi....');");

        return lista;
    }

    private void debug(ResultSet rs) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columns; i++) {
            System.out.print(
                    rs.getObject(i + 1) + ":"
                    + rs.getMetaData().getColumnName(i + 1) + "  ");
        }

        System.out.println();
    }

    void setDebugMode(boolean b) {

    }

}
