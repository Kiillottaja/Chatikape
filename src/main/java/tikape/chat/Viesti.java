/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat;

import java.sql.Timestamp;

/**
 *
 * @author teemupekkarinen
 */
public class Viesti {

    //id integer PRIMARY KEY,
//nimimerkki VARCHAR(50),
//keskustelu_id integer,
//teksti VARCHAR(160) NOT NULL,
//pvm TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
//FOREIGN KEY (nimimerkki) REFERENCES Kayttaja (nimimerkki),
//FOREIGN KEY (keskustelu_id) REFERENCES Keskustelu (id)
//);
    private int id;
    private String teksti;
    private String pvm;
    private Kayttaja kayttaja;
    private Keskustelu keskustelu;

    public Viesti(Integer id, String teksti, String pvm) {
        this.id = id;
        this.teksti = teksti;
        this.pvm = pvm;
    }

    public int getId() {
        return id;
    }

    public String getTeksti() {
        return teksti;
    }

    public String getPvm() {
        return pvm;
    }

    public Kayttaja getKayttaja() {
        return kayttaja;
    }
    
    public Keskustelu getKeskustelu() {
        return keskustelu;
    }

    public void setKayttaja(Kayttaja kayttaja) {
        this.kayttaja = kayttaja;
    }
    
    public void setKeskustelu(Keskustelu keskustelu) {
        this.keskustelu = keskustelu;
    }
    
    public void setPvm(String time) {
        this.pvm = time;
    }
    
    @Override
    public String toString() {
        return "<" + pvm + "> " + kayttaja + ": " + teksti;
    }

}
