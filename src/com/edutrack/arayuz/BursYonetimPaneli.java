package com.edutrack.arayuz;

import com.edutrack.model.Burs;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class BursYonetimPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JTextField tfSearch;
    private final JComboBox<String> cbStatus;
    private final JButton btnSort;
    private final JPanel listPanel;
    private boolean sortAsc = true;
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public BursYonetimPaneli(Kullanici aktifKullanici) {
        super("Bursları Yönet");
        this.aktifKullanici = aktifKullanici;

        setLayout(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
        setBorder(new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                  UIConstants.PADDING, UIConstants.PADDING));
        setBackground(UIConstants.BACKGROUND_COLOR);

        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        top.setOpaque(false);

        top.add(new JLabel("Ara:"));
        tfSearch = new JTextField(12);
        tfSearch.getDocument().addDocumentListener(new SimpleDocListener());
        top.add(tfSearch);

        top.add(new JLabel("Durum:"));
        cbStatus = new JComboBox<>(new String[]{"Tümü","Aktif","Süresi Geçti"});
        cbStatus.addActionListener(e -> refreshData());
        top.add(cbStatus);

        btnSort = new JButton(getSortText());
        btnSort.addActionListener(e -> {
            sortAsc = !sortAsc;
            btnSort.setText(getSortText());
            refreshData();
        });
        top.add(btnSort);

        add(top, BorderLayout.NORTH);

       
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                            UIConstants.PADDING, UIConstants.PADDING)
        ));
        add(scroll, BorderLayout.CENTER);

        
        refreshData();
    }

    private String getSortText() {
        return sortAsc ? "Eski → Yeni" : "Yeni → Eski";
    }

    @Override
    public void refreshData() {
        listPanel.removeAll();

        List<Burs> all = DosyaYoneticisi.burslariGetir().stream()
            .filter(b -> aktifKullanici.getKullaniciAdi().equals(b.getSponsorAdi()))
            .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        List<Burs> active = new ArrayList<>(), expired = new ArrayList<>();
        for (Burs b : all) {
            try {
                LocalDate due = LocalDate.parse(b.getDeadline(), ISO_FMT);
                if (due.isBefore(today)) expired.add(b);
                else active.add(b);
            } catch (Exception ex) {
                active.add(b);
            }
        }

        Comparator<Burs> cmp = Comparator.comparing(b -> {
            try { return LocalDate.parse(b.getDeadline(), ISO_FMT); }
            catch (Exception ex) { return today; }
        });
        active.sort(cmp);
        expired.sort(cmp);
        if (!sortAsc) {
            Collections.reverse(active);
            Collections.reverse(expired);
        }
        List<Burs> ordered = new ArrayList<>();
        ordered.addAll(active);
        ordered.addAll(expired);

        String kw = tfSearch.getText().trim().toLowerCase();
        String st = (String) cbStatus.getSelectedItem();

        for (Burs b : ordered) {
            boolean isExpired;
            try {
                isExpired = LocalDate.parse(b.getDeadline(), ISO_FMT).isBefore(today);
            } catch (Exception ex) {
                isExpired = false;
            }
            if ("Aktif".equals(st) && isExpired) continue;
            if ("Süresi Geçti".equals(st) && !isExpired) continue;
            if (!kw.isEmpty()
                && !b.getBaslik().toLowerCase().contains(kw)
                && !b.getAciklama().toLowerCase().contains(kw)
            ) continue;

            JPanel card = new JPanel(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
            boolean approaching = false;
            try {
                long days = ChronoUnit.DAYS.between(today,
                                LocalDate.parse(b.getDeadline(), ISO_FMT));
                approaching = days>=0 && days<=3;
            } catch (Exception ignored) {}

            Color bg = isExpired ? new Color(245,240,240) : new Color(235,255,235);
            card.setBackground(bg);

            Color border = isExpired ? Color.RED
                           : approaching ? Color.ORANGE
                                         : Color.GREEN.darker();
            card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(border, 2),
                new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                UIConstants.PADDING, UIConstants.PADDING)
            ));

            String info = String.format(
                "<html><div style='width:400px;'>"
              + "<b>%s</b><br/>%s<br/>Tutar: %.2f₺ | Teslim: %s<br/><small>ID: %s</small>"
              + "</div></html>",
                b.getBaslik(),
                b.getAciklama(),
                b.getMiktar(),
                b.getDeadline(),
                b.getId()
            );
            JLabel lbl = new JLabel(info);
            lbl.setFont(UIConstants.BODY_FONT);
            card.add(lbl, BorderLayout.CENTER);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
            btns.setOpaque(false);

            JButton btnEdit = new JButton("Düzenle", IconUtil.loadSVG("icons/edit.svg",16,16));
            btnEdit.addActionListener(e -> showEditDialog(b));
            btns.add(btnEdit);

            JButton btnDel = new JButton("Sil", IconUtil.loadSVG("icons/cross.svg",16,16));
            btnDel.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(
                    this,
                    "Bu bursu silmek istediğinize emin misiniz?",
                    "Onay", JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION) {
                    DosyaYoneticisi.bursSil(b.getId());
                    refreshData();
                }
            });
            btns.add(btnDel);

            card.add(btns, BorderLayout.SOUTH);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void showEditDialog(Burs b) {
        JTextField tf1 = new JTextField(b.getBaslik(),20);
        JTextArea  ta1 = new JTextArea(b.getAciklama(),4,20);
        ta1.setLineWrap(true); ta1.setWrapStyleWord(true);
        JTextField tf2 = new JTextField(String.valueOf(b.getMiktar()),10);
        JTextField tf3 = new JTextField(b.getDeadline(),10);

        JPanel p = new JPanel(new GridLayout(0,1,5,5));
        p.add(new JLabel("Başlık:")); p.add(tf1);
        p.add(new JLabel("Açıklama:")); p.add(new JScrollPane(ta1));
        p.add(new JLabel("Tutar:")); p.add(tf2);
        p.add(new JLabel("Teslim (YYYY-MM-DD):")); p.add(tf3);

        int res = JOptionPane.showConfirmDialog(
            this, p, "Burs Düzenle",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (res==JOptionPane.OK_OPTION) {
            try {
                double m = Double.parseDouble(tf2.getText().trim());
                LocalDate.parse(tf3.getText().trim(), ISO_FMT);
                b.setBaslik(tf1.getText().trim());
                b.setAciklama(ta1.getText().trim());
                b.setMiktar(m); b.setDeadline(tf3.getText().trim());
                DosyaYoneticisi.bursGuncelle(b);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Geçersiz giriş: "+ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private class SimpleDocListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) { refreshData(); }
        public void removeUpdate(DocumentEvent e) { refreshData(); }
        public void changedUpdate(DocumentEvent e) { refreshData(); }
    }
}
