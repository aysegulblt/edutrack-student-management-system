package com.edutrack.arayuz;

import com.edutrack.model.Etkinlik;
import com.edutrack.model.Kullanici;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
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


public class EtkinlikOlusturPaneli extends AbstractPanel {

    private final Kullanici kullanici;
    private JTextField tfBaslik;
    private JTextArea  taAciklama;
    private JTextField tfAdres;
    private JFormattedTextField ftfTarih;
    private JSpinner    spTarih;
    private JTextField tfKontenjan;
    private JButton    btnKaydet;
    private JLabel     lblError;

    
    private static final DateTimeFormatter UI_FMT  = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public EtkinlikOlusturPaneli(Kullanici kullanici) {
        super("Etkinlik Oluştur");
        this.kullanici = kullanici;
        initComponents();
        attachValidation();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(UIConstants.BACKGROUND_COLOR);

        Font lblFont   = UIManager.getFont("Label.font").deriveFont(13f);
        Font fieldFont = UIManager.getFont("TextField.font").deriveFont(12f);
        Insets m = (Insets) UIManager.get("Button.margin");

        
        lblError = new JLabel(" ", SwingConstants.LEFT);
        lblError.setForeground(Color.RED);
        lblError.setFont(lblFont);
        lblError.setBounds(180, 335, 400, 20);
        add(lblError);

        
        add(label("Etkinlik Başlığı:", 30, 30, lblFont));
        tfBaslik = textField(180, 30, 300, 30, fieldFont);
        add(tfBaslik);

        
        add(label("Açıklama:", 30, 75, lblFont));
        taAciklama = new JTextArea(4, 20);
        taAciklama.setFont(fieldFont);
        taAciklama.setLineWrap(true);
        taAciklama.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(taAciklama);
        spDesc.setBounds(180, 75, 300, 80);
        add(spDesc);

        
        add(label("Adres:", 30, 170, lblFont));
        tfAdres = textField(180, 170, 300, 30, fieldFont);
        add(tfAdres);

        
        add(label("Tarih (dd-MM-yyyy):", 30, 215, lblFont));
        Date tomorrow = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        SpinnerDateModel model = new SpinnerDateModel(
            tomorrow, tomorrow, null, Calendar.DATE
        );
        spTarih = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spTarih, "dd-MM-yyyy");
        spTarih.setEditor(editor);
        ftfTarih = editor.getTextField();
        ftfTarih.setFont(fieldFont);
        
        ftfTarih.setFormatterFactory(new DefaultFormatterFactory(
            new DateFormatter(new java.text.SimpleDateFormat("dd-MM-yyyy"))
        ));
        ftfTarih.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                try { spTarih.commitEdit(); }
                catch (java.text.ParseException ex) { /* ignore */ }
                validateForm();
            }
        });
        
        ftfTarih.getInputMap().put(KeyStroke.getKeyStroke("UP"),   "increment");
        ftfTarih.getActionMap().put("increment", new AbstractAction(){ public void actionPerformed(ActionEvent e){
            spTarih.setValue(spTarih.getNextValue()); validateForm();
        }});
        ftfTarih.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "decrement");
        ftfTarih.getActionMap().put("decrement", new AbstractAction(){ public void actionPerformed(ActionEvent e){
            spTarih.setValue(spTarih.getPreviousValue()); validateForm();
        }});
        spTarih.setBounds(180, 215, 140, 28);
        add(spTarih);

        
        add(label("Kontenjan:", 30, 260, lblFont));
        tfKontenjan = textField(180, 260, 100, 30, fieldFont);
        add(tfKontenjan);

        
        btnKaydet = new JButton("Etkinliği Kaydet", IconUtil.loadSVG("icons/save.svg", 16, 16));
        btnKaydet.setFont(lblFont.deriveFont(Font.BOLD));
        btnKaydet.setBounds(180, 305, 200, 32);
        btnKaydet.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnKaydet.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        btnKaydet.addActionListener(e -> saveEvent());
        add(btnKaydet);
    }

    private JLabel label(String txt, int x, int y, Font f) {
        JLabel l = new JLabel(txt);
        l.setFont(f);
        l.setBounds(x, y, 150, 25);
        return l;
    }

    private JTextField textField(int x, int y, int w, int h, Font f) {
        JTextField t = new JTextField();
        t.setFont(f);
        t.setBounds(x, y, w, h);
        return t;
    }

    private void attachValidation() {
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { validateForm(); }
            public void removeUpdate(DocumentEvent e)  { validateForm(); }
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        };
        tfBaslik.getDocument().addDocumentListener(dl);
        taAciklama.getDocument().addDocumentListener(dl);
        tfAdres.getDocument().addDocumentListener(dl);
        ftfTarih.getDocument().addDocumentListener(dl);
        tfKontenjan.getDocument().addDocumentListener(dl);
        spTarih.addChangeListener(e -> validateForm());
        validateForm();
    }

    private void validateForm() {
        boolean ok = true;
        lblError.setText(" ");

        
        if (tfBaslik.getText().trim().length() < 5) {
            tfBaslik.setBorder(new LineBorder(Color.RED)); ok = false;
        } else tfBaslik.setBorder(UIManager.getBorder("TextField.border"));

        
        if (taAciklama.getText().trim().length() < 10) {
            taAciklama.setBorder(new LineBorder(Color.RED)); ok = false;
        } else taAciklama.setBorder(UIManager.getBorder("TextArea.border"));

        
        if (tfAdres.getText().trim().isEmpty()) {
            tfAdres.setBorder(new LineBorder(Color.RED)); ok = false;
        } else tfAdres.setBorder(UIManager.getBorder("TextField.border"));

       
        boolean dateOk = false;
        LocalDate due = null;
        try {
            due = LocalDate.parse(ftfTarih.getText().trim(), UI_FMT);
            dateOk = due.isAfter(LocalDate.now());
        } catch (Exception ex) {
            dateOk = false;
        }
        if (!dateOk) {
            ftfTarih.setBorder(new LineBorder(Color.RED)); ok = false;
            lblError.setText("Tarih en az yarın olmalıdır.");
        } else ftfTarih.setBorder(UIManager.getBorder("TextField.border"));

        
        try {
            int k = Integer.parseInt(tfKontenjan.getText().trim());
            if (k <= 0) throw new NumberFormatException();
            tfKontenjan.setBorder(UIManager.getBorder("TextField.border"));
        } catch (Exception ex) {
            tfKontenjan.setBorder(new LineBorder(Color.RED)); ok = false;
        }

        btnKaydet.setEnabled(ok);
        if (!ok && lblError.getText().isBlank()) {
            lblError.setText("Lütfen tüm alanları doğru doldurun.");
        }
    }

    private void saveEvent() {
        if (!btnKaydet.isEnabled()) {
            JOptionPane.showMessageDialog(
                this,
                "Lütfen formu tamamlayın ve geçerli bir tarih seçin.",
                "Hata",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        LocalDate due = LocalDate.parse(ftfTarih.getText().trim(), UI_FMT);
        Etkinlik e = new Etkinlik(
            UUID.randomUUID().toString(),
            tfBaslik.getText().trim(),
            taAciklama.getText().trim() + "\nAdres: " + tfAdres.getText().trim(),
            due.format(ISO_FMT),
            Integer.parseInt(tfKontenjan.getText().trim()),
            kullanici.getKullaniciAdi()
        );
        DosyaYoneticisi.etkinlikEkle(e);
        JOptionPane.showMessageDialog(this,
            "Etkinlik başarıyla oluşturuldu.\nTarih: " + due.format(UI_FMT),
            "Başarılı",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        tfBaslik.setText("");
        taAciklama.setText("");
        tfAdres.setText("");
        spTarih.setValue(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        tfKontenjan.setText("");
        validateForm();
    }

    @Override
    public void refreshData() {
        
    }
}