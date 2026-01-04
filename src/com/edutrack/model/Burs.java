package com.edutrack.model;


public class Burs {
    private String id;
    private String baslik;
    private String aciklama;
    private double miktar;
    private String sponsorAdi;
    private String deadline;  

    
    public Burs(String id, String baslik, String aciklama,
                double miktar, String sponsorAdi, String deadline) {
        this.id = id;
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.miktar = miktar;
        this.sponsorAdi = sponsorAdi;
        this.deadline = deadline;
    }

    
    public Burs(String id, String baslik, String aciklama,
                double miktar, String sponsorAdi) {
        this(id, baslik, aciklama, miktar, sponsorAdi, "");
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

    public double getMiktar() {
        return miktar;
    }
    public void setMiktar(double miktar) {
        this.miktar = miktar;
    }

    public String getSponsorAdi() {
        return sponsorAdi;
    }
    public void setSponsorAdi(String sponsorAdi) {
        this.sponsorAdi = sponsorAdi;
    }

    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}