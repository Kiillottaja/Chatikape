/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat;

import java.util.HashMap;
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
        String DATABASE_URL = "postgres://odsinyrmznhzpm:-qSPdAXiwLpH5b78md6tVxOHJI@ec2-54-227-250-148.compute-1.amazonaws.com:5432/d68iqmmfd20f60";
        if (System.getenv(DATABASE_URL) != null) {
            jdbcOsoite = System.getenv(DATABASE_URL);
        }

        Database data = new Database(jdbcOsoite);
        data.setDebugMode(true);

        KayttajaDao kaDao = new KayttajaDao(data);
        AlueDao aDao = new AlueDao(data);
        KeskusteluDao keDao = new KeskusteluDao(data, aDao);
        ViestiDao vDao = new ViestiDao(data, kaDao, keDao);
        Kayttaja nykyinen = new Kayttaja("");

        get("/chat", (req, res) -> {
            HashMap map = new HashMap<>();

            return new ModelAndView(map, "index");

        }, new ThymeleafTemplateEngine());

        post("/chat", (req, res) -> {
            String nimimerkki = req.queryParams("nimimerkki");
            String salasana = req.queryParams("salasana");

            Kayttaja kayt = new Kayttaja(nimimerkki, salasana);
            if (kaDao.onkoTietokannassa(kayt)) {
                nykyinen.setNimimerkki(nimimerkki);
                res.redirect("chat/alueet");
                return "Tervetuloa keskustelufoorumille " + nimimerkki;
            }

            HashMap map = new HashMap<>();

            return "Käyttäjätunnus tai salasana eivät täsmää"
                    + "<br/>"
                    + "<a href = '/chat'> "
                    + "<span> Takaisin </span> "
                    + "</a>";
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
                return "Nimimerkki on jo käytössä"
                        + "<br/>"
                        + "<a href = '/chat/luokayttaja'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            if (!salasana.equals(salasana2)) {
                return "Tarkasta salasanojen vastaavuus"
                        + "<br/>"
                        + "<a href = '/chat/luokayttaja'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            if (nimimerkki.length() < 3) {
                return "Nimimerkki liian lyhyt. Anna yli 2 merkkiä pitkä nimimerkki"
                        + "<br/>"
                        + "<a href = '/chat/luokayttaja'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            if (salasana.length() < 3) {
                return "Salasana liian lyhyt. Anna yli 2 merkkiä pitkä salasana"
                        + "<br/>"
                        + "<a href = '/chat/luokayttaja'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            Kayttaja kayt = new Kayttaja(nimimerkki, salasana);
            if (!kaDao.onkoTietokannassa(kayt)) {
                kaDao.lisaaKayttaja(kayt);
                nykyinen.setNimimerkki(nimimerkki);
                res.redirect("/chat/alueet");
                return "Käyttäjä lisätty tietokantaan: " + nimimerkki;
            }
            nykyinen.setNimimerkki(nimimerkki);
            res.redirect("/chat/alueet");
            return "Sinulla on jo käyttäjätunnus. Tervetuloa keskustelufoorumille " + kayt.getNimimerkki();
        });

        get("/chat/alueet", (req, res) -> {
            
            if (nykyinen.getNimimerkki().isEmpty()) {
                HashMap map = new HashMap<>();

                return new ModelAndView(map, "index");
            }
            
            HashMap map = new HashMap<>();
            map.put("teksti", "Keskustelualueet");
            map.put("alueet", aDao.alueViestitYhteensaViimeisinViesti());
            String otsikot = "\t    Alue    \t    Viestejä yhteensä    \t    Viimeisin viesti";
            map.put("otsikot", otsikot);

            return new ModelAndView(map, "alueet");
        }, new ThymeleafTemplateEngine());

        post("/chat/alueet", (req, res) -> {
            String alue = "";
            alue = req.queryParams("alue");

            if (aDao.haeNimella(alue) != null) {
                return "Alue on jo olemassa"
                        + "<br/>"
                        + "<a href = '/chat/alueet'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            if (alue.isEmpty()) {
                return "Anna pidempi alueen nimi"
                        + "<br/>"
                        + "<a href = '/chat/alueet'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            aDao.lisaaAlue(alue);

            res.redirect("/chat/alueet");
            return "Lisätty";
        });

        get("/chat/alueet/:id", (req, res) -> {
                        
            if (nykyinen.getNimimerkki().isEmpty()) {
                HashMap map = new HashMap<>();

                return new ModelAndView(map, "index");
            }
            
            HashMap map = new HashMap<>();
            int id = Integer.parseInt(req.params("id"));

            String otsikko = "Alue: " + aDao.findOne(id).getNimi();

            map.put("otsikko", otsikko);

            map.put("keskustelut", keDao.keskustelunViestienMaaraJaViimeisin(id));
            String otsikot = "\t    Otsikko    \t    Viestien määrä    \t    Viimeisin viesti";
            map.put("otsikot", otsikot);

            return new ModelAndView(map, "alueenKeskustelut");
        }, new ThymeleafTemplateEngine());

        post("/chat/alueet/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            String otsikko = req.queryParams("otsikko");

            Keskustelu k = new Keskustelu(otsikko);
            k.setAlue(aDao.findOne(id));

            if (keDao.haeNimella(otsikko) != null) {
                return "Keskustelu on jo olemassa"
                        + "<br/>"
                        + "<a href = '/chat/alueet/" + id + "'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            if (otsikko.isEmpty()) {
                return "Anna pidempi otsikko"
                        + "<br/>"
                        + "<a href = '/chat/alueet/" + id + "'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            keDao.lisaaKeskustelu(k);

            res.redirect("/chat/alueet/" + id);
            return "Lisätty";
        });

        get("/chat/:id/keskustelut", (req, res) -> {
                                    
            if (nykyinen.getNimimerkki().isEmpty()) {
                HashMap map = new HashMap<>();

                return new ModelAndView(map, "index");
            }
            
            HashMap map = new HashMap<>();

            String otsikko = "Alue: " + keDao.findOne(Integer.parseInt(req.params("id"))).getAlue().getNimi() + " -> " + keDao.findOne(Integer.parseInt(req.params("id"))).getOtsikko();

            map.put("otsikko", otsikko);
            map.put("keskustelu", keDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("viestit", vDao.keskustelunViestit(keDao.findOne(Integer.parseInt(req.params("id")))));

            return new ModelAndView(map, "keskustelut");
        }, new ThymeleafTemplateEngine());

        post("/chat/:id/keskustelut", (req, res) -> {

            String nimimerkki = nykyinen.getNimimerkki();
            String viesti = "";
            viesti = req.queryParams("viesti");
            int id = Integer.parseInt(req.params("id"));

            if (viesti.length() > 160) {
                return "Viesti liian pitkä!"
                        + "<br/>"
                        + "<a href = '/chat/" + id + "/keskustelut'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            if (viesti.isEmpty()) {
                return "Viesti on tyhjä!"
                        + "<br/>"
                        + "<a href = '/chat/" + id + "/keskustelut'> "
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            if (nimimerkki.isEmpty()) {
                return "Kirjautuminen epäonnistui. Kirjaudu uudelleen!"
                        + "<br/>"
                        + "<a href=/chat>"
                        + "<span> Takaisin </span> "
                        + "</a>";
            }

            Viesti v = new Viesti(viesti);
            v.setKayttaja(kaDao.findOne(nimimerkki));
            v.setKeskustelu(keDao.findOne(id));
            v.setAlue(keDao.findOne(id).getAlue());

            vDao.lisaaViesti(v);

            res.redirect("/chat/" + id + "/keskustelut");
            return "Viesti vastaanotettu";
        });

    }
}
