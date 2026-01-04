package com.edutrack.arayuz;

import com.edutrack.model.Etkinlik;
import com.edutrack.model.EtkinlikBasvuru;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;


public class EtkinlikSorumlusuGenelBakisPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JLabel lblTotalEvents    = new JLabel();
    private final JLabel lblUpcomingEvents = new JLabel();
    private final JLabel lblPendingApps    = new JLabel();
    private final JLabel lblApprovedApps   = new JLabel();

    public EtkinlikSorumlusuGenelBakisPaneli(Kullanici aktifKullanici) {
        super(""); 
        this.aktifKullanici = aktifKullanici;
        initUI();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshData();
            }
        });
        refreshData();
    }

    private void initUI() {
        setLayout(new GridLayout(2, 2, UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
        setBorder(new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                   UIConstants.PADDING, UIConstants.PADDING));
        setBackground(UIConstants.BACKGROUND_COLOR);

        Font valueFont = UIConstants.HEADER_FONT.deriveFont(Font.PLAIN, 26f);

        lblTotalEvents   .setFont(valueFont);
        lblUpcomingEvents.setFont(valueFont);
        lblPendingApps   .setFont(valueFont);
        lblApprovedApps  .setFont(valueFont);

        add(wrapCard("Toplam Etkinlik", lblTotalEvents, new Color(235, 245, 255)));
        add(wrapCard("Gelecek Etkinlik", lblUpcomingEvents, new Color(225, 255, 235)));
        add(wrapCard("Bekleyen Başvuru", lblPendingApps, new Color(255, 250, 230)));
        add(wrapCard("Onaylanan Başvuru", lblApprovedApps, new Color(245, 230, 235)));
    }

    private JPanel wrapCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                            UIConstants.PADDING, UIConstants.PADDING)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(UIConstants.HEADER_FONT);
        lblTitle.setBorder(new EmptyBorder(0, 0, UIConstants.LAYOUT_GAP, 0));
        card.add(lblTitle, BorderLayout.NORTH);

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(UIConstants.BODY_FONT);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    @Override
    public void refreshData() {
        String user = aktifKullanici.getKullaniciAdi();

        List<Etkinlik> myEvents = DosyaYoneticisi.etkinlikleriGetir().stream()
            .filter(e -> user.equals(e.getOlusturan()))
            .collect(Collectors.toList());

        List<EtkinlikBasvuru> basvurular = DosyaYoneticisi.etkinlikBasvurulariniGetir().stream()
            .filter(b -> myEvents.stream().anyMatch(e -> e.getId().equals(b.getEtkinlikId())))
            .collect(Collectors.toList());

        int total = myEvents.size();
        int upcoming = (int) myEvents.stream()
            .filter(e -> {
                try { return LocalDate.parse(e.getTarih()).isAfter(LocalDate.now()); }
                catch (DateTimeParseException ex) { return false; }
            }).count();
        int pending = (int) basvurular.stream()
            .filter(b -> "Beklemede".equals(b.getDurum())).count();
        int approved = (int) basvurular.stream()
            .filter(b -> "Onaylandı".equals(b.getDurum())).count();

        lblTotalEvents   .setText(String.valueOf(total));
        lblUpcomingEvents.setText(String.valueOf(upcoming));
        lblPendingApps   .setText(String.valueOf(pending));
        lblApprovedApps  .setText(String.valueOf(approved));
    }
}
