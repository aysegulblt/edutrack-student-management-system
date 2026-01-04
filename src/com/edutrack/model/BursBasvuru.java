package com.edutrack.model;


public class BursBasvuru {
    private String kullaniciAdi;
    private String bursId;
    private String durum;        
    private String gerekce;      
    private String sponsorYorum; 

   
    public BursBasvuru(String kullaniciAdi,
                       String bursId,
                       String durum,
                       String gerekce,
                       String sponsorYorum) {
        this.kullaniciAdi = kullaniciAdi;
        this.bursId = bursId;
        this.durum = durum;
        this.gerekce = gerekce;
        this.sponsorYorum = sponsorYorum;
    }

    
    public BursBasvuru(String kullaniciAdi,
                       String bursId,
                       String durum,
                       String gerekce) {
        this(kullaniciAdi, bursId, durum, gerekce, "");
    }

    

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getBursId() {
        return bursId;
    }

    public void setBursId(String bursId) {
        this.bursId = bursId;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getGerekce() {
        return gerekce;
    }

    public void setGerekce(String gerekce) {
        this.gerekce = gerekce;
    }

    public String getSponsorYorum() {
        return sponsorYorum;
    }

    public void setSponsorYorum(String sponsorYorum) {
        this.sponsorYorum = sponsorYorum;
    }
}