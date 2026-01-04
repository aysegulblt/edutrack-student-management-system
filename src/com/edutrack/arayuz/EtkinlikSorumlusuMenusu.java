package com.edutrack.arayuz;

import com.edutrack.AnaPencere;
import com.edutrack.model.Kullanici;
import com.edutrack.util.PanelTipi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import java.awt.*;

public class EtkinlikSorumlusuMenusu extends AbstractPanel {
    private final AnaPencere pencere;
    private final Kullanici aktifKullanici;
    private final CardLayout icerikLayout = new CardLayout();
    private final JPanel icerikPanel     = new JPanel(icerikLayout);

    public EtkinlikSorumlusuMenusu(AnaPencere pencere) {
        super("Etkinlik Sorumlusu Menüsü");
        this.pencere        = pencere;
        this.aktifKullanici = Kullanici.getAktifKullanici();

        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
        menuPanel.setOpaque(true);
        menuPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING/2, UIConstants.PADDING,
            UIConstants.PADDING/2, UIConstants.PADDING
        ));

        Dimension btnSize = new Dimension(180, 30);
        Font      btnFont = UIManager.getFont("Button.font");

        JButton btnGenel   = createButton("Genel Bakış",        "icons/dashboard.svg",    btnSize, btnFont);
        JButton btnYonet   = createButton("Etkinlikleri Yönet", "icons/manage-events.svg", btnSize, btnFont);
        JButton btnOlustur = createButton("Etkinlik Oluştur",   "icons/plus-circle.svg",  btnSize, btnFont);
        JButton btnBasvur  = createButton("Başvuru İncele",     "icons/review.svg",        btnSize, btnFont);
        JButton btnAyarlar = createButton("Ayarlar",            "icons/settings.svg",      btnSize, btnFont);

        menuPanel.add(btnGenel);
        menuPanel.add(Box.createHorizontalStrut(UIConstants.LAYOUT_GAP));
        menuPanel.add(btnYonet);
        menuPanel.add(Box.createHorizontalStrut(UIConstants.LAYOUT_GAP));
        menuPanel.add(btnOlustur);
        menuPanel.add(Box.createHorizontalStrut(UIConstants.LAYOUT_GAP));
        menuPanel.add(btnBasvur);
        menuPanel.add(Box.createHorizontalGlue());
        menuPanel.add(btnAyarlar);

        add(menuPanel, BorderLayout.NORTH);

        
        EtkinlikSorumlusuGenelBakisPaneli panelGenel   = new EtkinlikSorumlusuGenelBakisPaneli(aktifKullanici);
        EtkinlikYonetPaneli               panelYonet   = new EtkinlikYonetPaneli(aktifKullanici);
        EtkinlikOlusturPaneli             panelOlustur = new EtkinlikOlusturPaneli(aktifKullanici);
        EtkinlikBasvuruDegerlendirPaneli  panelBasvuru = new EtkinlikBasvuruDegerlendirPaneli(aktifKullanici);
        AyarlarPaneli                     panelAyarlar = new AyarlarPaneli(
            pencere, icerikLayout, icerikPanel,
            PanelTipi.ETKINLIK_SORUMLUSU_GENEL_BAKIS_PANEL.getKey()
        );

        icerikPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        icerikPanel.setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING, UIConstants.PADDING,
            UIConstants.PADDING, UIConstants.PADDING
        ));

        icerikPanel.add(panelGenel,   PanelTipi.ETKINLIK_SORUMLUSU_GENEL_BAKIS_PANEL.getKey());
        icerikPanel.add(panelYonet,   PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey() + "_Yonet");
        icerikPanel.add(panelOlustur, PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey() + "_Olustur");
        icerikPanel.add(panelBasvuru, PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey() + "_Basvurular");
        icerikPanel.add(panelAyarlar, PanelTipi.SIFRE_DEGISTIR_PANEL.getKey());

        add(icerikPanel, BorderLayout.CENTER);

        
        btnGenel.addActionListener(e -> {
            panelGenel.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.ETKINLIK_SORUMLUSU_GENEL_BAKIS_PANEL.getKey());
        });
        btnYonet.addActionListener(e -> {
            panelYonet.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey() + "_Yonet");
        });
        btnOlustur.addActionListener(e -> {
            panelOlustur.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey() + "_Olustur");
        });
        btnBasvur.addActionListener(e -> {
            panelBasvuru.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.ETKINLIK_SORUMLUSU_MENU.getKey() + "_Basvurular");
        });
        btnAyarlar.addActionListener(e -> {
            panelAyarlar.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.SIFRE_DEGISTIR_PANEL.getKey());
        });

        
        panelGenel.refreshData();
        icerikLayout.show(icerikPanel, PanelTipi.ETKINLIK_SORUMLUSU_GENEL_BAKIS_PANEL.getKey());
    }

    @Override
    public void refreshData() {
        
    }

    private JButton createButton(String text, String iconPath, Dimension size, Font font) {
        JButton btn = new JButton(text, IconUtil.loadSVG(iconPath, 16, 16));
        btn.setPreferredSize(size);
        btn.setFont(font);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        return btn;
    }
}