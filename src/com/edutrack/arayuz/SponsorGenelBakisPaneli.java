package com.edutrack.arayuz;

import com.edutrack.model.Burs;
import com.edutrack.model.BursBasvuru;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public class SponsorGenelBakisPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JLabel lblTotalBurs  = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblBekleyen   = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblOnaylanan  = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblReddedilen = new JLabel("0", SwingConstants.CENTER);

    public SponsorGenelBakisPaneli(Kullanici aktifKullanici) {
        super("");  // boş başlık => görünmez
        this.aktifKullanici = aktifKullanici;
        initUI();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData();
            }
        });
        refreshData();
    }

    private void initUI() {
        setLayout(new GridLayout(2, 2, UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING, UIConstants.PADDING,
            UIConstants.PADDING, UIConstants.PADDING
        ));
        setBackground(UIConstants.BACKGROUND_COLOR);

        Font titleFont = UIConstants.HEADER_FONT;
        Font valueFont = UIConstants.HEADER_FONT.deriveFont(Font.PLAIN, 26f);

        lblTotalBurs .setFont(valueFont);
        lblBekleyen  .setFont(valueFont);
        lblOnaylanan .setFont(valueFont);
        lblReddedilen.setFont(valueFont);

        add(wrapCard("Toplam Burs",        lblTotalBurs,  new Color(225, 245, 255)));
        add(wrapCard("Bekleyen Başvuru",   lblBekleyen,   new Color(255, 250, 225)));
        add(wrapCard("Onaylanan Başvuru",  lblOnaylanan,  new Color(225, 255, 235)));
        add(wrapCard("Reddedilen Başvuru", lblReddedilen, new Color(255, 230, 230)));
    }

    private JPanel wrapCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(
                UIConstants.PADDING, UIConstants.PADDING,
                UIConstants.PADDING, UIConstants.PADDING
            )
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(UIConstants.HEADER_FONT);
        card.add(lblTitle, BorderLayout.NORTH);

        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refreshData() {
        String sponsorAdi = aktifKullanici.getKullaniciAdi();

        
        List<Burs> burslar = DosyaYoneticisi.burslariGetir().stream()
            .filter(b -> sponsorAdi.equals(b.getSponsorAdi()))
            .collect(Collectors.toList());

        
        List<BursBasvuru> basvurular = DosyaYoneticisi.bursBasvurulariniGetir().stream()
            .filter(bb -> burslar.stream()
                .anyMatch(b -> b.getId().equals(bb.getBursId())))
            .collect(Collectors.toList());

        lblTotalBurs .setText(String.valueOf(burslar.size()));
        lblBekleyen  .setText(String.valueOf(
            basvurular.stream().filter(b -> "Beklemede".equals(b.getDurum())).count()
        ));
        lblOnaylanan .setText(String.valueOf(
            basvurular.stream().filter(b -> "Onaylandı".equals(b.getDurum())).count()
        ));
        lblReddedilen.setText(String.valueOf(
            basvurular.stream().filter(b -> "Reddedildi".equals(b.getDurum())).count()
        ));
    }
}