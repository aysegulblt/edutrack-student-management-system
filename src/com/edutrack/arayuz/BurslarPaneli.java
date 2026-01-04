package com.edutrack.arayuz;

import com.edutrack.model.Burs;
import com.edutrack.model.BursBasvuru;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;


public class BurslarPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JPanel bursListePanel;
    private final JComboBox<String> filterCombo;
    private final JComboBox<String> sortCombo;
    private final JTextField tfSearch = new JTextField(12);
    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_DATE;

    public BurslarPaneli(Kullanici aktifKullanici) {
        super("Mevcut Burslar");
        this.aktifKullanici = aktifKullanici;

       
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        ctrl.setOpaque(false);
        ctrl.add(new JLabel("Ara:"));
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshData(); }
            public void removeUpdate(DocumentEvent e) { refreshData(); }
            public void changedUpdate(DocumentEvent e) { refreshData(); }
        });
        ctrl.add(tfSearch);

        ctrl.add(new JLabel("Durum:"));
        filterCombo = new JComboBox<>(new String[]{"Tüm Burslar", "Beklemede", "Onaylanan", "Reddedildi", "Süresi Geçti"});
        filterCombo.addActionListener(e -> refreshData());
        ctrl.add(filterCombo);

        ctrl.add(new JLabel("Sırala:"));
        sortCombo = new JComboBox<>(new String[]{"Tutar ↑", "Tutar ↓", "Tarih ↑", "Tarih ↓"});
        sortCombo.addActionListener(e -> refreshData());
        ctrl.add(sortCombo);

        add(ctrl, BorderLayout.NORTH);

       
        bursListePanel = new JPanel();
        bursListePanel.setLayout(new BoxLayout(bursListePanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(bursListePanel);
        scroll.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING)
        ));
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        bursListePanel.removeAll();
        List<Burs> burslar = DosyaYoneticisi.burslariGetir();
        List<BursBasvuru> basvurular = DosyaYoneticisi.bursBasvurulariniGetir();

        if (burslar.isEmpty()) {
            JLabel none = new JLabel("Henüz tanımlı burs bulunmuyor.", SwingConstants.CENTER);
            none.setBorder(new EmptyBorder(0, 0, UIConstants.PADDING, 0));
            bursListePanel.add(none);
        } else {
            String ka = aktifKullanici.getKullaniciAdi();
            String filter = (String) filterCombo.getSelectedItem();
            String keyword = tfSearch.getText().trim().toLowerCase();
            LocalDate today = LocalDate.now();

            
            List<Burs> filtered = burslar.stream()
                .filter(b -> {
                    if (!keyword.isEmpty() &&
                        !b.getBaslik().toLowerCase().contains(keyword) &&
                        !b.getSponsorAdi().toLowerCase().contains(keyword)) return false;
                    Optional<BursBasvuru> ob = basvurular.stream()
                        .filter(x -> x.getKullaniciAdi().equals(ka) && x.getBursId().equals(b.getId()))
                        .findFirst();
                    LocalDate dl;
                    try { dl = LocalDate.parse(b.getDeadline(), DF); } catch (Exception ex) { dl = today; }
                    boolean isExpired = dl.isBefore(today);
                    switch (filter) {
                        case "Beklemede": return ob.isPresent() && "Beklemede".equals(ob.get().getDurum());
                        case "Onaylanan": return ob.isPresent() && "Onaylandı".equals(ob.get().getDurum());
                        case "Reddedildi": return ob.isPresent() && "Reddedildi".equals(ob.get().getDurum());
                        case "Süresi Geçti": return isExpired;
                        default: return true;
                    }
                }).collect(Collectors.toList());

            
            Comparator<Burs> cmpAmt = Comparator.comparing(Burs::getMiktar);
            Comparator<Burs> cmpDateAsc = Comparator.comparing(b -> {
                try { return LocalDate.parse(b.getDeadline(), DF); } catch (Exception ex) { return LocalDate.MIN; }
            });
            String sort = (String) sortCombo.getSelectedItem();
            Comparator<Burs> cmp;
            switch (sort) {
                case "Tutar ↑": cmp = cmpAmt; break;
                case "Tutar ↓": cmp = cmpAmt.reversed(); break;
                case "Tarih ↑": cmp = cmpDateAsc; break;
                case "Tarih ↓": cmp = cmpDateAsc.reversed(); break;
                default: cmp = cmpAmt;
            }
            filtered.sort(cmp);

           
            for (Burs b : filtered) {
                Optional<BursBasvuru> ob = basvurular.stream()
                    .filter(x -> x.getKullaniciAdi().equals(ka) && x.getBursId().equals(b.getId()))
                    .findFirst();
                String durum = ob.map(BursBasvuru::getDurum).orElse("-");
                
                String sponsorComment = ob
                    .map(BursBasvuru::getSponsorYorum)
                    .filter(c -> c != null && !c.isBlank())
                    .orElse("");

                LocalDate dl;
                try { dl = LocalDate.parse(b.getDeadline(), DF); } catch (Exception ex) { dl = today; }
                long daysLeft = ChronoUnit.DAYS.between(today, dl);
                boolean isExpired = dl.isBefore(today);
                boolean approaching = !isExpired && daysLeft <= 3;

                JPanel kart = new JPanel(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
                Color bg = isExpired ? new Color(230,230,230)
                          : "Onaylandı".equals(durum) ? new Color(220,255,220)
                          : "Beklemede".equals(durum) ? new Color(255,250,200)
                          : "Reddedildi".equals(durum) ? new Color(255,235,235)
                          : Color.WHITE;
                kart.setBackground(bg);
                Color borderColor = approaching ? Color.ORANGE : Color.LIGHT_GRAY;
                if (isExpired) borderColor = Color.DARK_GRAY;
                kart.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING)
                ));
                kart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

                JLabel lbl = new JLabel(String.format(
                    "<html><b>%s</b><br/>%s<br/>Sponsor: %s | Tutar: %.2f₺ | Son Tarih: %s</html>",
                    b.getBaslik(), b.getAciklama(), b.getSponsorAdi(), b.getMiktar(), b.getDeadline()
                ));
                kart.add(lbl, BorderLayout.CENTER);

                JPanel sag = new JPanel(); sag.setOpaque(false);
                sag.setLayout(new BoxLayout(sag, BoxLayout.Y_AXIS));
                String msg;
                if (isExpired) msg = "Üzgünüz, burs süresi geçti.";
                else if ("Onaylandı".equals(durum)) msg = "Tebrikler! Başvurunuz onaylandı.";
                else if ("Beklemede".equals(durum)) msg = "Başvurunuz değerlendiriliyor.";
                else if ("Reddedildi".equals(durum)) msg = "Üzgünüz, başvurunuz reddedildi.";
                else msg = "Henüz başvuru yapılmadı.";
                sag.add(new JLabel(msg));
                
                if (!sponsorComment.isBlank()) {
                    sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
                    JLabel lblC = new JLabel("Sponsor Yorumu: " + sponsorComment);
                    lblC.setFont(UIConstants.BODY_FONT.deriveFont(Font.ITALIC, 12f));
                    lblC.setForeground(Color.DARK_GRAY);
                    sag.add(lblC);
                }

                sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
                if (!isExpired) {
                    if (!ob.isPresent()) {
                        JButton btn = new JButton("Başvur", IconUtil.loadSVG("icons/plus-circle.svg",16,16));
                        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                        btn.addActionListener(e -> apply(b));
                        sag.add(btn);
                    } else if ("Beklemede".equals(durum)) {
                        JButton btnE = new JButton("Gerekçenizi Düzenle", IconUtil.loadSVG("icons/edit.svg",16,16));
                        btnE.setAlignmentX(Component.CENTER_ALIGNMENT);
                        btnE.addActionListener(e -> editReason(ob.get()));
                        sag.add(btnE);
                        sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
                        JButton btnC = new JButton("İptal Et", IconUtil.loadSVG("icons/cross.svg",16,16));
                        btnC.setAlignmentX(Component.CENTER_ALIGNMENT);
                        btnC.addActionListener(e -> cancel(b));
                        sag.add(btnC);
                    }
                }
                kart.add(sag, BorderLayout.EAST);
                bursListePanel.add(kart);
                bursListePanel.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
            }
        }
        bursListePanel.revalidate();
        bursListePanel.repaint();
    }

    private void apply(Burs b) {
        String g = JOptionPane.showInputDialog(this, "Başvuru gerekçenizi girin:");
        if (g != null && !g.isBlank()) {
            DosyaYoneticisi.bursBasvuruEkle(
                new BursBasvuru(aktifKullanici.getKullaniciAdi(), b.getId(), "Beklemede", g)
            );
            refreshData();
        }
    }

    private void editReason(BursBasvuru bb) {
        String y = JOptionPane.showInputDialog(this, "Gerekçenizi güncelleyin:", bb.getGerekce());
        if (y != null && !y.isBlank()) {
            bb.setGerekce(y);
            DosyaYoneticisi.bursBasvuruGuncelle(bb);
            refreshData();
        }
    }

    private void cancel(Burs b) {
        if (JOptionPane.showConfirmDialog(this, "Başvuruyu iptal etmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DosyaYoneticisi.bursBasvuruSil(aktifKullanici.getKullaniciAdi(), b.getId());
            refreshData();
        }
    }
}
