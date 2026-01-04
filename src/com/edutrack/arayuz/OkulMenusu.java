package com.edutrack.arayuz;

import com.edutrack.AnaPencere;
import com.edutrack.model.Kullanici;
import com.edutrack.util.PanelTipi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import java.awt.*;

public class OkulMenusu extends AbstractPanel {
    private final AnaPencere pencere;
    private final Kullanici aktifKullanici;
    private final CardLayout icerikLayout = new CardLayout();
    private final JPanel icerikPanel     = new JPanel(icerikLayout);

    public OkulMenusu(AnaPencere pencere) {
        super("Okul Menüsü");
        this.pencere = pencere;
        this.aktifKullanici = Kullanici.getAktifKullanici();

        
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(UIConstants.MENU_BACKGROUND);
        menuBar.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING/2, UIConstants.PADDING,
            UIConstants.PADDING/2, UIConstants.PADDING
        ));

        JMenuItem itemGenel          = createMenuItem("Genel Bakış",       "icons/dashboard.svg");
        JMenuItem itemYeniProje      = createMenuItem("Yeni Proje Oluştur", "icons/plus-circle.svg");
        JMenuItem itemProjeleriYonet = createMenuItem("Projeleri Yönet",    "icons/manage-events.svg");
        JMenuItem itemBasvuruListesi = createMenuItem("Başvuruları Değerlendir", "icons/review.svg");
        JMenuItem itemAyarlar        = createMenuItem("Ayarlar",          "icons/settings.svg");

        menuBar.add(itemGenel);
        menuBar.add(itemYeniProje);
        menuBar.add(itemProjeleriYonet);
        menuBar.add(itemBasvuruListesi);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(itemAyarlar);
        add(menuBar, BorderLayout.NORTH);

        
        OkulGenelBakisPaneli           panelGenel    = new OkulGenelBakisPaneli(aktifKullanici);
        YeniProjePaneli                panelYeni     = new YeniProjePaneli(aktifKullanici);
        ProjeleriYonetPaneli           panelProjeler = new ProjeleriYonetPaneli(aktifKullanici);
        ProjeBasvuruDegerlendirPaneli  panelBasvuru  = new ProjeBasvuruDegerlendirPaneli(aktifKullanici);
        AyarlarPaneli                  panelAyarlar  = new AyarlarPaneli(
            pencere, icerikLayout, icerikPanel,
            PanelTipi.OKUL_GENEL_BAKIS_PANEL.getKey()
        );

        icerikPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        icerikPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING, UIConstants.PADDING,
            UIConstants.PADDING, UIConstants.PADDING
        ));
        icerikPanel.add(panelGenel,    PanelTipi.OKUL_GENEL_BAKIS_PANEL.getKey());
        icerikPanel.add(panelYeni,     PanelTipi.YENI_PROJE_PANEL.getKey());
        icerikPanel.add(panelProjeler, PanelTipi.PROJELER_YONETIM_PANEL.getKey());
        icerikPanel.add(panelBasvuru,  PanelTipi.PROJE_BASVURU_PANEL.getKey());
        icerikPanel.add(panelAyarlar,  PanelTipi.SIFRE_DEGISTIR_PANEL.getKey());

        add(icerikPanel, BorderLayout.CENTER);

        
        itemGenel.addActionListener(e -> {
            panelGenel.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.OKUL_GENEL_BAKIS_PANEL.getKey());
        });
        itemYeniProje.addActionListener(e ->
            icerikLayout.show(icerikPanel, PanelTipi.YENI_PROJE_PANEL.getKey())
        );
        itemProjeleriYonet.addActionListener(e -> {
            panelProjeler.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.PROJELER_YONETIM_PANEL.getKey());
        });
        itemBasvuruListesi.addActionListener(e -> {
            panelBasvuru.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.PROJE_BASVURU_PANEL.getKey());
        });
        itemAyarlar.addActionListener(e -> {
            panelAyarlar.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.SIFRE_DEGISTIR_PANEL.getKey());
        });

        
        panelGenel.refreshData();
        icerikLayout.show(icerikPanel, PanelTipi.OKUL_GENEL_BAKIS_PANEL.getKey());
    }

    @Override
    public void refreshData() {
        
    }

    private JMenuItem createMenuItem(String text, String svgPath) {
        JMenuItem mi = new JMenuItem(text);
        mi.setFont(UIManager.getFont("Menu.font"));
        mi.setOpaque(false);
        Icon icon = IconUtil.loadSVG(svgPath, 16, 16);
        if (icon != null) {
            mi.setIcon(icon);
            mi.setHorizontalTextPosition(SwingConstants.RIGHT);
            mi.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        }
        return mi;
    }
}