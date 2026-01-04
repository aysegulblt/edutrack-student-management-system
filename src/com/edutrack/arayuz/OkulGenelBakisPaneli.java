package com.edutrack.arayuz;

import com.edutrack.model.Proje;
import com.edutrack.model.ProjeBasvuru;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class OkulGenelBakisPaneli extends JPanel {
    private final Kullanici aktifKullanici;

    private static final int GRID_HGAP = 20;
    private static final int GRID_VGAP = 20;
    private static final Border PANEL_BORDER = new EmptyBorder(20, 20, 20, 20);
    private static final Border CARD_BORDER = new CompoundBorder(
        new LineBorder(UIManager.getColor("Table.gridColor"), 1),
        new EmptyBorder(10, 10, 10, 10)
    );

    private static final Font TITLE_FONT =
        UIManager.getFont("Label.font").deriveFont(Font.BOLD, 16f);
    private static final Font VALUE_FONT =
        UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 28f);

    public OkulGenelBakisPaneli(Kullanici aktifKullanici) {
        this.aktifKullanici = aktifKullanici;

        setLayout(new GridLayout(2, 2, GRID_HGAP, GRID_VGAP));
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(PANEL_BORDER);

        // Pastel renkler
        Color lightBlue  = new Color(220, 230, 255);
        Color lightYellow= new Color(255, 250, 220);
        Color lightGreen = new Color(220, 255, 220);
        Color lightRed   = new Color(255, 230, 230);

        add(createCard("Toplam Proje", this::getTotalProjects, lightBlue));
        add(createCard("Bekleyen Başvuru", this::getPending, lightYellow));
        add(createCard("Onaylanan Başvuru", this::getApproved, lightGreen));
        add(createCard("Reddedilen Başvuru", this::getRejected, lightRed));
    }

    
    public void refreshData() {
        for (Component comp : getComponents()) {
            if (!(comp instanceof JPanel)) continue;
            JPanel card = (JPanel) comp;
            Object lbl  = card.getClientProperty("valueLabel");
            Object sup  = card.getClientProperty("valueSupplier");
            if (lbl instanceof JLabel && sup instanceof Supplier) {
                ((JLabel) lbl).setText(((Supplier<String>) sup).get());
            }
        }
    }

    private JPanel createCard(String title, Supplier<String> valueSupplier, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(CARD_BORDER);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(TITLE_FONT);

        JLabel lblValue = new JLabel(valueSupplier.get(), SwingConstants.CENTER);
        lblValue.setFont(VALUE_FONT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        
        card.putClientProperty("valueLabel", lblValue);
        card.putClientProperty("valueSupplier", valueSupplier);
        return card;
    }

    private String getTotalProjects() {
        String uname = aktifKullanici.getKullaniciAdi();
        long count = DosyaYoneticisi.projeleriGetir().stream()
            .filter(p -> uname.equals(p.getOlusturan()))
            .count();
        return String.valueOf(count);
    }

    private String getPending() {
        String uname = aktifKullanici.getKullaniciAdi();
        List<String> ids = DosyaYoneticisi.projeleriGetir().stream()
            .filter(p -> uname.equals(p.getOlusturan()))
            .map(Proje::getId)
            .collect(Collectors.toList());
        long count = DosyaYoneticisi.projeBasvurulariniGetir().stream()
            .filter(b -> ids.contains(b.getProjeId()) && "Beklemede".equals(b.getDurum()))
            .count();
        return String.valueOf(count);
    }

    private String getApproved() {
        String uname = aktifKullanici.getKullaniciAdi();
        List<String> ids = DosyaYoneticisi.projeleriGetir().stream()
            .filter(p -> uname.equals(p.getOlusturan()))
            .map(Proje::getId)
            .collect(Collectors.toList());
        long count = DosyaYoneticisi.projeBasvurulariniGetir().stream()
            .filter(b -> ids.contains(b.getProjeId()) && "Onaylandı".equals(b.getDurum()))
            .count();
        return String.valueOf(count);
    }

    private String getRejected() {
        String uname = aktifKullanici.getKullaniciAdi();
        List<String> ids = DosyaYoneticisi.projeleriGetir().stream()
            .filter(p -> uname.equals(p.getOlusturan()))
            .map(Proje::getId)
            .collect(Collectors.toList());
        long count = DosyaYoneticisi.projeBasvurulariniGetir().stream()
            .filter(b -> ids.contains(b.getProjeId()) && "Reddedildi".equals(b.getDurum()))
            .count();
        return String.valueOf(count);
    }
}

