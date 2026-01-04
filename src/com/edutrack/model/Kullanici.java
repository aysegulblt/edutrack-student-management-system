package com.edutrack.model;

import java.util.ArrayList;
import java.util.List;

public class Kullanici {
    private String ad;
    private String soyad;
    private String kullaniciAdi;
    private String parola;
    private String rol;

    
    private List<String> oncekiParolalar = new ArrayList<>();

    
    private static Kullanici aktifKullanici = null;

   
    public Kullanici() {
    }

   
    public Kullanici(String ad, String soyad, String kullaniciAdi, String parola, String rol) {
        this.ad = ad;
        this.soyad = soyad;
        this.kullaniciAdi = kullaniciAdi;
        this.parola = parola;
        this.rol = rol;
    }

    
    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    
    public List<String> getOncekiParolalar() {
        return oncekiParolalar;
    }

    public void setOncekiParolalar(List<String> oncekiParolalar) {
        this.oncekiParolalar = oncekiParolalar;
    }

    
    public static void setAktifKullanici(Kullanici kullanici) {
        aktifKullanici = kullanici;
    }

    public static Kullanici getAktifKullanici() {
        return aktifKullanici;
    }

    public static void oturumuKapat() {
        aktifKullanici = null;
    }
}
