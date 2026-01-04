package com.edutrack.util;


public enum PanelTipi {
  
    GIRIS_PANELI("girisPaneli"),
    KAYIT_PANELI("kayitPaneli"),

    
    OGRENCI_MENU("ogrenciMenusu"),
    SPONSOR_MENU("sponsorMenusu"),
    OKUL_MENU("okulMenusu"),
    ETKINLIK_SORUMLUSU_MENU("etkinlikSorumlusuMenusu"),

    
    OKUL_GENEL_BAKIS_PANEL("okulGenelBakisPaneli"),
    SPONSOR_GENEL_BAKIS_PANEL("sponsorGenelBakisPaneli"),
    ETKINLIK_SORUMLUSU_GENEL_BAKIS_PANEL("etkinlikSorumlusuGenelBakisPaneli"),
    GENELBAKIS_PANEL("genelBakisPaneli"),

   
    PROJELER_PANEL("projelerPaneli"),
    BURSLAR_PANEL("burslarPaneli"),
    ETKINLIKLER_PANEL("etkinliklerPaneli"),

    
    YENI_PROJE_PANEL("yeniProjePaneli"),
    PROJELER_YONETIM_PANEL("projeleriYonetimPaneli"),
    PROJE_BASVURU_PANEL("projeBasvuruPaneli"),

    
    YENI_BURS_PANEL("yeniBursPaneli"),
    BURS_BASVURU_PANEL("bursBasvuruPaneli"),
    DESTEKLENEN_PANEL("desteklenenPaneli"),
    BURS_YONETIM_PANEL("bursYonetimPaneli"),

    
    ETKINLIK_YONETIM_PANEL("etkinlikYonetimPaneli"),

   
    SIFRE_DEGISTIR_PANEL("sifreDegistirPaneli"),

    
    AYARLAR_PANELI("ayarlarPaneli");

    private final String key;

    PanelTipi(String key) {
        this.key = key;
    }

    
    public String getKey() {
        return key;
    }
}