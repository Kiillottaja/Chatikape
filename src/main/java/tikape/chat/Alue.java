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
public class Alue {

    private Integer id;
    private String nimi;
    private int viesteja;
    private String viimeisin;

    public Alue(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public Alue(String nimi) {
        this.nimi = nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public Integer getId() {
        return id;
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
        return "%-10s %-10s %10s\n"+ this.nimi + this.viesteja + this.viimeisin;
    }

}
