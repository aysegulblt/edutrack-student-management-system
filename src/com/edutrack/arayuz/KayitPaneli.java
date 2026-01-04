package com.edutrack.arayuz;

import com.edutrack.AnaPencere;
import com.edutrack.model.Kullanici;
import com.edutrack.util.Sabitler;
import com.edutrack.util.PanelTipi;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.regex.Pattern;


public class KayitPaneli extends JPanel {
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$");

    private final Font labelFont = new Font("SansSerif", Font.BOLD, 13);
    private final Font fieldFont = new Font("SansSerif", Font.PLAIN, 12);

    private final JTextField tfAd = new JTextField();
    private final JTextField tfSoyad = new JTextField();
    private final JTextField tfKullaniciAdi = new JTextField();
    private final JPasswordField pfParola = new JPasswordField();
    private char defaultEcho;
    private final JCheckBox cbShowPassword = new JCheckBox("Şifreyi Göster");

    private final JComboBox<String> cbRol = new JComboBox<>(new String[]{
        "Öğrenci", "Okul Yetkilisi", "Sponsor", "Etkinlik Sorumlusu"
    });
    private final JLabel lblError = new JLabel(" ");

    
    private final JProgressBar strengthBar = new JProgressBar(0, 5);
    private final JLabel lblStrength = new JLabel("Güç: ");

    private final JButton btnKaydet = new JButton("Kayıt Ol");
    private final JButton btnGeri   = new JButton("Geri");

    public KayitPaneli(AnaPencere pencere) {
        setPreferredSize(new Dimension(900, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 245, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                clearFields();
            }
        });

        
        URL logoUrl = getClass().getResource("/logo.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image img = icon.getImage().getScaledInstance(280, 190, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(lblLogo);
        }
        add(Box.createVerticalStrut(10));

        
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int y = 0;

       
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        JLabel lbl1 = new JLabel("Ad:"); lbl1.setFont(labelFont);
        form.add(lbl1, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        tfAd.setFont(fieldFont);
        form.add(tfAd, gbc);

        
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        JLabel lbl2 = new JLabel("Soyad:"); lbl2.setFont(labelFont);
        form.add(lbl2, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        tfSoyad.setFont(fieldFont);
        form.add(tfSoyad, gbc);

        
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        JLabel lbl3 = new JLabel("Kullanıcı Adı:"); lbl3.setFont(labelFont);
        form.add(lbl3, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        tfKullaniciAdi.setFont(fieldFont);
        form.add(tfKullaniciAdi, gbc);

       
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        JLabel lbl4 = new JLabel("Parola:"); lbl4.setFont(labelFont);
        form.add(lbl4, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        pfParola.setFont(fieldFont);
        form.add(pfParola, gbc);
        defaultEcho = pfParola.getEchoChar();
        gbc.gridx = 2; gbc.weightx = 0;
        form.add(cbShowPassword, gbc);

        
        y++;
        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1;
        form.add(strengthBar, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        form.add(lblStrength, gbc);

        
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
        JLabel lbl5 = new JLabel("Rol:"); lbl5.setFont(labelFont);
        form.add(lbl5, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cbRol, gbc);

        
        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        lblError.setFont(fieldFont);
        lblError.setForeground(Color.RED);
        form.add(lblError, gbc);
        gbc.gridwidth = 1;

        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        btnKaydet.setPreferredSize(new Dimension(120, 32));
        btnKaydet.setIcon(IconUtil.loadSVG("icons/save.svg", 16, 16));
        btnKaydet.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnKaydet.setIconTextGap(6);
        btnGeri.setPreferredSize(new Dimension(120, 32));
        btnGeri.setIcon(IconUtil.loadSVG("icons/arrow-left.svg", 16, 16));
        btnGeri.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnGeri.setIconTextGap(6);
        btnPanel.add(btnKaydet);
        btnPanel.add(btnGeri);

        y++;
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);
        gbc.gridwidth = 1;

        add(form);
        add(Box.createVerticalStrut(10));

        
        btnKaydet.addActionListener(e -> onRegister(pencere));
        btnGeri.addActionListener(e -> pencere.goster(PanelTipi.GIRIS_PANELI.getKey()));
        cbShowPassword.addActionListener(e ->
            pfParola.setEchoChar(cbShowPassword.isSelected() ? (char)0 : defaultEcho)
        );
        pfParola.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
        });
        KeyAdapter enter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) btnKaydet.doClick();
            }
        };
        tfAd.addKeyListener(enter);
        tfSoyad.addKeyListener(enter);
        tfKullaniciAdi.addKeyListener(enter);
        pfParola.addKeyListener(enter);
    }

    private void updateStrength() {
        String s = new String(pfParola.getPassword());
        int score = 0;
        if (s.length() >= 8) score++;
        if (s.matches(".*[A-Z].*")) score++;
        if (s.matches(".*[a-z].*")) score++;
        if (s.matches(".*\\d.*")) score++;
        if (s.matches(".*\\W.*")) score++;
        strengthBar.setValue(score);
        lblStrength.setText("Güç: " + (score < 3 ? "Zayıf" : score < 5 ? "Orta" : "Güçlü"));
    }

    private void clearFields() {
        lblError.setText(" ");
        tfAd.setText("");
        tfSoyad.setText("");
        tfKullaniciAdi.setText("");
        pfParola.setText("");
        cbShowPassword.setSelected(false);
        cbRol.setSelectedIndex(0);
        tfAd.setBorder(UIManager.getBorder("TextField.border"));
        tfSoyad.setBorder(UIManager.getBorder("TextField.border"));
        tfKullaniciAdi.setBorder(UIManager.getBorder("TextField.border"));
        pfParola.setBorder(UIManager.getBorder("PasswordField.border"));
        btnKaydet.setEnabled(true);
    }

    private void onRegister(AnaPencere pencere) {
        btnKaydet.setEnabled(false);
        lblError.setText(" ");

        String ad  = tfAd.getText().trim();
        String soy = tfSoyad.getText().trim();
        String ka  = tfKullaniciAdi.getText().trim();
        String pw  = new String(pfParola.getPassword());
        String rol = cbRol.getSelectedItem().toString();

        if (ad.isEmpty() || soy.isEmpty() || ka.isEmpty() || pw.isEmpty()) {
            lblError.setText(Sabitler.MSG_BOS_ALAN_VAR);
            if (ad.isEmpty()) tfAd.setBorder(new LineBorder(Color.RED));
            if (soy.isEmpty()) tfSoyad.setBorder(new LineBorder(Color.RED));
            if (ka.isEmpty()) tfKullaniciAdi.setBorder(new LineBorder(Color.RED));
            if (pw.isEmpty()) pfParola.setBorder(new LineBorder(Color.RED));
            btnKaydet.setEnabled(true);
            return;
        }
        for (Kullanici k : DosyaYoneticisi.kullanicilariGetir()) {
            if (k.getKullaniciAdi().equalsIgnoreCase(ka)) {
                lblError.setText("Bu kullanıcı adı zaten alınmış.");
                tfKullaniciAdi.setBorder(new LineBorder(Color.RED));
                btnKaydet.setEnabled(true);
                return;
            }
        }
        if (!PASSWORD_PATTERN.matcher(pw).matches()) {
            JOptionPane.showMessageDialog(
                this,
                "Şifre en az 8 karakter, büyük/küçük harf, rakam ve özel karakter içermelidir.",
                "Uyarı",
                JOptionPane.WARNING_MESSAGE
            );
            pfParola.setBorder(new LineBorder(Color.RED));
            btnKaydet.setEnabled(true);
            return;
        }

        DosyaYoneticisi.kullaniciEkle(new Kullanici(ad, soy, ka, pw, rol));
        JOptionPane.showMessageDialog(
            this,
            "Kayıt başarılı!",
            "",
            JOptionPane.INFORMATION_MESSAGE
        );
        pencere.goster(PanelTipi.GIRIS_PANELI.getKey());
    }
}