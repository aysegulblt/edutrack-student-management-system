package com.edutrack.model;


public abstract class Icerik {
    private String id;
    private String baslik;
    private String aciklama;

    public Icerik(String id, String baslik, String aciklama) {
        this.id = id;
        this.baslik = baslik;
        this.aciklama = aciklama;
    }

    public String getId() {
        return id;
    }

    public String getBaslik() {
        return baslik;
    }

    public String getAciklama() {
        return aciklama;
    }

   
    
    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
    
}