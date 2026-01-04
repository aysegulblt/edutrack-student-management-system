package com.edutrack.arayuz;

import com.edutrack.model.Burs;
import com.edutrack.model.BursBasvuru;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.stream.Collectors;


public class DesteklenenOgrencilerPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JPanel listePaneli;
    private final JTextField tfSearch;
    private final JLabel lblTotal;

    public DesteklenenOgrencilerPaneli(Kullanici aktifKullanici) {
        super("Desteklenen Öğrenciler");
        this.aktifKullanici = aktifKullanici;

        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        top.setOpaque(false);
        top.add(new JLabel("Öğrenci Ara:"));
        tfSearch = new JTextField(15);
        top.add(tfSearch);
        lblTotal = new JLabel("Toplam: 0");
        lblTotal.setFont(UIConstants.BODY_FONT);
        top.add(lblTotal);
        add(top, BorderLayout.NORTH);

        
        listePaneli = new JPanel();
        listePaneli.setLayout(new BoxLayout(listePaneli, BoxLayout.Y_AXIS));
        listePaneli.setBackground(UIConstants.BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(listePaneli);
        scrollPane.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                            UIConstants.PADDING, UIConstants.PADDING)
        ));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshData(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshData(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshData(); }
        });

        
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                refreshData();
            }
        });

       
        refreshData();
    }

    @Override
    public void refreshData() {
        listePaneli.removeAll();

        
        List<Burs> tumBurslar = DosyaYoneticisi.burslariGetir().stream()
            .filter(b -> b.getSponsorAdi().equals(aktifKullanici.getKullaniciAdi()))
            .collect(Collectors.toList());

        
        List<BursBasvuru> basvurular = DosyaYoneticisi.bursBasvurulariniGetir().stream()
            .filter(bb -> "Onaylandı".equals(bb.getDurum())
                       && tumBurslar.stream()
                           .anyMatch(b -> b.getId().equals(bb.getBursId())))
            .collect(Collectors.toList());

        
        String keyword = tfSearch.getText().trim().toLowerCase();
        List<BursBasvuru> filtered = basvurular.stream()
            .filter(bb -> bb.getKullaniciAdi().toLowerCase().contains(keyword))
            .collect(Collectors.toList());

        
        lblTotal.setText("Toplam: " + filtered.size());

        if (filtered.isEmpty()) {
            JLabel none = new JLabel("Henüz desteklenen öğrenci yok.", SwingConstants.CENTER);
            none.setBorder(new EmptyBorder(UIConstants.PADDING, 0, 0, 0));
            listePaneli.add(none);
        } else {
            int idx = 0;
            for (BursBasvuru bb : filtered) {
                Burs burs = tumBurslar.stream()
                    .filter(b -> b.getId().equals(bb.getBursId()))
                    .findFirst().orElse(null);
                if (burs == null) continue;

                JPanel kart = new JPanel(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
                
                Color bg = (idx++ % 2 == 0)
                    ? new Color(240, 255, 240)
                    : new Color(255, 250, 240);
                kart.setBackground(bg);
                kart.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                    UIConstants.PADDING, UIConstants.PADDING)
                ));
                kart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                String html = String.format(
                    "<html><b>Öğrenci:</b> %s<br/>" +
                    "<b>Burs:</b> %s<br/>" +
                    "<b>Tutar:</b> %.2f₺<br/>" +
                    "<b>Son Başvuru:</b> %s</html>",
                    bb.getKullaniciAdi(),
                    burs.getBaslik(),
                    burs.getMiktar(),
                    burs.getDeadline()
                );

                JLabel lbl = new JLabel(html);
                lbl.setFont(UIConstants.BODY_FONT);
                kart.add(lbl, BorderLayout.CENTER);

                listePaneli.add(kart);
                listePaneli.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
            }
        }

        listePaneli.revalidate();
        listePaneli.repaint();
    }
}