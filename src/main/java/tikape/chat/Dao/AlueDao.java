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
import tikape.chat.Keskustelu;
import tikape.chat.Viesti;

/**
 *
 * @author teemupekkarinen
 */
public class AlueDao implements Dao<Alue, Integer> {

    private Database data;

    public AlueDao(Database data) {
        this.data = data;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Alue WHERE id = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            Alue a = new Alue(id, nimi);

            rs.close();
            stmt.close();
            conn.close();

            return a;
        }
    }

    @Override
    public List<Alue> findAll() throws SQLException {
        List<Alue> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM Alue;");

            while (rs.next()) {
                Integer id = rs.getInt("id");

                Alue k = findOne(id);

                list.add(k);
            }

            rs.close();
            stmt.close();
            conn.close();

            return list;
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Alue WHERE id = ?;");

            stmt.setInt(1, key);

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        }
    }

    public List<Keskustelu> alueenKeskustelut(Alue alue) throws SQLException {
        List<Keskustelu> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT Keskustelu.id, Keskustelu.otsikko FROM Alue, Keskustelu WHERE Alue.id = Keskustelu.alue_id AND Alue.id = ?");
            stmt.setObject(1, alue.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                Integer id = rs.getInt("id");
                String otsikko = rs.getString("otsikko");

                Keskustelu k = new Keskustelu(id, otsikko);

                k.setAlue(alue);

                list.add(k);
            }

            rs.close();
            stmt.close();
            conn.close();

            return list;
        }
    }

    public void lisaaAlue(String alue) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Alue (nimi) VALUES (?);");

            stmt.setString(1, alue);

            stmt.executeUpdate();
            stmt.close();

            conn.close();
        }
    }

    public Alue haeNimella(String key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Alue WHERE nimi = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            Alue a = new Alue(id, nimi);

            rs.close();
            stmt.close();
            conn.close();

            return a;
        }
    }

    public List<Alue> alueViestitYhteensaViimeisinViesti() throws SQLException {

        List<Alue> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT a.id AS id, a.nimi AS nimi, COUNT(v.id) AS maara, MAX(v.pvm) AS viimeisin FROM Alue a LEFT JOIN Keskustelu ke ON a.id = ke.alue_id LEFT JOIN Viesti v ON v.keskustelu_id=ke.id Group BY a.nimi ORDER BY a.nimi;");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String nimi = rs.getString("nimi");
                Integer maara = rs.getInt("maara");
                String viimeisin = "-";
                if (rs.getString("viimeisin") != null) {
                    viimeisin = rs.getString("viimeisin");
                }
                Alue ak = new Alue(id, nimi);

                ak.setViesteja(maara);
                ak.setViimeisin(viimeisin);

                list.add(ak);
            }

            rs.close();
            stmt.close();
            conn.close();

            return list;
        }
    }

}
