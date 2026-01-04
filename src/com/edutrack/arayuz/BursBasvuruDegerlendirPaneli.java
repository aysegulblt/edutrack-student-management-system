package com.edutrack.arayuz;

import com.edutrack.model.Burs;
import com.edutrack.model.BursBasvuru;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


public class BursBasvuruDegerlendirPaneli extends AbstractPanel {
    private final Kullanici aktifKullanici;
    private final JTable table;
    private final DefaultTableModel model;
    private final JComboBox<String> cbFilter;
    private final JTextField tfSearch;
    private final List<BursBasvuru> displayed = new ArrayList<>();
    private final Map<String, Burs> bursMap;

    public BursBasvuruDegerlendirPaneli(Kullanici aktifKullanici) {
        super("Burs Başvurularını Değerlendir");
        this.aktifKullanici = aktifKullanici;
        // Bu sponsorun burslarını map'e al
        bursMap = DosyaYoneticisi.burslariGetir().stream()
            .filter(b -> b.getSponsorAdi().equals(aktifKullanici.getKullaniciAdi()))
            .collect(Collectors.toMap(Burs::getId, b -> b));

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(new JLabel("Durum:"));
        cbFilter = new JComboBox<>(new String[]{"Tümü", "Beklemede", "Onaylandı", "Reddedildi"});
        top.add(cbFilter);
        top.add(new JLabel("Öğrenci Ara:"));
        tfSearch = new JTextField(15);
        top.add(tfSearch);
        JButton btnExport = new JButton("CSV Dışa Aktar", IconUtil.loadSVG("icons/export.svg", 16, 16));
        top.add(btnExport);
        add(top, BorderLayout.NORTH);

       
        String[] cols = {"Burs", "Öğrenci", "Durum", "Öğrenci Gerekçesi", "Sponsor Yorumu"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                
                return col == 2 || col == 4;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));

        
        TableColumn colDurum = table.getColumnModel().getColumn(2);
        colDurum.setCellEditor(new DefaultCellEditor(
            new JComboBox<>(new String[]{"Beklemede", "Onaylandı", "Reddedildi"})
        ));

        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                String st = (String) tbl.getValueAt(row, 2);
                if (sel) c.setBackground(tbl.getSelectionBackground());
                else if ("Beklemede".equals(st)) c.setBackground(new Color(255, 250, 200));
                else if ("Onaylandı".equals(st)) c.setBackground(new Color(220, 255, 220));
                else c.setBackground(new Color(255, 220, 220));
                return c;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        
        cbFilter.addActionListener(e -> refreshData());
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshData(); }
            public void removeUpdate(DocumentEvent e) { refreshData(); }
            public void changedUpdate(DocumentEvent e) { refreshData(); }
        });
        btnExport.addActionListener(e -> exportCSV());

        
        model.addTableModelListener(e -> {
            if (e.getType() != TableModelEvent.UPDATE) return;
            int r = e.getFirstRow(), c = e.getColumn();
            BursBasvuru bb = displayed.get(r);
            if (c == 2) {
                
                String old = bb.getDurum();
                String nst = (String) model.getValueAt(r, 2);
                if (!nst.equals(old)) {
                    String prompt = nst.equals("Onaylandı")
                        ? "Onay gerekçenizi girin:" : "Reddetme gerekçenizi girin:";
                    String reason = JOptionPane.showInputDialog(this, prompt, bb.getSponsorYorum());
                    if (reason == null) {
                        SwingUtilities.invokeLater(() -> model.setValueAt(old, r, 2));
                        return;
                    }
                    bb.setDurum(nst);
                    bb.setSponsorYorum(reason.trim());
                    model.setValueAt(bb.getSponsorYorum(), r, 4);
                    DosyaYoneticisi.bursBasvuruGuncelle(bb);
                    refreshData();
                }
            } else if (c == 4) {
               
                bb.setSponsorYorum((String) model.getValueAt(r, 4));
                DosyaYoneticisi.bursBasvuruGuncelle(bb);
            }
        });

        
        refreshData();
    }

    @Override
    public void refreshData() {
        model.setRowCount(0);
        displayed.clear();
        String filter = (String) cbFilter.getSelectedItem();
        String kw = tfSearch.getText().trim().toLowerCase();

        
        List<BursBasvuru> apps = DosyaYoneticisi.bursBasvurulariniGetir().stream()
            .filter(b -> bursMap.containsKey(b.getBursId()))
            .collect(Collectors.toList());

        for (BursBasvuru b : apps) {
            if (!"Tümü".equals(filter) && !b.getDurum().equals(filter)) continue;
            if (!kw.isEmpty() && !b.getKullaniciAdi().toLowerCase().contains(kw)) continue;
            displayed.add(b);
            Burs br = bursMap.get(b.getBursId());
            model.addRow(new Object[]{
                br.getBaslik(), b.getKullaniciAdi(), b.getDurum(), b.getGerekce(), b.getSponsorYorum()
            });
        }
    }

    private void exportCSV() {
        JFileChooser ch = new JFileChooser();
        if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ch.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println("Burs,Öğrenci,Durum,Öğrenci Gerekçesi,Sponsor Yorumu");
                for (int i = 0; i < model.getRowCount(); i++) {
                    pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        model.getValueAt(i, 0),
                        model.getValueAt(i, 1),
                        model.getValueAt(i, 2),
                        model.getValueAt(i, 3),
                        model.getValueAt(i, 4)
                    );
                }
                JOptionPane.showMessageDialog(this, "CSV başarıyla kaydedildi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "CSV dışa aktarım hatası: " + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
