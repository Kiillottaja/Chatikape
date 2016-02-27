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
        Connection conn = data.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Kayttaja WHERE nimimerkki = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        String nimimerkki = rs.getString("nimimerkki");
        String nimi = rs.getString("nimi");

        Kayttaja e = new Kayttaja(nimimerkki, nimi);

        rs.close();
        stmt.close();
        conn.close();

        return e;

    }

    @Override
    public List<Kayttaja> findAll() throws SQLException {
        List<Kayttaja> list = new ArrayList();
        Connection conn = data.getConnection();
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

    @Override
    public void delete(String key) throws SQLException {
        Connection connection = data.getConnection();
        Statement stmt = connection.createStatement();

        stmt.executeUpdate("DELETE FROM Kayttaja WHERE id = " + key + ";");

        stmt.close();
        connection.close();

    }

    public void lisaaKayttaja(Kayttaja kayttaja) throws SQLException {
        Connection connection = data.getConnection();
        Statement stmt = connection.createStatement();

        stmt.executeUpdate("INSERT INTO Kayttaja (nimimerkki, nimi) VALUES ('" + kayttaja.getNimimerkki() + "', '" + kayttaja.getName() + "');");
        stmt.close();

        connection.close();
    }

}
