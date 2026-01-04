package com.edutrack.model;


public class Proje extends Icerik implements Secilebilir {

    private String teslimTarihi;
    private String olusturan;
    private String dokumanYolu; // Eklenen alan: proje dokümanının dosya yolu

    
    public Proje(String id,
                 String baslik,
                 String aciklama,
                 String teslimTarihi,
                 String olusturan,
                 String dokumanYolu) {
        super(id, baslik, aciklama);
        this.teslimTarihi = teslimTarihi;
        this.olusturan = olusturan;
        this.dokumanYolu = dokumanYolu;
    }

   
    public Proje(String id,
                 String baslik,
                 String aciklama,
                 String teslimTarihi,
                 String olusturan) {
        this(id, baslik, aciklama, teslimTarihi, olusturan, null);
    }

    
    public String getTeslimTarihi() {
        return teslimTarihi;
    }

    public void setTeslimTarihi(String teslimTarihi) {
        this.teslimTarihi = teslimTarihi;
    }

    public String getOlusturan() {
        return olusturan;
    }

    public void setOlusturan(String olusturan) {
        this.olusturan = olusturan;
    }

    public String getDokumanYolu() {
        return dokumanYolu;
    }

    public void setDokumanYolu(String dokumanYolu) {
        this.dokumanYolu = dokumanYolu;
    }

    @Override
    public void detayGoster() {
        System.out.println("Proje Detayları:");
        System.out.println("- Başlık: " + getBaslik());
        System.out.println("- Açıklama: " + getAciklama());
        System.out.println("- Teslim Tarihi: " + teslimTarihi);
        System.out.println("- Oluşturan: " + olusturan);
        System.out.println("- Doküman Yolu: " + (dokumanYolu != null ? dokumanYolu : "[yok]"));
    }

    @Override
    public void secildi() {
        System.out.println("Bu projeye başvuruldu: " + getBaslik());
    }
}