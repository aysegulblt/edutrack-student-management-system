package com.edutrack.arayuz;

import com.edutrack.model.Etkinlik;
import com.edutrack.model.EtkinlikBasvuru;
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


public class EtkinliklerPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JPanel etkinlikListePanel;
    private final JTextField tfSearch;
    private final JComboBox<String> cbFilter;
    private final JComboBox<String> cbSort;
    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_DATE;

    public EtkinliklerPaneli(Kullanici aktifKullanici) {
        super("Etkinlik Listesi");
        this.aktifKullanici = aktifKullanici;

        tfSearch = new JTextField(12);
        cbFilter = new JComboBox<>(new String[]{
            "Tüm Etkinlikler", "Beklemede", "Onaylandı", "Reddedildi", "Süresi Geçti"
        });
        cbSort   = new JComboBox<>(new String[]{ "Tarih ↑", "Tarih ↓" });

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        ctrl.setOpaque(false);
        ctrl.add(new JLabel("Ara:"));
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { refreshData(); }
            public void removeUpdate(DocumentEvent e)  { refreshData(); }
            public void changedUpdate(DocumentEvent e) { refreshData(); }
        });
        ctrl.add(tfSearch);

        ctrl.add(new JLabel("Durum:"));
        cbFilter.addActionListener(e -> refreshData());
        ctrl.add(cbFilter);

        ctrl.add(new JLabel("Sırala:"));
        cbSort.addActionListener(e -> refreshData());
        ctrl.add(cbSort);

        add(ctrl, BorderLayout.NORTH);

        etkinlikListePanel = new JPanel();
        etkinlikListePanel.setLayout(new BoxLayout(etkinlikListePanel, BoxLayout.Y_AXIS));
        etkinlikListePanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(etkinlikListePanel);
        scroll.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(
                UIConstants.PADDING, UIConstants.PADDING,
                UIConstants.PADDING, UIConstants.PADDING
            )
        ));
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        etkinlikListePanel.removeAll();

        List<EtkinlikBasvuru> basvurular = DosyaYoneticisi.etkinlikBasvurulariniGetir();
        List<Etkinlik>          tum       = DosyaYoneticisi.etkinlikleriGetir();

        String ka      = aktifKullanici.getKullaniciAdi();
        String kw      = tfSearch.getText().trim().toLowerCase();
        String filter  = (String) cbFilter.getSelectedItem();
        boolean asc    = "Tarih ↑".equals(cbSort.getSelectedItem());
        LocalDate today= LocalDate.now();

        List<Etkinlik> filtered = tum.stream()
            .filter(ev -> {
                if (!kw.isEmpty() && !ev.getBaslik().toLowerCase().contains(kw)) 
                    return false;

                Optional<EtkinlikBasvuru> ob = basvurular.stream()
                    .filter(b -> b.getKullaniciAdi().equals(ka)
                              && b.getEtkinlikId().equals(ev.getId()))
                    .findFirst();

                LocalDate date;
                try { date = LocalDate.parse(ev.getTarih(), DF); }
                catch (Exception ex) { date = today; }
                boolean expired = date.isBefore(today);

                switch (filter) {
                    case "Beklemede":     return ob.isPresent() && "Beklemede".equals(ob.get().getDurum());
                    case "Onaylandı":     return ob.isPresent() && "Onaylandı".equals(ob.get().getDurum());
                    case "Reddedildi":    return ob.isPresent() && "Reddedildi".equals(ob.get().getDurum());
                    case "Süresi Geçti":  return expired;
                    default:              return true;
                }
            })
            .collect(Collectors.toList());

       
        filtered.sort((a,b) -> {
            LocalDate da, db;
            try { da = LocalDate.parse(a.getTarih(), DF); } catch (Exception e){ da = today; }
            try { db = LocalDate.parse(b.getTarih(), DF); } catch (Exception e){ db = today; }
            return asc ? da.compareTo(db) : db.compareTo(da);
        });

        if (filtered.isEmpty()) {
            JLabel none = new JLabel("Gösterilecek etkinlik yok.", SwingConstants.CENTER);
            none.setBorder(new EmptyBorder(0,0,UIConstants.PADDING,0));
            etkinlikListePanel.add(none);
        } else {
            for (Etkinlik ev : filtered) {
                addEventCard(ev, basvurular, ka, today);
            }
        }

        etkinlikListePanel.revalidate();
        etkinlikListePanel.repaint();
    }

    private void addEventCard(Etkinlik ev,
                              List<EtkinlikBasvuru> basvurular,
                              String ka,
                              LocalDate today) {
        EtkinlikBasvuru mevc = basvurular.stream()
            .filter(b -> b.getKullaniciAdi().equals(ka)
                      && b.getEtkinlikId().equals(ev.getId()))
            .findFirst().orElse(null);

        String status = mevc == null ? "-" : mevc.getDurum();
        String comment = mevc == null ? "" : mevc.getComments();

        LocalDate date;
        try { date = LocalDate.parse(ev.getTarih(), DF); }
        catch (Exception ex) { date = today; }
        boolean expired     = date.isBefore(today);
        long    daysLeft    = ChronoUnit.DAYS.between(today, date);
        boolean approaching = !expired && daysLeft <= 3;

        JPanel kart = new JPanel(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
        kart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // arka plan
        Color bg = expired   ? new Color(220,220,220)
                 : "Onaylandı".equals(status) ? new Color(220,255,220)
                 : "Reddedildi".equals(status)? new Color(255,220,220)
                 : "Beklemede".equals(status) ? new Color(255,250,200)
                 : Color.WHITE;
        kart.setBackground(bg);

       
        Color bc = expired   ? Color.DARK_GRAY
                 : approaching ? Color.ORANGE 
                 : Color.GRAY;
        kart.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(bc,2),
            new EmptyBorder(UIConstants.PADDING,UIConstants.PADDING,
                            UIConstants.PADDING,UIConstants.PADDING)
        ));

        JLabel info = new JLabel(String.format(
            "<html><b>%s</b><br/>%s<br/>Tarih: %s | Kont: %d</html>",
            ev.getBaslik(), ev.getAciklama(), ev.getTarih(), ev.getKontenjan()
        ));
        info.setFont(UIConstants.BODY_FONT);
        kart.add(info, BorderLayout.CENTER);

        JPanel sag = new JPanel();
        sag.setLayout(new BoxLayout(sag, BoxLayout.Y_AXIS));
        sag.setOpaque(false);

        String msg;
        if (expired)                     msg = "Üzgünüz, süresi geçti.";
        else if ("Onaylandı".equals(status)) msg = "Katılım onaylandı.";
        else if ("Beklemede".equals(status)) msg = "Değerlendiriliyor.";
        else if ("Reddedildi".equals(status))msg = "Katılım reddedildi.";
        else                            msg = "Başvuru yok.";
        sag.add(new JLabel(msg));

        if (approaching) {
            sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
            JLabel warn = new JLabel("Son 3 güne yaklaştı!");
            warn.setForeground(Color.ORANGE.darker());
            warn.setFont(UIConstants.BODY_FONT.deriveFont(Font.BOLD));
            sag.add(warn);
        }

        if (!comment.isBlank()) {
            sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
            JLabel org = new JLabel("Yorum: " + comment);
            org.setFont(UIConstants.BODY_FONT.deriveFont(Font.ITALIC, 12f));
            org.setForeground(Color.DARK_GRAY);
            sag.add(org);
        }

        sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
        sag.add(new JLabel("Durum: " + (expired ? "Geçti" : status)));

        if (!expired) {
            if (mevc == null) {
                sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
                JButton btn = new JButton("Katıl", IconUtil.loadSVG("icons/check.svg",16,16));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setIconTextGap(UIConstants.ICON_TEXT_GAP);
                btn.addActionListener(e -> {
                    DosyaYoneticisi.etkinlikBasvuruEkle(
                      new EtkinlikBasvuru(ka, ev.getId(), "Beklemede")
                    );
                    refreshData();
                });
                sag.add(btn);
            } else if ("Beklemede".equals(status)) {
                sag.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
                JButton btn = new JButton("İptal", IconUtil.loadSVG("icons/cross.svg",16,16));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setIconTextGap(UIConstants.ICON_TEXT_GAP);
                btn.addActionListener(e -> {
                    DosyaYoneticisi.etkinlikBasvuruSil(ka, ev.getId());
                    refreshData();
                });
                sag.add(btn);
            }
        }

        kart.add(sag, BorderLayout.EAST);
        etkinlikListePanel.add(kart);
        etkinlikListePanel.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
    }
}