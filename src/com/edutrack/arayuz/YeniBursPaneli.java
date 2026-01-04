package com.edutrack.arayuz;

import com.edutrack.model.Burs;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DateFormatter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

public class YeniBursPaneli extends JPanel {
    private final Kullanici aktifKullanici;
    private final JTextField tfBaslik     = new JTextField();
    private final JTextArea  taAciklama   = new JTextArea(4, 20);
    private final JSpinner    spMiktar     = new JSpinner(new SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 50));
    private final JSpinner    spSonBasvuru;
    private final JLabel      lblBursKodu  = new JLabel();
    private final JLabel      lblError     = new JLabel(" ");
    private final JButton     btnKaydet    = new JButton("Bursu Kaydet");

    private static final DateTimeFormatter UI_FMT  = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public YeniBursPaneli(Kullanici aktifKullanici) {
        this.aktifKullanici = aktifKullanici;
        setLayout(new GridBagLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);

        Insets m = (Insets)UIManager.get("TextField.margin");
        Border defaultFieldBorder = UIManager.getBorder("TextField.border");

       
        Date tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        SpinnerDateModel dateModel = new SpinnerDateModel(tomorrow, tomorrow, null, Calendar.DATE);
        spSonBasvuru = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spSonBasvuru, "dd-MM-yyyy");
        spSonBasvuru.setEditor(dateEditor);

        
        JFormattedTextField ftf = dateEditor.getTextField();
        ftf.setFormatterFactory(new DefaultFormatterFactory(
            new DateFormatter(new java.text.SimpleDateFormat("dd-MM-yyyy"))
        ));
        ftf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                try { spSonBasvuru.commitEdit(); } catch (Exception ignored) {}
                validateForm();
            }
        });

        
        ftf.getInputMap().put(KeyStroke.getKeyStroke("UP"), "inc");
        ftf.getActionMap().put("inc", new AbstractAction(){
            @Override public void actionPerformed(ActionEvent e){
                spSonBasvuru.setValue(spSonBasvuru.getNextValue());
                validateForm();
            }
        });
        ftf.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "dec");
        ftf.getActionMap().put("dec", new AbstractAction(){
            @Override public void actionPerformed(ActionEvent e){
                spSonBasvuru.setValue(spSonBasvuru.getPreviousValue());
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
        JLabel l1 = new JLabel("Burs Başlığı:"); l1.setFont(lblFont);
        add(l1, gbc);
        gbc.gridx=1; gbc.gridwidth=2;
        tfBaslik.setFont(fieldFont);
        add(tfBaslik, gbc);

        
        row++; gbc.gridy=row; gbc.gridx=0; gbc.gridwidth=1;
        JLabel l2 = new JLabel("Açıklama:"); l2.setFont(lblFont);
        add(l2, gbc);
        gbc.gridx=1; gbc.gridwidth=2;
        taAciklama.setFont(fieldFont);
        taAciklama.setLineWrap(true);
        taAciklama.setWrapStyleWord(true);
        add(new JScrollPane(taAciklama), gbc);

       
        row++; gbc.gridy=row; gbc.gridx=0; gbc.gridwidth=1;
        JLabel l3 = new JLabel("Miktar (₺):"); l3.setFont(lblFont);
        add(l3, gbc);
        gbc.gridx=1; gbc.gridwidth=2;
        spMiktar.setFont(fieldFont);
        add(spMiktar, gbc);

        
        row++; gbc.gridy=row; gbc.gridx=0; gbc.gridwidth=1;
        JLabel l4 = new JLabel("Son Başvuru Tarihi:"); l4.setFont(lblFont);
        add(l4, gbc);
        gbc.gridx=1; gbc.gridwidth=2;
        spSonBasvuru.setFont(fieldFont);
        add(spSonBasvuru, gbc);

        
        row++; gbc.gridy=row; gbc.gridx=0; gbc.gridwidth=3;
        lblBursKodu.setFont(fieldFont.deriveFont(Font.ITALIC, 12f));
        add(lblBursKodu, gbc);

        
        row++; gbc.gridy=row; gbc.gridx=0; gbc.gridwidth=3;
        lblError.setForeground(Color.RED);
        add(lblError, gbc);

       
        row++; gbc.gridy=row; gbc.gridx=1; gbc.gridwidth=1;
        btnKaydet.setFont(lblFont);
        btnKaydet.setIcon(IconUtil.loadSVG("icons/save.svg", 16, 16));
        btnKaydet.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnKaydet.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        btnKaydet.addActionListener(e -> saveBurs());
        add(btnKaydet, gbc);

        
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { validateForm(); }
            public void removeUpdate(DocumentEvent e)  { validateForm(); }
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        };
        tfBaslik.getDocument().addDocumentListener(dl);
        taAciklama.getDocument().addDocumentListener(dl);
        spMiktar.addChangeListener(e -> validateForm());
        spSonBasvuru.addChangeListener(e -> validateForm());

        clearForm();
    }

    
    private void clearForm() {
        tfBaslik.setText("");
        taAciklama.setText("");
        spMiktar.setValue(100);
        spSonBasvuru.setValue(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        lblError.setText(" ");
        lblBursKodu.setText("Burs Kodu: " + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        validateForm();
    }


    private void validateForm() {
        String title = tfBaslik.getText().trim();
        String desc  = taAciklama.getText().trim();
        boolean okText = title.length() >= 5 && desc.length() >= 10;
        boolean okMiktar = ((int)spMiktar.getValue()) > 0;

        LocalDate due = null;
        boolean okDate = false;
        try {
            due = ((Date)spSonBasvuru.getValue())
                    .toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate();
            okDate = due.isAfter(LocalDate.now());
        } catch (Exception ignored) {}

        boolean valid = okText && okMiktar && okDate;
        btnKaydet.setEnabled(valid);

        Border red = BorderFactory.createLineBorder(Color.RED,1);
        tfBaslik.setBorder(okText   ? UIManager.getBorder("TextField.border") : red);
        taAciklama.setBorder(okText  ? UIManager.getBorder("TextArea.border")  : red);
        spMiktar.setBorder(okMiktar  ? UIManager.getBorder("Spinner.border")   : red);
        spSonBasvuru.setBorder(okDate? UIManager.getBorder("TextField.border") : red);

        lblError.setText(valid ? " " : "Lütfen tüm alanları doldurun ve tarihi kontrol edin.");
    }

    
    private void saveBurs() {
        validateForm();
        if (!btnKaydet.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                "Form geçersiz. Lütfen düzeltin.",
                "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id       = UUID.randomUUID().toString();
        String baslik   = tfBaslik.getText().trim();
        String aciklama = taAciklama.getText().trim();
        int miktar      = (int)spMiktar.getValue();
        LocalDate due   = ((Date)spSonBasvuru.getValue())
                             .toInstant().atZone(ZoneId.systemDefault())
                             .toLocalDate();

        Burs b = new Burs(id, baslik, aciklama, miktar,
                         aktifKullanici.getKullaniciAdi(),
                         due.format(ISO_FMT));
        DosyaYoneticisi.bursEkle(b);

        JOptionPane.showMessageDialog(this,
            "Burs başarıyla kaydedildi!\nKod: " + id +
            "\nSon Başvuru: " + due.format(UI_FMT),
            "Başarılı", JOptionPane.INFORMATION_MESSAGE);

        clearForm();
    }
}