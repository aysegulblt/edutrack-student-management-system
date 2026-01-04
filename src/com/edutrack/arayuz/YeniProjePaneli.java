package com.edutrack.arayuz;

import com.edutrack.model.Kullanici;
import com.edutrack.model.Proje;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class YeniProjePaneli extends JPanel {
    private final Kullanici aktifKullanici;
    private final JTextField tfBaslik   = new JTextField();
    private final JTextArea  taAciklama = new JTextArea(4, 20);
    private final JSpinner    spTeslimTarihi;
    private final JButton     btnKaydet     = new JButton("Projeyi Kaydet");

    
    private static final DateTimeFormatter UI_FMT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    private static final DateTimeFormatter ISO_FMT =
        DateTimeFormatter.ISO_LOCAL_DATE;

    public YeniProjePaneli(Kullanici aktifKullanici) {
        this.aktifKullanici = aktifKullanici;

        Insets m = (Insets) UIManager.get("Button.margin");
        setPreferredSize(new Dimension(800, 400));
        setBackground(UIManager.getColor("Panel.background"));
        setBorder(BorderFactory.createEmptyBorder(
            m.top, m.left, m.bottom, m.right
        ));
        setLayout(new GridBagLayout());

        
        Date tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        SpinnerDateModel model = new SpinnerDateModel(
            tomorrow,    
            tomorrow,    
            null,        
            Calendar.DATE 
        );
        spTeslimTarihi = new JSpinner(model);

        
        JSpinner.DateEditor editor =
            new JSpinner.DateEditor(spTeslimTarihi, "dd-MM-yyyy");
        spTeslimTarihi.setEditor(editor);

        
        JFormattedTextField ftf = editor.getTextField();
        ftf.setFormatterFactory(new DefaultFormatterFactory(
            new DateFormatter(new java.text.SimpleDateFormat("dd-MM-yyyy"))
        ));
        ftf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                
                try { spTeslimTarihi.commitEdit(); }
                catch (java.text.ParseException ex) { /* invalid text */ }
                validateForm();
            }
        });

        
        ftf.getInputMap().put(KeyStroke.getKeyStroke("UP"), "inc");
        ftf.getActionMap().put("inc", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                spTeslimTarihi.setValue(spTeslimTarihi.getNextValue());
                validateForm();
            }
        });
        ftf.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "dec");
        ftf.getActionMap().put("dec", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                spTeslimTarihi.setValue(spTeslimTarihi.getPreviousValue());
                validateForm();
            }
        });

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = m;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        Font lblFont   = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 14f);
        Font fieldFont = UIManager.getFont("TextField.font").deriveFont(13f);

        int row = 0;
        
        gbc.gridx=0; gbc.gridy=row; gbc.gridwidth=1;
        JLabel lbl1 = new JLabel("Proje Başlığı:", SwingConstants.LEFT);
        lbl1.setFont(lblFont);
        add(lbl1, gbc);

        gbc.gridx=1; gbc.gridwidth=2;
        tfBaslik.setFont(fieldFont);
        tfBaslik.setPreferredSize(new Dimension(200, 30));
        add(tfBaslik, gbc);

        
        row++;
        gbc.gridy=row; gbc.gridx=0; gbc.gridwidth=1;
        JLabel lbl2 = new JLabel("Açıklama:", SwingConstants.LEFT);
        lbl2.setFont(lblFont);
        add(lbl2, gbc);

        gbc.gridx=1; gbc.gridwidth=2;
        taAciklama.setFont(fieldFont);
        taAciklama.setLineWrap(true);
        taAciklama.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(taAciklama);
        spDesc.setPreferredSize(new Dimension(400, 100));
        add(spDesc, gbc);

        
        row++;
        gbc.gridy=row; gbc.gridx=0; gbc.gridwidth=1;
        JLabel lbl3 = new JLabel("Teslim Tarihi:", SwingConstants.LEFT);
        lbl3.setFont(lblFont);
        add(lbl3, gbc);

        gbc.gridx=1; gbc.gridwidth=2;
        spTeslimTarihi.setFont(fieldFont);
        add(spTeslimTarihi, gbc);

        
        row++;
        gbc.gridy=row; gbc.gridx=1; gbc.gridwidth=1;
        btnKaydet.setFont(lblFont);
        btnKaydet.setIcon(IconUtil.loadSVG("icons/save.svg", 16, 16));
        btnKaydet.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnKaydet.setIconTextGap(6);
        btnKaydet.addActionListener(e -> saveProject());
        add(btnKaydet, gbc);

        
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { validateForm(); }
            public void removeUpdate(DocumentEvent e)  { validateForm(); }
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        };
        tfBaslik.getDocument().addDocumentListener(dl);
        taAciklama.getDocument().addDocumentListener(dl);
        ftf.getDocument().addDocumentListener(dl);
        spTeslimTarihi.addChangeListener(e -> validateForm());

        validateForm();
    }

    private void validateForm() {
        String title = tfBaslik.getText().trim();
        String desc  = taAciklama.getText().trim();

        
        boolean okText = title.length() >= 5 && desc.length() >= 10;

        
        LocalDate due = LocalDate.MIN;
        boolean okDate = false;
        JSpinner.DateEditor ed = (JSpinner.DateEditor) spTeslimTarihi.getEditor();
        String txt = ed.getTextField().getText().trim();
        try {
            due = LocalDate.parse(txt, UI_FMT);
            okDate = due.isAfter(LocalDate.now());
        } catch (Exception ex) {
            okDate = false;
        }

        
        boolean valid = okText && okDate;
        btnKaydet.setEnabled(valid);

        
        tfBaslik.setBorder(okText
            ? UIManager.getBorder("TextField.border")
            : BorderFactory.createLineBorder(Color.RED));
        taAciklama.setBorder(okText
            ? UIManager.getBorder("TextArea.border")
            : BorderFactory.createLineBorder(Color.RED));
        ed.getTextField().setBorder(okDate
            ? UIManager.getBorder("TextField.border")
            : BorderFactory.createLineBorder(Color.RED));
    }

    private void saveProject() {
        
        validateForm();
        if (!btnKaydet.isEnabled()) {
            JOptionPane.showMessageDialog(
                this,
                "Lütfen tüm alanları doğru doldurun ve teslim tarihini yarından sonraya ayarlayın.",
                "Hata",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String title = tfBaslik.getText().trim();
        String desc  = taAciklama.getText().trim();
        JSpinner.DateEditor ed = (JSpinner.DateEditor) spTeslimTarihi.getEditor();
        LocalDate due = LocalDate.parse(ed.getTextField().getText().trim(), UI_FMT);

        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Proje p = new Proje(
            id,
            title,
            desc,
            due.format(ISO_FMT),
            aktifKullanici.getKullaniciAdi()
        );
        DosyaYoneticisi.projeEkle(p);

        JOptionPane.showMessageDialog(
            this,
            "Proje kaydedildi!\nID: " + id +
            "\nTeslim Tarihi: " + due.format(UI_FMT),
            "Başarılı",
            JOptionPane.INFORMATION_MESSAGE
        );

        
        tfBaslik.setText("");
        taAciklama.setText("");
        spTeslimTarihi.setValue(
            Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
        );
        validateForm();
    }
}