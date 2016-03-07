/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.chat;

/**
 *
 * @author lauri
 */
public class AlueenKoonti {
    private String alue;
    private int viesteja;
    private String viimeisin;

    public AlueenKoonti(String alue, int viesteja, String viimeisin) {
        this.alue = alue;
        this.viesteja = viesteja;
        this.viimeisin = viimeisin;
    }

    public String getAlue() {
        return alue;
    }

    public void setAlue(String alue) {
        this.alue = alue;
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
        return this.alue + "\t" + this.viesteja + "\t" + this.viimeisin;
    }
    
    
    

    
}
