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
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class EtkinlikBasvuruDegerlendirPaneli extends AbstractPanel {
    private final Kullanici kullanici;
    private final JPanel basvuruListePanel;
    private final JComboBox<String> cbStatusFilter;
    private final JTextField tfSearch;
    private final JComboBox<String> cbDateSort;
    private final JLabel lblSummary;
    private final DateTimeFormatter DF = DateTimeFormatter.ISO_DATE;

    public EtkinlikBasvuruDegerlendirPaneli(Kullanici kullanici) {
        super("Etkinlik Başvurularını Değerlendir");
        this.kullanici = kullanici;
        setLayout(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
        setBackground(UIConstants.BACKGROUND_COLOR);

        
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.LAYOUT_GAP, 0));
        ctrl.setOpaque(false);

        ctrl.add(new JLabel("Durum:"));
        cbStatusFilter = new JComboBox<>(new String[]{"Hepsi", "Beklemede", "Onaylandı", "Reddedildi"});
        cbStatusFilter.addActionListener(e -> refreshData());
        ctrl.add(cbStatusFilter);

        ctrl.add(new JLabel("Etkinlik Ara:"));
        tfSearch = new JTextField(15);
        tfSearch.getDocument().addDocumentListener(new SimpleDocListener());
        ctrl.add(tfSearch);

        ctrl.add(new JLabel("Tarih ↑/↓:"));
        cbDateSort = new JComboBox<>(new String[]{"↑", "↓"});
        cbDateSort.setToolTipText("Başvuru Tarihi Artan/Azalan");
        cbDateSort.addActionListener(e -> refreshData());
        ctrl.add(cbDateSort);

        JButton btnExport = new JButton("CSV Dışa Aktar", IconUtil.loadSVG("icons/export.svg", 16, 16));
        btnExport.addActionListener(e -> exportCSV());
        ctrl.add(btnExport);

        lblSummary = new JLabel(" ", SwingConstants.RIGHT);
        lblSummary.setFont(UIConstants.BODY_FONT);
        ctrl.add(Box.createHorizontalGlue());
        ctrl.add(lblSummary);

        add(ctrl, BorderLayout.NORTH);

      
        basvuruListePanel = new JPanel();
        basvuruListePanel.setLayout(new BoxLayout(basvuruListePanel, BoxLayout.Y_AXIS));
        basvuruListePanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(basvuruListePanel);
        scroll.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING)
        ));
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        basvuruListePanel.removeAll();

        
        List<Etkinlik> myEvents = DosyaYoneticisi.etkinlikleriGetir().stream()
            .filter(e -> kullanici.getKullaniciAdi().equals(e.getOlusturan()))
            .collect(Collectors.toList());
        Set<String> myIds = myEvents.stream().map(Etkinlik::getId).collect(Collectors.toSet());

    
        List<EtkinlikBasvuru> all = DosyaYoneticisi.etkinlikBasvurulariniGetir().stream()
            .filter(b -> myIds.contains(b.getEtkinlikId()))
            .collect(Collectors.toList());

       
        long cntPend = all.stream().filter(b->"Beklemede".equals(b.getDurum())).count();
        long cntApp  = all.stream().filter(b->"Onaylandı".equals(b.getDurum())).count();
        long cntRej  = all.stream().filter(b->"Reddedildi".equals(b.getDurum())).count();
        lblSummary.setText(String.format("Beklemede: %d   Onaylandı: %d   Reddedildi: %d",
            cntPend, cntApp, cntRej));

        
        String filter = (String) cbStatusFilter.getSelectedItem();
        String kw     = tfSearch.getText().trim().toLowerCase();
        List<EtkinlikBasvuru> filtered = all.stream()
            .filter(b -> "Hepsi".equals(filter) || b.getDurum().equals(filter))
            .filter(b -> {
                String title = myEvents.stream()
                    .filter(e->e.getId().equals(b.getEtkinlikId()))
                    .map(Etkinlik::getBaslik)
                    .findFirst().orElse("");
                return title.toLowerCase().contains(kw);
            })
            .collect(Collectors.toList());

     
        boolean asc = "↑".equals(cbDateSort.getSelectedItem());
        filtered.sort(Comparator.comparing(b -> {
            Etkinlik ev = myEvents.stream()
                .filter(e->e.getId().equals(b.getEtkinlikId()))
                .findFirst().orElse(null);
            try { return LocalDate.parse(ev.getTarih(), DF); }
            catch(Exception ex) { return LocalDate.MAX; }
        }));
        if (!asc) Collections.reverse(filtered);

      
        if (filtered.isEmpty()) {
            JLabel none = new JLabel("Gösterilecek başvuru yok.", SwingConstants.CENTER);
            none.setFont(UIConstants.BODY_FONT);
            none.setBorder(new EmptyBorder(UIConstants.PADDING,0,0,0));
            basvuruListePanel.add(none);
        } else {
            for (EtkinlikBasvuru b : filtered) {
                Etkinlik ev = myEvents.stream()
                    .filter(e->e.getId().equals(b.getEtkinlikId()))
                    .findFirst().orElse(null);

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setOpaque(true);

                Color bg, border;
                switch (b.getDurum()) {
                    case "Onaylandı":  bg = new Color(230,255,230); border = new Color(80,180,80); break;
                    case "Reddedildi": bg = new Color(255,235,235); border = new Color(200,60,60); break;
                    default:           bg = new Color(255,250,200); border = new Color(220,180,20);
                }
                card.setBackground(bg);
                card.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(border, 2),
                    new EmptyBorder(UIConstants.PADDING,UIConstants.PADDING,
                                    UIConstants.PADDING,UIConstants.PADDING)
                ));

                Font keyF = UIConstants.BODY_FONT.deriveFont(Font.BOLD);
                Font valF = UIConstants.BODY_FONT;

                JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
                r1.setOpaque(false);
                r1.add(new JLabel("Öğrenci:") {{ setFont(keyF); }});
                r1.add(new JLabel(b.getKullaniciAdi()) {{ setFont(valF); }});
                card.add(r1);

                JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
                r2.setOpaque(false);
                r2.add(new JLabel("Etkinlik:") {{ setFont(keyF); }});
                String txt = ev!=null ? ev.getBaslik()+" ("+ev.getTarih()+")" : "[bilinmiyor]";
                r2.add(new JLabel(txt) {{ setFont(valF); }});
                card.add(r2);

                if (b.getComments()!=null && !b.getComments().isBlank()) {
                    JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
                    r3.setOpaque(false);
                    r3.add(new JLabel("Yorum:") {{ setFont(keyF); }});
                    r3.add(new JLabel(b.getComments()) {{ setFont(valF); }});
                    card.add(r3);
                }

                card.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
                JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT,UIConstants.LAYOUT_GAP,0));
                bp.setOpaque(false);

                if ("Beklemede".equals(b.getDurum())) {
                    JButton ok = new JButton("Onayla", IconUtil.loadSVG("icons/check.svg",16,16));
                    JButton no = new JButton("Reddet", IconUtil.loadSVG("icons/cross.svg",16,16));
                    ok.addActionListener(e->handleDecision(b,"Onaylandı"));
                    no.addActionListener(e->handleDecision(b,"Reddedildi"));
                    bp.add(ok); bp.add(no);
                } else {
                    JButton edit = new JButton("Durum Değiştir", IconUtil.loadSVG("icons/edit.svg",16,16));
                    edit.addActionListener(e-> {
                        String[] opts = {"Beklemede","Onaylandı","Reddedildi"};
                        String ns = (String) JOptionPane.showInputDialog(
                            this,"Yeni Durum:","Güncelle",JOptionPane.PLAIN_MESSAGE,
                            null,opts,b.getDurum());
                        if (ns!=null) handleDecision(b,ns);
                    });
                    bp.add(edit);
                }

                card.add(bp);
                basvuruListePanel.add(card);
                basvuruListePanel.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP));
            }
        }

        basvuruListePanel.revalidate();
        basvuruListePanel.repaint();
    }

    private void handleDecision(EtkinlikBasvuru b, String status) {
        String comment = JOptionPane.showInputDialog(
            this, "Açıklama (isteğe bağlı):", b.getComments()==null?"":b.getComments()
        );
        b.setDurum(status);
        b.setComments(comment);
        DosyaYoneticisi.etkinlikBasvuruGuncelle(b);
        refreshData();
    }

    private void exportCSV() {
        
        List<Etkinlik> myEvents = DosyaYoneticisi.etkinlikleriGetir().stream()
            .filter(e -> kullanici.getKullaniciAdi().equals(e.getOlusturan()))
            .collect(Collectors.toList());
        Set<String> myIds = myEvents.stream().map(Etkinlik::getId).collect(Collectors.toSet());

        List<EtkinlikBasvuru> all = DosyaYoneticisi.etkinlikBasvurulariniGetir().stream()
            .filter(b -> myIds.contains(b.getEtkinlikId()))
            .collect(Collectors.toList());

        String filter = (String) cbStatusFilter.getSelectedItem();
        String kw     = tfSearch.getText().trim().toLowerCase();

        List<EtkinlikBasvuru> filtered = all.stream()
            .filter(b -> "Hepsi".equals(filter) || b.getDurum().equals(filter))
            .filter(b -> {
                String title = myEvents.stream()
                    .filter(e->e.getId().equals(b.getEtkinlikId()))
                    .map(Etkinlik::getBaslik)
                    .findFirst().orElse("");
                return title.toLowerCase().contains(kw);
            })
            .collect(Collectors.toList());

        boolean asc = "↑".equals(cbDateSort.getSelectedItem());
        filtered.sort(Comparator.comparing(b -> {
            Etkinlik ev = myEvents.stream()
                .filter(e->e.getId().equals(b.getEtkinlikId()))
                .findFirst().orElse(null);
            try { return LocalDate.parse(ev.getTarih(), DF); }
            catch(Exception ex){ return LocalDate.MAX; }
        }));
        if (!asc) Collections.reverse(filtered);

        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try(PrintWriter pw = new PrintWriter(f, "UTF-8")) {
            pw.println("Etkinlik,Öğrenci,Durum,Yorum");
            for (EtkinlikBasvuru b : filtered) {
                String title = myEvents.stream()
                    .filter(e->e.getId().equals(b.getEtkinlikId()))
                    .map(Etkinlik::getBaslik)
                    .findFirst().orElse("");
                String comment = b.getComments()==null?"":b.getComments();
                
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    title, b.getKullaniciAdi(), b.getDurum(), comment.replace("\"","\"\""));
            }
            JOptionPane.showMessageDialog(this, "CSV başarıyla kaydedildi:\n" + f.getAbsolutePath());
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,
                "CSV dışa aktarım hatası: " + ex.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class SimpleDocListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e){ refreshData(); }
        public void removeUpdate(DocumentEvent e){ refreshData(); }
        public void changedUpdate(DocumentEvent e){ refreshData(); }
    }
}