/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat;

/**
 *
 * @author teemupekkarinen
 */
public class Keskustelu {
    
//    //id integer PRIMARY KEY,
//alue_id integer,
//otsikko text NOT NULL,
//FOREIGN KEY (alue_id) REFERENCES Alue (id)
//CONSTRAINT Tarkastus CHECK (LENGTH(otsikko) > 2)
//);

    private Integer id;
    private Alue alue;
    private String otsikko;
    
    public Keskustelu(Integer id, String otsikko) {
        this.id = id;
        this.otsikko = otsikko;
    }
    
    public void setAlue(Alue alue) {
        this.alue = alue;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getOtsikko() {
        return otsikko;
    }
    
    public Alue getAlue() {
        return alue;
    }
    
    @Override
    public String toString() {
        return id + " " + alue + " " + otsikko;
    }
    
}
