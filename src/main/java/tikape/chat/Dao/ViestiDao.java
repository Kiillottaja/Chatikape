/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat.Dao;

/**
 *
 * @author teemupekkarinen
 */
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import tikape.chat.*;

/**
 *
 * @author teemupekkarinen
 */
public class ViestiDao implements Dao<Viesti, Integer> {

    private Database data;
    private Dao<Kayttaja, String> kaDao;
    private Dao<Keskustelu, Integer> keDao;

    public ViestiDao(Database data, Dao<Kayttaja, String> kaDao, Dao<Keskustelu, Integer> keDao) {
        this.data = data;
        this.kaDao = kaDao;
        this.keDao = keDao;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            Integer id = rs.getInt("id");
            String nimimerkki = rs.getString("nimimerkki");
            Integer keskusteluId = rs.getInt("keskustelu_id");
            Integer alueId = rs.getInt("alue_id");
            String teksti = rs.getString("teksti");
            String time = rs.getString("pvm");

            Viesti v = new Viesti(id, teksti, time);

            rs.close();
            stmt.close();
            conn.close();

            v.setKayttaja(kaDao.findOne(nimimerkki));
            v.setKeskustelu(keDao.findOne(keskusteluId));

            return v;
        }
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        List<Viesti> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM Viesti;");

            while (rs.next()) {
                Integer id = rs.getInt("id");
                Viesti v = findOne(id);

                list.add(v);

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
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Viesti WHERE id = ?;");

            stmt.setInt(1, key);

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        }
    }

    public List<Viesti> keskustelunViestit(Keskustelu keskustelu) throws SQLException {

        List<Viesti> list = new ArrayList();
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT Keskustelu.id, Viesti.nimimerkki, Viesti.teksti, Viesti.pvm FROM Keskustelu, Viesti WHERE Viesti.keskustelu_id = Keskustelu.id AND Keskustelu.id = ?");
            stmt.setObject(1, keskustelu.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String nimimerkki = rs.getString("nimimerkki");
                String teksti = rs.getString("teksti");
                String pvm = rs.getString("pvm");

                Viesti v = new Viesti(id, teksti, pvm);

                v.setKayttaja(kaDao.findOne(nimimerkki));
                v.setKeskustelu(keDao.findOne(id));

                System.out.println(v);

                list.add(v);
            }

            rs.close();
            stmt.close();
            conn.close();

            return list;
        }
    }

    public void lisaaViesti(Viesti v) throws SQLException {
        try (Connection conn = data.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Viesti(nimimerkki, keskustelu_id, alue_id, teksti) VALUES(?,?,?,?);");

           java.sql.Timestamp ts = new Timestamp(System.currentTimeMillis());
           
            stmt.setString(1, v.getKayttaja().getNimimerkki());
            stmt.setInt(2, v.getKeskustelu().getId());
            stmt.setInt(3, v.getAlue().getId());
            stmt.setString(4, v.getTeksti());

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        }
    }

}
