package com.edutrack.arayuz;

import com.edutrack.model.Proje;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class ProjeleriYonetPaneli extends AbstractPanel {
    private final JPanel projeListePanel;
    private final JTextField tfSearch;
    private final JButton btnSort;
    private final Kullanici aktifKullanici;
    private boolean sortAsc = true;
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter UI_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ProjeleriYonetPaneli(Kullanici aktifKullanici) {
        super("Projeleri Yönet");
        this.aktifKullanici = aktifKullanici;
        setLayout(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));

        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        top.setOpaque(false);

        top.add(new JLabel("Ara:"));
        tfSearch = new JTextField(12);
        tfSearch.getDocument().addDocumentListener(new SimpleDocListener());
        top.add(tfSearch);

        top.add(new JLabel("Sırala:"));
        btnSort = new JButton(getSortText());
        btnSort.addActionListener(e -> {
            sortAsc = !sortAsc;
            btnSort.setText(getSortText());
            refreshData();
        });
        top.add(btnSort);

        add(top, BorderLayout.NORTH);

        
        projeListePanel = new JPanel();
        projeListePanel.setLayout(new BoxLayout(projeListePanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(projeListePanel);
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
        projeListePanel.removeAll();

        
        List<Proje> all = DosyaYoneticisi.projeleriGetir().stream()
            .filter(p -> aktifKullanici.getKullaniciAdi().equals(p.getOlusturan()))
            .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        List<Proje> active = new ArrayList<>(), expired = new ArrayList<>();

        
        for (Proje p : all) {
            try {
                LocalDate due = LocalDate.parse(p.getTeslimTarihi(), fmt);
                if (due.isBefore(today)) expired.add(p);
                else active.add(p);
            } catch (Exception e) {
                active.add(p);
            }
        }

        
        Comparator<Proje> cmp = Comparator.comparing(p -> {
            try { return LocalDate.parse(p.getTeslimTarihi(), fmt); }
            catch (Exception ex) { return today; }
        });
        active.sort(cmp);
        expired.sort(cmp);
        if (!sortAsc) {
            Collections.reverse(active);
            Collections.reverse(expired);
        }

        
        List<Proje> ordered = new ArrayList<>(active);
        ordered.addAll(expired);

        String keyword = tfSearch.getText().trim().toLowerCase();

       
        for (Proje p : ordered) {
            if (!keyword.isEmpty() &&
                !p.getBaslik().toLowerCase().contains(keyword) &&
                !p.getAciklama().toLowerCase().contains(keyword)) {
                continue;
            }

            LocalDate due;
            boolean isExpired;
            long daysLeft = Long.MAX_VALUE;
            try {
                due = LocalDate.parse(p.getTeslimTarihi(), fmt);
                isExpired = due.isBefore(today);
                daysLeft = ChronoUnit.DAYS.between(today, due);
            } catch (Exception ex) {
                isExpired = false;
            }
            boolean approaching = !isExpired && daysLeft <= 3;

           
            JPanel card = new JPanel(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
            Color bg = isExpired
                       ? new Color(240,240,240)
                       : new Color(235,255,235);
            card.setBackground(bg);

            Color border = isExpired
                           ? Color.RED
                           : approaching
                             ? Color.ORANGE
                             : new Color(80,180,80);
            card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(border, 2),
                new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                UIConstants.PADDING, UIConstants.PADDING)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

            
            String info = String.format(
                "<html><b>%s</b> <small>(%s)</small><br/>%s<br/>Teslim: %s</html>",
                p.getBaslik(),
                p.getId(),
                p.getAciklama(),
                LocalDate.parse(p.getTeslimTarihi(), fmt).format(UI_FMT)
            );
            JLabel lbl = new JLabel(info);
            lbl.setFont(UIConstants.BODY_FONT);
            card.add(lbl, BorderLayout.CENTER);

            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
            btnPanel.setOpaque(false);

            
            JButton btnEdit = new JButton("Düzenle");
            btnEdit.addActionListener(e -> showEditDialog(p));
            btnPanel.add(btnEdit);

            
            JButton btnDelete = new JButton("Sil");
            btnDelete.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(
                        this,
                        "Bu projeyi silmek istediğinize emin misiniz?",
                        "Onay",
                        JOptionPane.YES_NO_OPTION
                    ) == JOptionPane.YES_OPTION) {
                    DosyaYoneticisi.projeSil(p.getId());
                    refreshData();
                }
            });
            btnPanel.add(btnDelete);

            
            if (!isExpired) {
                card.add(btnPanel, BorderLayout.SOUTH);
            }

            projeListePanel.add(card);
            projeListePanel.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
        }

        projeListePanel.revalidate();
        projeListePanel.repaint();
    }

    private void showEditDialog(Proje p) {
        JTextField tfTitle = new JTextField(p.getBaslik(), 20);
        JTextArea taDesc  = new JTextArea(p.getAciklama(), 4, 20);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);

        
        LocalDate curr = LocalDate.parse(p.getTeslimTarihi(), fmt);
        SpinnerDateModel dateModel = new SpinnerDateModel(
            Date.from(curr.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
            null,
            Calendar.DATE
        );
        JSpinner spDate = new JSpinner(dateModel);
        JSpinner.DateEditor de = new JSpinner.DateEditor(spDate, "dd.MM.yyyy");
        spDate.setEditor(de);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int y=0;

        gbc.gridx=0; gbc.gridy=y; panel.add(new JLabel("Başlık:"), gbc);
        gbc.gridx=1; panel.add(tfTitle, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; panel.add(new JLabel("Açıklama:"), gbc);
        gbc.gridx=1;
        panel.add(new JScrollPane(taDesc), gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; panel.add(new JLabel("Teslim Tarihi:"), gbc);
        gbc.gridx=1; panel.add(spDate, gbc);

        int res = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Projeyi Düzenle",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (res == JOptionPane.OK_OPTION) {
            String newTitle = tfTitle.getText().trim();
            String newDesc  = taDesc.getText().trim();
            Date sel = (Date) spDate.getValue();
            LocalDate newDue = sel.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

           
            if (newTitle.length()<5 || newDesc.length()<10 || !newDue.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                    this,
                    "Başlık en az 5, açıklama en az 10 karakter olmalı\n" +
                    "ve teslim tarihi en az yarından sonraya ayarlanmalı.",
                    "Geçersiz Girdi",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            
            p.setBaslik(newTitle);
            p.setAciklama(newDesc);
            p.setTeslimTarihi(newDue.format(fmt));
            DosyaYoneticisi.projeGuncelle(p);
            refreshData();
        }
    }

    private class SimpleDocListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e)  { refreshData(); }
        public void removeUpdate(DocumentEvent e)  { refreshData(); }
        public void changedUpdate(DocumentEvent e) { refreshData(); }
    }
}