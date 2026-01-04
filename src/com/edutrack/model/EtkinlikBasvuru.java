package com.edutrack.model;


public class EtkinlikBasvuru {
    private String kullaniciAdi;
    private String etkinlikId;
    private String durum;            
    private String ogrenciGerekce;   
    private String comments;         

    public EtkinlikBasvuru() {
        this("", "", "Beklemede", "", "");
    }

    public EtkinlikBasvuru(String kullaniciAdi,
                           String etkinlikId,
                           String durum) {
        this(kullaniciAdi, etkinlikId, durum, "", "");
    }

    public EtkinlikBasvuru(String kullaniciAdi,
                           String etkinlikId,
                           String durum,
                           String ogrenciGerekce) {
        this(kullaniciAdi, etkinlikId, durum, ogrenciGerekce, "");
    }

    public EtkinlikBasvuru(String kullaniciAdi,
                           String etkinlikId,
                           String durum,
                           String ogrenciGerekce,
                           String comments) {
        this.kullaniciAdi    = kullaniciAdi;
        this.etkinlikId      = etkinlikId;
        this.durum           = durum;
        this.ogrenciGerekce  = ogrenciGerekce != null ? ogrenciGerekce : "";
        this.comments        = comments       != null ? comments       : "";
    }

    public String getKullaniciAdi()       { return kullaniciAdi; }
    public void   setKullaniciAdi(String u){ this.kullaniciAdi = u; }

    public String getEtkinlikId()         { return etkinlikId; }
    public void   setEtkinlikId(String i) { this.etkinlikId = i; }

    public String getDurum()              { return durum; }
    public void   setDurum(String d)      { this.durum = d; }

    public String getOgrenciGerekce()     { return ogrenciGerekce; }
    public void   setOgrenciGerekce(String g) {
        this.ogrenciGerekce = g != null ? g : "";
    }

    
    public String getComments()           { return comments; }
    public void   setComments(String c)   { this.comments = c != null ? c : ""; }

    @Override
    public String toString() {
        return "EtkinlikBasvuru{" +
               "kullaniciAdi='"   + kullaniciAdi   + '\'' +
               ", etkinlikId='"   + etkinlikId     + '\'' +
               ", durum='"        + durum          + '\'' +
               ", ogrenciGerekce='"+ogrenciGerekce + '\'' +
               ", comments='"     + comments       + '\'' +
               '}';
    }
}