package com.edutrack.model;


public class ProjeBasvuru {
    private String kullaniciAdi;
    private String projeId;
    private String durum;     
    private String aciklama;   

    
    public ProjeBasvuru() {
        this.aciklama = "";
    }

    
    public ProjeBasvuru(String kullaniciAdi, String projeId, String durum) {
        this(kullaniciAdi, projeId, durum, "");
    }

    
    public ProjeBasvuru(String kullaniciAdi,
                        String projeId,
                        String durum,
                        String aciklama) {
        this.kullaniciAdi = kullaniciAdi;
        this.projeId      = projeId;
        this.durum        = durum;
        this.aciklama     = aciklama != null ? aciklama : "";
    }

    
   
    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    
    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    
    public String getProjeId() {
        return projeId;
    }

    
    public void setProjeId(String projeId) {
        this.projeId = projeId;
    }

    
    public String getDurum() {
        return durum;
    }

    
    public void setDurum(String durum) {
        this.durum = durum;
    }

    
    public String getAciklama() {
        return aciklama;
    }

    
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama != null ? aciklama : "";
    }

    @Override
    public String toString() {
        return "ProjeBasvuru{" +
               "kullaniciAdi='" + kullaniciAdi + '\'' +
               ", projeId='" + projeId + '\'' +
               ", durum='" + durum + '\'' +
               ", aciklama='" + aciklama + '\'' +
               '}';
    }
}