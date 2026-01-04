package com.edutrack;

import com.edutrack.arayuz.AyarlarPaneli;
import com.edutrack.arayuz.OgrenciMenusu;
import com.edutrack.arayuz.SponsorMenusu;
import com.edutrack.arayuz.OkulMenusu;
import com.edutrack.arayuz.EtkinlikSorumlusuMenusu;
import com.edutrack.model.Kullanici;
import com.edutrack.util.PanelTipi;

import javax.swing.JOptionPane;

public class Yonlendirici {

    public static void kullaniciYonlendir(Kullanici k) {
        AnaPencere pencere = AnaPencere.getInstance();
        switch (k.getRol()) {
            case "Öğrenci" -> {
                
                pencere.gosterPanel(
                    new AyarlarPaneli(
                        pencere,
                        pencere.getCardLayout(),
                        pencere.getCardPanel(),
                        PanelTipi.OGRENCI_MENU.getKey()
                    ),
                    PanelTipi.AYARLAR_PANELI.getKey()
                );
                
                pencere.gosterPanel(
                    new OgrenciMenusu(pencere),
                    PanelTipi.OGRENCI_MENU.getKey()
                );
            }
            case "Sponsor" -> {
                pencere.gosterPanel(
                    new AyarlarPaneli(
                        pencere,
                        pencere.getCardLayout(),
                        pencere.getCardPanel(),
                        PanelTipi.SPONSOR_MENU.getKey()
                    ),
                    PanelTipi.AYARLAR_PANELI.getKey()
                );
                pencere.gosterPanel(
                    new SponsorMenusu(pencere),
                    PanelTipi.SPONSOR_MENU.getKey()
                );
            }
            case "Okul Yetkilisi" -> {
                pencere.gosterPanel(
                    new AyarlarPaneli(
                        pencere,
                        pencere.getCardLayout(),
                        pencere.getCardPanel(),
                        PanelTipi.OKUL_MENU.getKey()
                    ),
                    PanelTipi.AYARLAR_PANELI.getKey()
                );
                pencere.gosterPanel(
                    new OkulMenusu(pencere),
                    PanelTipi.OKUL_MENU.getKey()
                );
            }
            case "Etkinlik Sorumlusu" -> {
                pencere.gosterPanel(
                    new AyarlarPaneli(
                        pencere,
                        pencere.getCardLayout(),
                        pencere.getCardPanel(),
                        PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey()
                    ),
                    PanelTipi.AYARLAR_PANELI.getKey()
                );
                pencere.gosterPanel(
                    new EtkinlikSorumlusuMenusu(pencere),
                    PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey()
                );
            }
            default ->
                JOptionPane.showMessageDialog(
                    null,
                    "Bilinmeyen rol: " + k.getRol(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE
                );
        }
    }
}