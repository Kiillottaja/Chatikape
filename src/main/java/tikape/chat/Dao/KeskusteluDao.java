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
import java.util.Date;
import java.util.List;
import tikape.chat.Alue;
import tikape.chat.Database;
import tikape.chat.Keskustelu;
import tikape.chat.Viesti;

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
        Connection connection = data.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE id = ?");
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
        connection.close();

        k.setAlue(aDao.findOne(alueId));

        return k;
    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        List<Keskustelu> list = new ArrayList();
        Connection conn = data.getConnection();
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

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = data.getConnection();
        Statement stmt = connection.createStatement();

        stmt.executeUpdate("DELETE FROM Keskustelu WHERE id = " + key + ";");

        stmt.close();
        connection.close();
    }

    public void lisaaKeskustelu(Keskustelu k) throws SQLException {
        Connection connection = data.getConnection();
        Statement stmt = connection.createStatement();

        stmt.executeUpdate("INSERT INTO Keskustelu (alue_id, otsikko) "
                + "VALUES (" + k.getAlue().getId() + ", "
                + "'" + k.getOtsikko() + "');");

        stmt.close();

        connection.close();
    }

}
