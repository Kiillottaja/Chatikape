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

        Database data = new Database("jdbc:sqlite:chat.db");
        data.setDebugMode(true);

        KayttajaDao kaDao = new KayttajaDao(data);
        AlueDao aDao = new AlueDao(data);
        KeskusteluDao keDao = new KeskusteluDao(data, aDao);
        ViestiDao vDao = new ViestiDao(data, kaDao, keDao);

        get("/chat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("teksti", "Keskustelualueet");
            map.put("alueet", aDao.findAll());
            map.put("viestit", vDao.keskustelunViestit(keDao.findOne(1)));

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/chat/talous", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("teksti", "Talous");
            map.put("keskustelut", aDao.alueenKeskustelut(aDao.findOne(1)));

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
