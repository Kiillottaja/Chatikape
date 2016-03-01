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

    public Kayttaja(String nimimerkki, String name) {
        this.nimimerkki = nimimerkki;
        this.name = name;
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

    public void setNimimerkki(String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return nimimerkki;
    }

}
