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
import tikape.chat.Keskustelu;

/**
 *
 * @author teemupekkarinen
 */
public class KeskusteluDao implements Dao<Keskustelu, Integer> {

    private Database data;
    private Dao<Alue, Integer> aDao;

    public KeskusteluDao(Database data, Dao<Alue, Integer> aDao) {
        this.data = data;
        this.aDao = aDao;
    }

    @Override
    public Keskustelu findOne(Integer key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Keskustelu WHERE id = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            Integer alueId = rs.getInt("alue_id");
            String otsikko = rs.getString("otsikko");

            Keskustelu k = new Keskustelu(id, otsikko);

            rs.close();
            stmt.close();
            conn.close();

            k.setAlue(aDao.findOne(alueId));

            return k;
        }
    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        List<Keskustelu> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM Keskustelu;");

            while (rs.next()) {
                Integer id = rs.getInt("id");
                Keskustelu k = findOne(id);

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
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Keskustelu WHERE id = ?;");
            
            stmt.setInt(1, key);

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        }
    }

    public void lisaaKeskustelu(Keskustelu k) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Keskustelu (alue_id, otsikko) VALUES (?,?);");
            
            stmt.setInt(1, k.getAlue().getId());
            stmt.setString(2, k.getOtsikko());
            

            stmt.executeUpdate();

            stmt.close();

            conn.close();
        }
    }

    public Keskustelu haeNimella(String key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Keskustelu WHERE otsikko = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            Integer alueId = rs.getInt("alue_id");
            String otsikko = rs.getString("otsikko");

            Keskustelu k = new Keskustelu(id, otsikko);

            k.setAlue(aDao.findOne(alueId));

            rs.close();
            stmt.close();
            conn.close();

            return k;
        }
    }

    public List<Keskustelu> keskustelunViestienMaaraJaViimeisin(int alue_id) throws SQLException {
        List<Keskustelu> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT k.id AS id, k.otsikko AS otsikko, COUNT(v.id) AS maara, MAX(v.pvm) AS viimeisin FROM Keskustelu k LEFT JOIN Viesti v ON k.id = v.keskustelu_id GROUP BY k.id HAVING k.alue_id = ? ORDER BY v.pvm, k.otsikko DESC LIMIT 10;");

            stmt.setInt(1, alue_id);
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String otsikko = rs.getString("otsikko");
                Integer maara = rs.getInt("maara");
                String viimeisin = "-";
                if (rs.getString("viimeisin") != null) {
                    viimeisin = rs.getString("viimeisin");
                }
                Keskustelu k = new Keskustelu(id, otsikko);
                k.setAlue(aDao.findOne(alue_id));

                k.setViesteja(maara);
                k.setViimeisin(viimeisin);

                list.add(k);
            }

            rs.close();
            stmt.close();
            conn.close();

            return list;
        }
    }

}
