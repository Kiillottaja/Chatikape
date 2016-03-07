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
    public String nimimerkki;

    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:chatti.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        String DATABASE_URL = "postgres://odsinyrmznhzpm:-qSPdAXiwLpH5b78md6tVxOHJI@ec2-54-227-250-148.compute-1.amazonaws.com:5432/d68iqmmfd20f60";
        if (System.getenv(DATABASE_URL) != null) {
            jdbcOsoite = System.getenv(DATABASE_URL);
        }

        Database data = new Database(jdbcOsoite);
        data.setDebugMode(true);

        KayttajaDao kaDao = new KayttajaDao(data);
        AlueDao aDao = new AlueDao(data);
        KeskusteluDao keDao = new KeskusteluDao(data, aDao);
        ViestiDao vDao = new ViestiDao(data, kaDao, keDao, aDao);

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

            return "Käyttäjätunnus tai salasana eivät täsmää " + new ModelAndView(map, "index");
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

        post("/chat/alueet", (req, res) -> {
            String alue = req.queryParams("alue");

            if (aDao.haeNimella(alue) != null) {
                return "Alue on jo olemassa";
            }

            aDao.lisaaAlue(alue);

            res.redirect("/chat/alueet");
            return "Lisätty";
        });

        get("/chat/alueet/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            int id = Integer.parseInt(req.params("id"));

            String otsikko = "Alue: " + aDao.findOne(id).getNimi();

            map.put("otsikko", otsikko);
            map.put("aihe", aDao.findOne(id));
            map.put("keskustelut", aDao.alueenKeskustelut(aDao.findOne(id)));

            return new ModelAndView(map, "alueenKeskustelut");
        }, new ThymeleafTemplateEngine());

        post("/chat/alueet/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            String otsikko = req.queryParams("otsikko");

            Keskustelu k = new Keskustelu(otsikko);
            k.setAlue(aDao.findOne(id));

            if (keDao.haeNimella(otsikko) != null) {
                return "Keskustelu on jo olemassa";
            }

            keDao.lisaaKeskustelu(k);

            res.redirect("/chat/alueet/" + id);
            return "Lisätty";
        });

        get("/chat/:id/keskustelut", (req, res) -> {
            HashMap map = new HashMap<>();

            String otsikko = "Alue: " + keDao.findOne(Integer.parseInt(req.params("id"))).getAlue().getNimi() + " -> " + keDao.findOne(Integer.parseInt(req.params("id"))).getOtsikko();

            map.put("otsikko", otsikko);
            map.put("keskustelu", keDao.findOne(Integer.parseInt(req.params("id"))));
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
