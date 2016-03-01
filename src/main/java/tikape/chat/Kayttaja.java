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
    private String name;
    private String salasana;

    public Kayttaja(String nimimerkki, String name, String salasana) {
        this.nimimerkki = nimimerkki;
        this.name = name;
        this.salasana = salasana;
    }

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

    public String getName() {
        return name;
    }
    
    public String getSalasana() {
        return salasana;
    }

    public void setNimimerkki(String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalasana(String salasana) {
        this.salasana = salasana;
    }

    @Override
    public String toString() {
        return nimimerkki;
    }

}
