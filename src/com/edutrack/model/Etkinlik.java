package com.edutrack.model;


public class Etkinlik {
    private String id;
    private String baslik;
    private String aciklama;
    private String tarih;
    private int kontenjan;
    private String olusturan; 
    
    
    public Etkinlik() {}

    
    public Etkinlik(String id, String baslik, String aciklama, String tarih, int kontenjan, String olusturan) {
        this.id = id;
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.tarih = tarih;
        this.kontenjan = kontenjan;
        this.olusturan = olusturan;
    }

    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getBaslik() {
        return baslik;
    }
    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getAciklama() {
        return aciklama;
    }
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getTarih() {
        return tarih;
    }
    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public int getKontenjan() {
        return kontenjan;
    }
    public void setKontenjan(int kontenjan) {
        this.kontenjan = kontenjan;
    }

    public String getOlusturan() {
        return olusturan;
    }
    public void setOlusturan(String olusturan) {
        this.olusturan = olusturan;
    }
}