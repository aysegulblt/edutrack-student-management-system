package com.edutrack.model;

public class Sponsor extends Kullanici {

	private String kurumAdi;

    public Sponsor(String ad, String soyad, String kullaniciAdi, String sifre, String rol) {
        super(ad, soyad, kullaniciAdi, sifre, "Sponsor");
        this.kurumAdi = kurumAdi;
    }

    public String getKurumAdi() {
        return kurumAdi;
    }

    public void setKurumAdi(String kurumAdi) {
        this.kurumAdi = kurumAdi;
    }

    public void menuAc() {
        System.out.println("Sponsor menüsü açılıyor...");
       
    }
}
