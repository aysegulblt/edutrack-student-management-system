package com.edutrack.arayuz;

import com.edutrack.model.Proje;
import com.edutrack.model.ProjeBasvuru;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ProjelerPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JPanel projeListePanel;
    private final JComboBox<String> filterCombo;
    private final JComboBox<String> sortCombo;
    private final JTextField tfSearch;

    public ProjelerPaneli(Kullanici aktifKullanici) {
        super("Mevcut Projeler");
        this.aktifKullanici = aktifKullanici;
        setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        ctrl.setOpaque(false);

        tfSearch = new JTextField(12);
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshData(); }
            public void removeUpdate(DocumentEvent e) { refreshData(); }
            public void changedUpdate(DocumentEvent e) { refreshData(); }
        });
        ctrl.add(new JLabel("Ara:"));
        ctrl.add(tfSearch);

        filterCombo = new JComboBox<>(new String[]{
            "Tüm Projeler", "Beklemede Olan", "Onaylanan", "Reddedilen", "Süresi Geçti"
        });
        filterCombo.addActionListener(e -> refreshData());
        ctrl.add(new JLabel("Durum:"));
        ctrl.add(filterCombo);

        sortCombo = new JComboBox<>(new String[]{"Tarih ↑", "Tarih ↓"});
        sortCombo.addActionListener(e -> refreshData());
        ctrl.add(new JLabel("Sırala:"));
        ctrl.add(sortCombo);

        add(ctrl, BorderLayout.NORTH);

        projeListePanel = new JPanel();
        projeListePanel.setLayout(new BoxLayout(projeListePanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(projeListePanel);
        scroll.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                            UIConstants.PADDING, UIConstants.PADDING)
        ));
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        projeListePanel.removeAll();

        List<Proje> projeler;
        List<ProjeBasvuru> basvurular;
        try {
            projeler = DosyaYoneticisi.projeleriGetir();
            basvurular = DosyaYoneticisi.projeBasvurulariniGetir();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Veri yükleme hatası: " + ex.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (projeler.isEmpty()) {
            JLabel lbl = new JLabel("Henüz proje yok.", SwingConstants.CENTER);
            lbl.setBorder(new EmptyBorder(UIConstants.PADDING, 0, 0, 0));
            projeListePanel.add(lbl);
        } else {
            String ka = aktifKullanici.getKullaniciAdi();
            String filter = (String) filterCombo.getSelectedItem();
            String keyword = tfSearch.getText().trim().toLowerCase();
            LocalDate today = LocalDate.now();

            List<Proje> active = new ArrayList<>();
            List<Proje> expired = new ArrayList<>();
            for (Proje p : projeler) {
                LocalDate due;
                try { due = LocalDate.parse(p.getTeslimTarihi()); }
                catch (DateTimeParseException e) { due = today; }
                if (due.isBefore(today)) expired.add(p);
                else active.add(p);
            }

            Comparator<Proje> cmp = Comparator.comparing(p -> {
                try { return LocalDate.parse(p.getTeslimTarihi()); }
                catch (Exception e) { return today; }
            });
            Collections.sort(active, cmp);
            Collections.sort(expired, cmp);
            if ("Tarih ↓".equals(sortCombo.getSelectedItem())) {
                Collections.reverse(active);
                Collections.reverse(expired);
            }

            List<Proje> ordered = new ArrayList<>();
            ordered.addAll(active);
            ordered.addAll(expired);

            for (Proje p : ordered) {
                if (!keyword.isEmpty() &&
                    !p.getBaslik().toLowerCase().contains(keyword) &&
                    !p.getAciklama().toLowerCase().contains(keyword)) continue;

                boolean isExpired;
                long daysLeft;
                LocalDate due;
                try { due = LocalDate.parse(p.getTeslimTarihi()); }
                catch (DateTimeParseException e) { due = today; }
                isExpired = due.isBefore(today);
                daysLeft = ChronoUnit.DAYS.between(today, due);
                boolean approaching = !isExpired && daysLeft <= 3;

                
                ProjeBasvuru pb = basvurular.stream()
                    .filter(b -> b.getKullaniciAdi().equals(ka) && b.getProjeId().equals(p.getId()))
                    .findFirst().orElse(null);
                String status = pb != null ? pb.getDurum() : "-";

                if ("Süresi Geçti".equals(filter) && !isExpired) continue;
                if ("Beklemede Olan".equals(filter) && !"Beklemede".equals(status)) continue;
                if ("Onaylanan".equals(filter) && !"Onaylandı".equals(status)) continue;
                if ("Reddedilen".equals(filter) && !"Reddedildi".equals(status)) continue;

                
                Color bgColor;
                if (isExpired) bgColor = UIConstants.BACKGROUND_COLOR.darker();
                else if ("Onaylandı".equals(status)) bgColor = new Color(230, 255, 230);
                else if ("Reddedildi".equals(status)) bgColor = new Color(255, 235, 235);
                else if ("Beklemede".equals(status)) bgColor = new Color(255, 250, 200);
                else bgColor = Color.WHITE;

                Color borderColor;
                if (approaching) borderColor = Color.ORANGE;
                else if (isExpired) borderColor = Color.DARK_GRAY;
                else if ("Onaylandı".equals(status)) borderColor = new Color(80, 180, 80);
                else if ("Reddedildi".equals(status)) borderColor = new Color(200, 60, 60);
                else if ("Beklemede".equals(status)) borderColor = new Color(220, 180, 20);
                else borderColor = Color.GRAY;

                JPanel kart = new JPanel(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
                kart.setBackground(bgColor);
                kart.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 2),
                    new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                    UIConstants.PADDING, UIConstants.PADDING)
                ));
                kart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

                JLabel lbl = new JLabel(String.format(
                    "<html><b>%s</b><br/>%s<br/>Teslim: %s</html>",
                    p.getBaslik(), p.getAciklama(), p.getTeslimTarihi()
                ));
                kart.add(lbl, BorderLayout.CENTER);

                JPanel sag = new JPanel(); sag.setOpaque(false);
                sag.setLayout(new BoxLayout(sag, BoxLayout.Y_AXIS));
                String msg;
                if (approaching) msg = "Dikkat! Son 3 güne yaklaştı.";
                else if (isExpired) msg = "Üzgünüz, proje süresi doldu.";
                else if ("Onaylandı".equals(status)) msg = "Tebrikler! Başvurunuz onaylandı.";
                else if ("Reddedildi".equals(status)) msg = "Üzgünüz, başvurunuz reddedildi.";
                else if ("Beklemede".equals(status)) msg = "Başvurunuz değerlendiriliyor.";
                else msg = "Henüz başvuru yapılmadı.";
                sag.add(new JLabel(msg));

                if (pb != null && pb.getAciklama() != null && !pb.getAciklama().isBlank()) {
                    sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
                    JLabel lblComment = new JLabel("Yetkili Yorumu: " + pb.getAciklama());
                    lblComment.setFont(UIConstants.BODY_FONT.deriveFont(Font.ITALIC, 12f));
                    lblComment.setForeground(Color.DARK_GRAY);
                    sag.add(lblComment);
                }

                sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
                
                if (!isExpired && pb == null) {
                    JButton btnApply = new JButton("Başvur Et", IconUtil.loadSVG("icons/check.svg", 16, 16));
                    btnApply.setAlignmentX(Component.CENTER_ALIGNMENT);
                    btnApply.addActionListener(e -> apply(p));
                    sag.add(btnApply);
                }
               
                else if (!isExpired && "Beklemede".equals(status)) {
                    JButton btnCancel = new JButton("İptal Et", IconUtil.loadSVG("icons/cross.svg", 16, 16));
                    btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    btnCancel.addActionListener(e -> cancel(p));
                    sag.add(btnCancel);
                }

                kart.add(sag, BorderLayout.EAST);
                projeListePanel.add(kart);
                projeListePanel.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
            }
        }
        projeListePanel.revalidate();
        projeListePanel.repaint();
    }

    private void apply(Proje p) {
        try {
            DosyaYoneticisi.projeBasvuruEkle(
                new ProjeBasvuru(aktifKullanici.getKullaniciAdi(), p.getId(), "Beklemede")
            );
            JOptionPane.showMessageDialog(this, "Başvurunuz kaydedildi.");
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Başvuru hatası: " + e.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancel(Proje p) {
        if (JOptionPane.showConfirmDialog(this,
            "Başvuruyu iptal etmek istediğinize emin misiniz?",
            "Onay", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            DosyaYoneticisi.projeBasvuruSil(
                aktifKullanici.getKullaniciAdi(), p.getId()
            );
            JOptionPane.showMessageDialog(this, "Başvuru iptal edildi.");
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "İptal hatası: " + e.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}
