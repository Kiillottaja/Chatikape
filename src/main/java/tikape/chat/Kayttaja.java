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
public class Kayttaja {

    private String nimimerkki;
    private String salasana;

    public Kayttaja(String nimimerkki, String salasana) {
        this.nimimerkki = nimimerkki;
        this.salasana = salasana;
    }

    public Kayttaja(String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

    public String getNimimerkki() {
        return nimimerkki;
    }
    
    public String getSalasana() {
        return salasana;
    }

    public void setNimimerkki(String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

    public void setSalasana(String salasana) {
        this.salasana = salasana;
    }

    @Override
    public String toString() {
        return nimimerkki;
    }

}
