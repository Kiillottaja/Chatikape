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

        Database db = new Database(jdbcOsoite);

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
                return "Tervetuloa keskustelufoorumille " + nimimerkki;
            }
            return "Käyttäjätunnus tai salasana eivät täsmää";
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
                return "Käyttäjä lisätty tietokantaan: " + nimimerkki;
            }
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
            map.put("viestit", vDao.keskustelunViestit(keDao.findOne(1)));

            return new ModelAndView(map, "talous");
        }, new ThymeleafTemplateEngine());

        get("/chat/kemia", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("teksti", "Kemia");
            map.put("keskustelut", aDao.alueenKeskustelut(aDao.findOne(2)));

            return new ModelAndView(map, "kemia");
        }, new ThymeleafTemplateEngine());
    }
}
