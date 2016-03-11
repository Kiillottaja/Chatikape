/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import tikape.chat.Alue;
import tikape.chat.Database;
import tikape.chat.Kayttaja;

/**
 *
 * @author teemupekkarinen
 */
public class KayttajaDao implements Dao<Kayttaja, String> {

    private Database data;

    public KayttajaDao(Database data) {
        this.data = data;
    }

    @Override
    public Kayttaja findOne(String key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Kayttaja WHERE nimimerkki = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            String nimimerkki = rs.getString("nimimerkki");
            String salasana = rs.getString("salasana");

            Kayttaja e = new Kayttaja(nimimerkki, salasana);

            rs.close();
            stmt.close();
            conn.close();

            return e;
        }
    }

    @Override
    public List<Kayttaja> findAll() throws SQLException {
        List<Kayttaja> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nimimerkki FROM Kayttaja;");

            while (rs.next()) {
                String nimimerkki = rs.getString("nimimerkki");

                Kayttaja k = findOne(nimimerkki);

                list.add(k);
            }

            rs.close();
            stmt.close();
            conn.close();

            return list;
        }
    }

    @Override
    public void delete(String key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kayttaja WHERE nimimerkki = ?;");
            
            stmt.setString(1, key);

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        }
    }

    public void lisaaKayttaja(Kayttaja kayttaja) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kayttaja (nimimerkki, salasana) VALUES (?,?);");
            
            stmt.setString(1, kayttaja.getNimimerkki());
            stmt.setString(2, kayttaja.getSalasana());

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        }
    }

    public boolean onkoTietokannassa(Kayttaja kayttaja) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT nimimerkki, salasana FROM Kayttaja WHERE nimimerkki = ?");
            stmt.setObject(1, kayttaja.getNimimerkki());

            ResultSet rs = stmt.executeQuery();

            boolean hasOne = rs.next();
            if (!hasOne) {

                rs.close();
                stmt.close();
                conn.close();

                return false;
            }
            String salasana = rs.getString("salasana");

            if (!kayttaja.getSalasana().equals(salasana)) {
                return false;
            }

            return true;
        }
    }

}
