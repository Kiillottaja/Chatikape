/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.chat.Dao.*;

/**
 *
 * @author teemupekkarinen
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:chatti.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }

        Database data = new Database(jdbcOsoite);
        data.setDebugMode(true);

        KayttajaDao kaDao = new KayttajaDao(data);
        AlueDao aDao = new AlueDao(data);
        KeskusteluDao keDao = new KeskusteluDao(data, aDao);
        ViestiDao vDao = new ViestiDao(data, kaDao, keDao);      

        get("/chat", (req, res) -> {
            HashMap map = new HashMap<>();

            return new ModelAndView(map, "index");

        }, new ThymeleafTemplateEngine());

        post("/chat", (req, res) -> {
            String nimimerkki = req.queryParams("nimimerkki");
            String salasana = req.queryParams("salasana");

            Kayttaja kayt = new Kayttaja(nimimerkki, salasana);
            if (kaDao.onkoTietokannassa(kayt)) {
                res.redirect("chat/alueet");
                return "Tervetuloa keskustelufoorumille " + nimimerkki;
            }

            HashMap map = new HashMap<>();

            return "Käyttäjätunnus tai salasana eivät täsmää" + new ModelAndView(map, "index");
        });

        get("/chat/luokayttaja", (req, res) -> {
            HashMap map = new HashMap<>();

            return new ModelAndView(map, "luokayttaja");

        }, new ThymeleafTemplateEngine());

        post("/chat/luokayttaja", (req, res) -> {
            String nimimerkki = req.queryParams("nimimerkki");
            String salasana = req.queryParams("salasana");
            String salasana2 = req.queryParams("salasana2");

            if (kaDao.findOne(nimimerkki) != null) {
                return "Nimimerkki on jo käytössä";
            }

            if (!salasana.equals(salasana2)) {
                return "Tarkasta salasanojen vastaavuus";
            }

            Kayttaja kayt = new Kayttaja(nimimerkki, salasana);
            if (!kaDao.onkoTietokannassa(kayt)) {
                kaDao.lisaaKayttaja(kayt);
                res.redirect("/chat/alueet");
                return "Käyttäjä lisätty tietokantaan: " + nimimerkki;
            }
            res.redirect("/chat/alueet");
            return "Sinulla on jo käyttäjätunnus. Tervetuloa keskustelufoorumille " + kayt.getNimimerkki();
        });

        get("/chat/alueet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("teksti", "Keskustelualueet");
            map.put("alueet", aDao.findAll());

            return new ModelAndView(map, "alueet");
        }, new ThymeleafTemplateEngine());

        get("/chat/talous", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("keskustelut", aDao.alueenKeskustelut(aDao.findOne(1)));

            return new ModelAndView(map, "talous");
        }, new ThymeleafTemplateEngine());

        get("/chat/kemia", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("teksti", "Kemia");
            map.put("keskustelut", aDao.alueenKeskustelut(aDao.findOne(2)));

            return new ModelAndView(map, "kemia");
        }, new ThymeleafTemplateEngine());

        get("/chat/:id/keskustelut", (req, res) -> {
            HashMap map = new HashMap<>();

            String otsikko = "Alue: " + aDao.findOne(Integer.parseInt(req.params("id"))).getNimi() + " -> " + keDao.findOne(Integer.parseInt(req.params("id"))).getOtsikko();

            map.put("otsikko", otsikko);
            map.put("keskustelu", keDao.findOne(Integer.parseInt(req.params("id"))).getId());
            map.put("viestit", vDao.keskustelunViestit(keDao.findOne(Integer.parseInt(req.params("id")))));

            return new ModelAndView(map, "keskustelut");
        }, new ThymeleafTemplateEngine());

        post("/chat/:id/keskustelut", (req, res) -> {

            String nimimerkki = req.queryParams("nimimerkki");
            String viesti = req.queryParams("viesti");
            
            if (kaDao.findOne(nimimerkki) == null) {
                return "Nimimerkkiä ei löydy";
            }

            if (viesti.length() > 160) {
                return "Viesti liian pitkä!";
            }
            int id = Integer.parseInt(req.params("id"));
            Viesti v = new Viesti(viesti);
            v.setKayttaja(kaDao.findOne(nimimerkki));
            v.setKeskustelu(keDao.findOne(id));
            
            vDao.lisaaViesti(v);
            
            res.redirect("/chat/" + id + "/keskustelut");
            return "Viesti vastaanotettu";
        });

    }
}
