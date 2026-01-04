package com.edutrack.arayuz;

import com.edutrack.model.*;
import com.edutrack.veri.DosyaYoneticisi;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


public class GenelBakisPaneli extends JPanel {
    private final Kullanici aktifKullanici;
    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;

    public GenelBakisPaneli(Kullanici aktifKullanici) {
        this.aktifKullanici = aktifKullanici;

        Insets m = (Insets) UIManager.get("Button.margin");
        setLayout(new GridLayout(0, 2, m.left, m.top));
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(BorderFactory.createEmptyBorder(
            m.top, m.left, m.bottom, m.right
        ));

        
        add(createCard("Toplam Proje",       String.valueOf(DosyaYoneticisi.projeleriGetir().size()),
            new Color(235, 245, 255)));
        long basvProj = DosyaYoneticisi.projeBasvurulariniGetir().stream()
            .filter(b -> b.getKullaniciAdi().equals(aktifKullanici.getKullaniciAdi()))
            .count();
        add(createCard("Başvurduğu Proje",   String.valueOf(basvProj),
            new Color(255, 250, 230)));
        long onayProj = DosyaYoneticisi.projeBasvurulariniGetir().stream()
            .filter(b -> b.getKullaniciAdi().equals(aktifKullanici.getKullaniciAdi()) && "Onaylandı".equals(b.getDurum()))
            .count();
        add(createCard("Onaylanan Proje",    String.valueOf(onayProj),
            new Color(225, 255, 235)));
        long yakProj = DosyaYoneticisi.projeleriGetir().stream()
            .filter(p -> {
                try {
                    LocalDate t = LocalDate.parse(p.getTeslimTarihi(), DF);
                    LocalDate today = LocalDate.now();
                    return !t.isBefore(today) && !t.isAfter(today.plusDays(3));
                } catch (Exception e) {
                    return false;
                }
            }).count();
        add(createCard("Yaklaşan Teslim",   String.valueOf(yakProj),
            new Color(255, 235, 235)));

        
        add(createCard("Toplam Burs",        String.valueOf(DosyaYoneticisi.burslariGetir().size()),
            new Color(245, 240, 255)));
        long basvBurs = DosyaYoneticisi.bursBasvurulariniGetir().stream()
            .filter(b -> b.getKullaniciAdi().equals(aktifKullanici.getKullaniciAdi()))
            .count();
        add(createCard("Başvurduğu Burs",     String.valueOf(basvBurs),
            new Color(255, 240, 245)));
        long onayBurs = DosyaYoneticisi.bursBasvurulariniGetir().stream()
            .filter(b -> b.getKullaniciAdi().equals(aktifKullanici.getKullaniciAdi()) && "Onaylandı".equals(b.getDurum()))
            .count();
        add(createCard("Onaylanan Burs",     String.valueOf(onayBurs),
            new Color(235, 255, 240)));
        long yakBurs = DosyaYoneticisi.burslariGetir().stream()
            .filter(b -> {
                try {
                    LocalDate d = LocalDate.parse(b.getDeadline(), DF);
                    return !d.isBefore(LocalDate.now()) && !d.isAfter(LocalDate.now().plusDays(3));
                } catch (Exception e) {
                    return false;
                }
            }).count();
        add(createCard("Yaklaşan Burs",      String.valueOf(yakBurs),
            new Color(255, 245, 230)));

        
        add(createCard("Toplam Etkinlik",    String.valueOf(DosyaYoneticisi.etkinlikleriGetir().size()),
            new Color(225, 255, 255)));
        long basvEtk = DosyaYoneticisi.etkinlikBasvurulariniGetir().stream()
            .filter(b -> b.getKullaniciAdi().equals(aktifKullanici.getKullaniciAdi()))
            .count();
        add(createCard("Başvurduğu Etkinlik",String.valueOf(basvEtk),
            new Color(255, 255, 230)));
        long onayEtk = DosyaYoneticisi.etkinlikBasvurulariniGetir().stream()
            .filter(b -> b.getKullaniciAdi().equals(aktifKullanici.getKullaniciAdi()) && "Onaylandı".equals(b.getDurum()))
            .count();
        add(createCard("Onaylanan Etkinlik", String.valueOf(onayEtk),
            new Color(235, 240, 255)));
        long yakEtk = DosyaYoneticisi.etkinlikleriGetir().stream()
            .filter(e -> {
                try {
                    LocalDate d = LocalDate.parse(e.getTarih(), DF);
                    return !d.isBefore(LocalDate.now()) && !d.isAfter(LocalDate.now().plusDays(3));
                } catch (Exception ex) {
                    return false;
                }
            }).count();
        add(createCard("Yaklaşan Etkinlik",   String.valueOf(yakEtk),
            new Color(245, 255, 235)));
    }

    private JPanel createCard(String title, String value, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(lblValue.getFont().deriveFont(Font.PLAIN, 24f));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }
}