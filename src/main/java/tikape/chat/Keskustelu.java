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

    private Integer id;
    private Alue alue;
    private String otsikko;
    private int viesteja;
    private String viimeisin;

    public Keskustelu(Integer id, String otsikko) {
        this.id = id;
        this.otsikko = otsikko;
        this.viesteja = 0;
        this.viimeisin = "";
    }

    public Keskustelu(String otsikko) {
        this.otsikko = otsikko;
    }

    public void setAlue(Alue alue) {
        this.alue = alue;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getViesteja() {
        return viesteja;
    }

    public void setViesteja(int viestejä) {
        this.viesteja = viestejä;
    }

    public String getViimeisin() {
        return viimeisin;
    }

    public void setViimeisin(String viimeisin) {
        this.viimeisin = viimeisin;
    }

    @Override
    public String toString() {
        
        if (viesteja == 0 & viimeisin.isEmpty()) {
            return otsikko;
        }
        
        return otsikko + "\t" + viesteja + "\t" + viimeisin;
    }

}
