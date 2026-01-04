package com.edutrack.arayuz;

import com.edutrack.AnaPencere;
import com.edutrack.model.Kullanici;
import com.edutrack.util.PanelTipi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import java.awt.*;


public class OgrenciMenusu extends AbstractPanel {
    private final AnaPencere pencere;
    private final CardLayout icerikLayout = new CardLayout();
    private final JPanel icerikPanel = new JPanel(icerikLayout);

    public OgrenciMenusu(AnaPencere pencere) {
        super("Öğrenci Menüsü");
        this.pencere = pencere;

        
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(UIConstants.MENU_BACKGROUND);
        
        menuBar.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING / 2, UIConstants.PADDING,
            UIConstants.PADDING / 2, UIConstants.PADDING));

        
        JMenuItem itemGenel            = createMenuItem("Genel Bakış",       "icons/dashboard.svg");
        JMenuItem itemProjeleriGor     = createMenuItem("Projeleri Görüntüle","icons/edit.svg");
        JMenuItem itemBurslariGor      = createMenuItem("Bursları Görüntüle", "icons/review.svg");
        JMenuItem itemEtkinlikleriGor  = createMenuItem("Etkinlikleri Görüntüle","icons/manage-events.svg");
        JMenuItem itemAyarlar          = createMenuItem("Ayarlar",           "icons/settings.svg");

       
        menuBar.add(itemGenel);
        menuBar.add(itemProjeleriGor);
        menuBar.add(itemBurslariGor);
        menuBar.add(itemEtkinlikleriGor);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(itemAyarlar);

        add(menuBar, BorderLayout.NORTH);

        
        Kullanici aktif = Kullanici.getAktifKullanici();
        icerikPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        icerikPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING, UIConstants.PADDING,
            UIConstants.PADDING, UIConstants.PADDING));

       
        icerikPanel.add(new GenelBakisPaneli(aktif),          PanelTipi.GENELBAKIS_PANEL.getKey());
        ProjelerPaneli    panelProjeler    = new ProjelerPaneli(aktif);
        BurslarPaneli     panelBurslar     = new BurslarPaneli(aktif);
        EtkinliklerPaneli panelEtkinlikler = new EtkinliklerPaneli(aktif);
        AyarlarPaneli     panelAyarlar     = new AyarlarPaneli(
            pencere, icerikLayout, icerikPanel, PanelTipi.GENELBAKIS_PANEL.getKey()
        );

        icerikPanel.add(panelProjeler,     PanelTipi.PROJELER_PANEL.getKey());
        icerikPanel.add(panelBurslar,      PanelTipi.BURSLAR_PANEL.getKey());
        icerikPanel.add(panelEtkinlikler,  PanelTipi.ETKINLIKLER_PANEL.getKey());
        icerikPanel.add(panelAyarlar,      PanelTipi.AYARLAR_PANELI.getKey());

        add(icerikPanel, BorderLayout.CENTER);

        
        itemGenel.addActionListener(e -> icerikLayout.show(
            icerikPanel, PanelTipi.GENELBAKIS_PANEL.getKey()
        ));
        itemProjeleriGor.addActionListener(e -> {
            panelProjeler.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.PROJELER_PANEL.getKey());
        });
        itemBurslariGor.addActionListener(e -> {
            panelBurslar.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.BURSLAR_PANEL.getKey());
        });
        itemEtkinlikleriGor.addActionListener(e -> {
            panelEtkinlikler.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.ETKINLIKLER_PANEL.getKey());
        });
        itemAyarlar.addActionListener(e -> {
            panelAyarlar.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.AYARLAR_PANELI.getKey());
        });

        
        icerikLayout.show(icerikPanel, PanelTipi.GENELBAKIS_PANEL.getKey());
    }

    @Override
    public void refreshData() {
       
    }

    /**
     * Menü öğeleri için ortak stil ve ikon yükleme.
     */
    private JMenuItem createMenuItem(String text, String svgPath) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(UIManager.getFont("Menu.font"));
        item.setOpaque(false);
        Icon icon = IconUtil.loadSVG(svgPath, 16, 16);
        if (icon != null) {
            item.setIcon(icon);
            item.setHorizontalTextPosition(SwingConstants.RIGHT);
            item.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        }
        return item;
    }
}