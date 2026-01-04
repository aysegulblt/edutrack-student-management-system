package com.edutrack.arayuz;

import com.edutrack.model.Etkinlik;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class EtkinlikYonetPaneli extends AbstractPanel {
    private final Kullanici sorumlu;
    private final JPanel listePanel;
    private final JTextField tfSearch;
    private final JButton btnSort;
    private boolean sortAsc = true;
    private static final DateTimeFormatter RAW_FMT = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter UI_FMT  = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public EtkinlikYonetPaneli(Kullanici sorumlu) {
        super("Etkinliklerimi Yönet");
        this.sorumlu = sorumlu;
        
        setBackground(UIConstants.BACKGROUND_COLOR);
        setLayout(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));

        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        top.setOpaque(false);
        top.add(new JLabel("Ara:"));
        tfSearch = new JTextField(12);
        tfSearch.getDocument().addDocumentListener(new SimpleDocListener());
        top.add(tfSearch);
        btnSort = new JButton(getSortText());
        btnSort.addActionListener(e -> {
            sortAsc = !sortAsc;
            btnSort.setText(getSortText());
            refreshData();
        });
        top.add(btnSort);
        add(top, BorderLayout.NORTH);

        
        listePanel = new JPanel();
        listePanel.setOpaque(false);
        listePanel.setLayout(new BoxLayout(listePanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listePanel);
        scroll.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                            UIConstants.PADDING, UIConstants.PADDING)
        ));
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    private String getSortText() {
        return sortAsc ? "Tarih ↑" : "Tarih ↓";
    }

    @Override
    public void refreshData() {
        listePanel.removeAll();
        LocalDate today = LocalDate.now();

       
        List<Etkinlik> all = DosyaYoneticisi.etkinlikleriGetir().stream()
            .filter(e -> sorumlu.getKullaniciAdi().equals(e.getOlusturan()))
            .collect(Collectors.toList());

        
        all.sort(Comparator.comparing(e -> {
            try { return LocalDate.parse(e.getTarih(), RAW_FMT); }
            catch (Exception ex) { return today; }
        }));
        if (!sortAsc) Collections.reverse(all);

        String kw = tfSearch.getText().trim().toLowerCase();
        for (Etkinlik ev : all) {
            if (!kw.isEmpty() && !ev.getBaslik().toLowerCase().contains(kw)) {
                continue;
            }
            LocalDate date;
            boolean expired;
            long daysLeft = Long.MAX_VALUE;
            try {
                date = LocalDate.parse(ev.getTarih(), RAW_FMT);
                expired = date.isBefore(today);
                daysLeft = ChronoUnit.DAYS.between(today, date);
            } catch (Exception ex) {
                date = today;
                expired = false;
            }
            boolean approaching = !expired && daysLeft <= 3;

            
            JPanel card = new JPanel(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
            card.setBackground(expired ? new Color(240,240,240) : Color.WHITE);

            Color border = expired
                           ? Color.RED
                           : approaching
                             ? Color.ORANGE
                             : new Color(80,180,80);
            card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(border, 2),
                new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING,
                                UIConstants.PADDING, UIConstants.PADDING)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

            
            String info = String.format(
                "<html><b>%s</b> <small>(%s)</small><br/>%s<br/>Tarih: %s</html>",
                ev.getBaslik(),
                ev.getId(),
                ev.getAciklama(),
                date.format(UI_FMT)
            );
            JLabel lbl = new JLabel(info);
            lbl.setFont(UIConstants.BODY_FONT);
            card.add(lbl, BorderLayout.CENTER);

            
            JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
            bp.setOpaque(false);

            JButton btnEdit = new JButton("Düzenle", IconUtil.loadSVG("icons/edit.svg",16,16));
            btnEdit.setEnabled(!expired);
            btnEdit.addActionListener(e -> showEditDialog(ev));
            bp.add(btnEdit);

            JButton btnDel  = new JButton("Sil", IconUtil.loadSVG("icons/cross.svg",16,16));
            btnDel.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(
                    this,
                    "Etkinlik silinsin mi?",
                    "Onay",
                    JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION) {
                    DosyaYoneticisi.etkinlikSil(ev.getId());
                    refreshData();
                }
            });
            bp.add(btnDel);

            card.add(bp, BorderLayout.SOUTH);
            listePanel.add(card);
            listePanel.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
        }

        listePanel.revalidate();
        listePanel.repaint();
    }

    private void showEditDialog(Etkinlik ev) {
        JTextField tfTitle = new JTextField(ev.getBaslik(), 20);
        JTextArea taDesc  = new JTextArea(ev.getAciklama(), 4, 20);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);

        LocalDate curr = LocalDate.parse(ev.getTarih(), RAW_FMT);
        SpinnerDateModel sm = new SpinnerDateModel(
            Date.from(curr.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
            null,
            Calendar.DATE
        );
        JSpinner spDate = new JSpinner(sm);
        spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd"));

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
        gbc.gridx=1; panel.add(new JScrollPane(taDesc), gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; panel.add(new JLabel("Tarih:"), gbc);
        gbc.gridx=1; panel.add(spDate, gbc);

        int res = JOptionPane.showConfirmDialog(
            this, panel, "Etkinliği Düzenle",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (res == JOptionPane.OK_OPTION) {
            String nt = tfTitle.getText().trim();
            String nd = taDesc.getText().trim();
            Date   sd = (Date) spDate.getValue();
            LocalDate ndt = sd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (nt.length()<3 || nd.length()<5 || ndt.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                    this,
                    "Başlık ≥3, açıklama ≥5 karakter ve tarih yarından sonra olmalı.",
                    "Geçersiz Girdi",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            ev.setBaslik(nt);
            ev.setAciklama(nd);
            ev.setTarih(ndt.format(RAW_FMT));
            DosyaYoneticisi.etkinlikGuncelle(ev);
            refreshData();
        }
    }

    private class SimpleDocListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e)  { refreshData(); }
        public void removeUpdate(DocumentEvent e)  { refreshData(); }
        public void changedUpdate(DocumentEvent e) { refreshData(); }
    }
}