package com.edutrack.arayuz;

import com.edutrack.AnaPencere;
import com.edutrack.model.Kullanici;
import com.edutrack.util.PanelTipi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import java.awt.*;


public class SponsorMenusu extends JPanel {
    private final AnaPencere pencere;
    private final Kullanici aktifKullanici;
    private final CardLayout icerikLayout = new CardLayout();
    private final JPanel icerikPanel     = new JPanel(icerikLayout);

    private final SponsorGenelBakisPaneli      panelGenel;
    private final YeniBursPaneli               panelYeniBurs;
    private final BursBasvuruDegerlendirPaneli panelBasvurular;
    private final BursYonetimPaneli            panelYonetim;
    private final DesteklenenOgrencilerPaneli  panelDesteklenen;
    private final AyarlarPaneli                panelAyarlar;

    public SponsorMenusu(AnaPencere pencere) {
        this.pencere        = pencere;
        this.aktifKullanici = Kullanici.getAktifKullanici();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 255, 240));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        JPanel menuPanel = new JPanel(new GridLayout(1, 6, 6, 0));
        menuPanel.setBackground(new Color(230, 250, 230));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Font btnFont = UIManager.getFont("Button.font");
        Dimension btnSize = new Dimension(120, 25);

        JButton btnGenel       = new JButton("Genel Bakış",   IconUtil.loadSVG("icons/dashboard.svg", 16, 16));
        JButton btnYeniBurs    = new JButton("Burs Oluştur",   IconUtil.loadSVG("icons/plus-circle.svg", 16, 16));
        JButton btnBasvurular  = new JButton("Başvuru İncele", IconUtil.loadSVG("icons/review.svg", 16, 16));
        JButton btnYonetim     = new JButton("Bursları Yönet", IconUtil.loadSVG("icons/manage.svg", 16, 16));
        JButton btnDesteklenen = new JButton("Desteklenenler", IconUtil.loadSVG("icons/group.svg", 16, 16));
        JButton btnAyarlar     = new JButton("Ayarlar",        IconUtil.loadSVG("icons/settings.svg", 16, 16));

        for (JButton b : new JButton[]{
                btnGenel, btnYeniBurs, btnBasvurular,
                btnYonetim, btnDesteklenen, btnAyarlar
        }) {
            b.setFont(btnFont);
            b.setPreferredSize(btnSize);
            b.setForeground(Color.BLACK);
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            b.setHorizontalTextPosition(SwingConstants.RIGHT);
            b.setIconTextGap(6);
            menuPanel.add(b);
        }

        add(menuPanel, BorderLayout.NORTH);

        
        panelGenel       = new SponsorGenelBakisPaneli(aktifKullanici);
        panelYeniBurs    = new YeniBursPaneli(aktifKullanici);
        panelBasvurular  = new BursBasvuruDegerlendirPaneli(aktifKullanici);
        panelYonetim     = new BursYonetimPaneli(aktifKullanici);
        panelDesteklenen = new DesteklenenOgrencilerPaneli(aktifKullanici);
        panelAyarlar     = new AyarlarPaneli(
            pencere, icerikLayout, icerikPanel,
            PanelTipi.SPONSOR_GENEL_BAKIS_PANEL.getKey()
        );

        icerikPanel.setBackground(new Color(250, 255, 250));
        icerikPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        icerikPanel.add(panelGenel,       PanelTipi.SPONSOR_GENEL_BAKIS_PANEL.getKey());
        icerikPanel.add(panelYeniBurs,    PanelTipi.YENI_BURS_PANEL.getKey());
        icerikPanel.add(panelBasvurular,  PanelTipi.BURS_BASVURU_PANEL.getKey());
        icerikPanel.add(panelYonetim,     PanelTipi.BURS_YONETIM_PANEL.getKey());
        icerikPanel.add(panelDesteklenen, PanelTipi.DESTEKLENEN_PANEL.getKey());
        icerikPanel.add(panelAyarlar,     PanelTipi.AYARLAR_PANELI.getKey());

        add(icerikPanel, BorderLayout.CENTER);

       
        btnGenel.addActionListener(e -> {
            panelGenel.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.SPONSOR_GENEL_BAKIS_PANEL.getKey());
        });
        btnYeniBurs.addActionListener(e ->
            icerikLayout.show(icerikPanel, PanelTipi.YENI_BURS_PANEL.getKey())
        );
        btnBasvurular.addActionListener(e -> {
            panelBasvurular.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.BURS_BASVURU_PANEL.getKey());
        });
        btnYonetim.addActionListener(e -> {
            panelYonetim.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.BURS_YONETIM_PANEL.getKey());
        });
        btnDesteklenen.addActionListener(e -> {
            panelDesteklenen.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.DESTEKLENEN_PANEL.getKey());
        });
        btnAyarlar.addActionListener(e -> {
            panelAyarlar.refreshData();
            icerikLayout.show(icerikPanel, PanelTipi.AYARLAR_PANELI.getKey());
        });

        
        panelGenel.refreshData();
        icerikLayout.show(icerikPanel, PanelTipi.SPONSOR_GENEL_BAKIS_PANEL.getKey());
    }
}