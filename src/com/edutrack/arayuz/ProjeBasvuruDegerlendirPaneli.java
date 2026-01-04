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
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class ProjeBasvuruDegerlendirPaneli extends AbstractPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<String> cbFilter;
    private final JTextField tfSearch;
    private final JComboBox<String> cbSort;
    private final List<ProjeBasvuru> displayedList = new ArrayList<>();
    private final Map<String, Proje> projeMap;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public ProjeBasvuruDegerlendirPaneli(Kullanici aktifKullanici) {
        super("Proje Başvuruları");

        
        projeMap = DosyaYoneticisi.projeleriGetir().stream()
            .filter(p -> p.getOlusturan().equals(aktifKullanici.getKullaniciAdi()))
            .collect(Collectors.toMap(Proje::getId, p -> p));

        setLayout(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));

        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.LAYOUT_GAP, 0));
        top.setOpaque(false);

        top.add(new JLabel("Durum:"));
        cbFilter = new JComboBox<>(new String[]{"Tümü", "Beklemede", "Onaylandı", "Reddedildi"});
        cbFilter.addActionListener(e -> refreshData());
        top.add(cbFilter);

        top.add(new JLabel("Ara:"));
        tfSearch = new JTextField(15);
        tfSearch.getDocument().addDocumentListener(new SimpleDocListener());
        top.add(tfSearch);

        top.add(new JLabel("Sırala (Teslim ↑/↓):"));
        cbSort = new JComboBox<>(new String[]{"↑", "↓"});
        cbSort.setToolTipText("Artan / Azalan");
        cbSort.addActionListener(e -> refreshData());
        top.add(cbSort);

        JButton btnExport = new JButton("CSV Dışa Aktar", IconUtil.loadSVG("icons/export.svg", 16, 16));
        btnExport.addActionListener(e -> exportCSV());
        top.add(btnExport);

        add(top, BorderLayout.NORTH);

        
        model = new DefaultTableModel(new String[]{
            "Proje", "Öğrenci", "Durum", "Yorum (Sorumlu)"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) {
               
                return col == 2 || col == 3;
            }
        };
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        
        TableColumn statusCol = table.getColumnModel().getColumn(2);
        statusCol.setCellEditor(new DefaultCellEditor(
            new JComboBox<>(new String[]{"Beklemede", "Onaylandı", "Reddedildi"})
        ));

        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                JTable tbl, Object val, boolean sel, boolean focus, int row, int col
            ) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                String status = (String) tbl.getValueAt(row, 2);
                Color bg = switch (status) {
                    case "Beklemede" -> new Color(255, 250, 200);
                    case "Onaylandı"  -> new Color(220, 255, 220);
                    case "Reddedildi" -> new Color(255, 220, 220);
                    default           -> Color.WHITE;
                };
                c.setBackground(bg);
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        
        model.addTableModelListener(e -> {
            if (e.getType()!=TableModelEvent.UPDATE) return;
            int row = e.getFirstRow();
            int col = e.getColumn();
            ProjeBasvuru pb = displayedList.get(row);

            if (col == 2) {
                String oldStatus = pb.getDurum();
                String newStatus = (String) model.getValueAt(row, 2);
                if (!Objects.equals(oldStatus, newStatus)) {
                    String prompt = switch (newStatus) {
                        case "Onaylandı"  -> "Onay gerekçenizi girin:";
                        case "Reddedildi" -> "Reddetme gerekçenizi girin:";
                        default           -> null;
                    };
                    if (prompt != null) {
                        String comment = JOptionPane.showInputDialog(this, prompt, pb.getAciklama());
                        if (comment == null) {
                            
                            SwingUtilities.invokeLater(() -> model.setValueAt(oldStatus, row, 2));
                            return;
                        }
                        pb.setAciklama(comment.trim());
                        model.setValueAt(pb.getAciklama(), row, 3);
                    }
                    pb.setDurum(newStatus);
                    DosyaYoneticisi.projeBasvuruGuncelle(pb);
                    refreshData();
                }
            }
            else if (col == 3) {
                String newComment = (String) model.getValueAt(row, 3);
                pb.setAciklama(newComment);
                DosyaYoneticisi.projeBasvuruGuncelle(pb);
            }
        });

        
        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0);
        displayedList.clear();

        
        List<ProjeBasvuru> apps = DosyaYoneticisi.projeBasvurulariniGetir().stream()
            .filter(b -> projeMap.containsKey(b.getProjeId()))
            .collect(Collectors.toList());

        
        String filter = (String) cbFilter.getSelectedItem();
        String kw     = tfSearch.getText().trim().toLowerCase();
        apps = apps.stream().filter(b -> {
            if (!"Tümü".equals(filter) && !b.getDurum().equals(filter)) return false;
            String title = projeMap.get(b.getProjeId()).getBaslik().toLowerCase();
            String user  = b.getKullaniciAdi().toLowerCase();
            return title.contains(kw) || user.contains(kw);
        }).collect(Collectors.toList());

        
        boolean asc = "↑".equals(cbSort.getSelectedItem());
        apps.sort(Comparator.comparing(b -> {
            String dateStr = projeMap.get(b.getProjeId()).getTeslimTarihi();
            try { return LocalDate.parse(dateStr, DATE_FMT); }
            catch (Exception ex) { return LocalDate.MAX; }
        }));
        if (!asc) Collections.reverse(apps);

        
        for (ProjeBasvuru b : apps) {
            displayedList.add(b);
            model.addRow(new Object[]{
                projeMap.get(b.getProjeId()).getBaslik(),
                b.getKullaniciAdi(),
                b.getDurum(),
                b.getAciklama()
            });
        }
    }

    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("CSV Olarak Kaydet");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println("Proje,Öğrenci,Durum,Yorum");
                for (int i = 0; i < model.getRowCount(); i++) {
                    pw.printf("\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        model.getValueAt(i, 0),
                        model.getValueAt(i, 1),
                        model.getValueAt(i, 2),
                        model.getValueAt(i, 3)
                    );
                }
                JOptionPane.showMessageDialog(this, "CSV başarıyla kaydedildi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "CSV dışa aktarım hatası: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private class SimpleDocListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e)  { refreshData(); }
        public void removeUpdate(DocumentEvent e)  { refreshData(); }
        public void changedUpdate(DocumentEvent e) { refreshData(); }
    }
}