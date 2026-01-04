package com.edutrack.arayuz;

import com.edutrack.AnaPencere;
import com.edutrack.model.Kullanici;
import com.edutrack.util.PanelTipi;
import com.edutrack.util.Sabitler;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AyarlarPaneli extends AbstractPanel {
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$");

    private final AnaPencere pencere;
    private final CardLayout kartLayout;
    private final JPanel parentPanel;
    private final String geriPanelKey;

    private final JPasswordField pfEski       = new JPasswordField();
    private final JPasswordField pfYeni       = new JPasswordField();
    private final JPasswordField pfYeniTekrar = new JPasswordField();
    private final JCheckBox cbShowEski        = new JCheckBox("Göster");
    private final JCheckBox cbShowYeni        = new JCheckBox("Göster");
    private final JCheckBox cbShowTekrar      = new JCheckBox("Göster");

    private final JLabel lblPolicy = new JLabel(
        "Parola en az 8 karakter, büyük/küçük harf, rakam ve özel karakter içermelidir."
    );
    private final JProgressBar strengthBar = new JProgressBar(0, 5);
    private final JLabel lblStrength      = new JLabel("Güç: ");
    private final JLabel lblError         = new JLabel(" ");

    private final JButton btnDegistir = new JButton("Şifre Değiştir");
    private final JButton btnGeri     = new JButton("Geri");
    private final JButton btnCikis    = new JButton("Çıkış Yap");
    private final JButton btnHesapSil = new JButton("Hesabı Sil");

    public AyarlarPaneli(
        AnaPencere pencere,
        CardLayout kartLayout,
        JPanel parentPanel,
        String geriPanelKey
    ) {
        super("Ayarlar");
        this.pencere      = pencere;
        this.kartLayout   = kartLayout;
        this.parentPanel  = parentPanel;
        this.geriPanelKey = geriPanelKey;

        Color bg = UIConstants.BACKGROUND_COLOR;
        setBackground(bg);
        setBorder(new EmptyBorder(
            UIConstants.PADDING, UIConstants.PADDING,
            UIConstants.PADDING, UIConstants.PADDING
        ));
        setLayout(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP / 2));

       
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBackground(bg);

       
        btnGeri.setIcon(IconUtil.loadSVG("icons/arrow-left.svg", 16, 16));
        btnGeri.setForeground(Color.BLACK);
        btnGeri.setBorderPainted(false);
        btnGeri.setContentAreaFilled(false);
        btnGeri.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGeri.addActionListener(e -> kartLayout.show(parentPanel, geriPanelKey));
        header.add(btnGeri);
        header.add(Box.createHorizontalGlue());

        
        JLabel lblBaslik = new JLabel("Ayarlar");
        lblBaslik.setFont(lblBaslik.getFont().deriveFont(Font.BOLD, 18f));
        header.add(lblBaslik);
        header.add(Box.createHorizontalGlue());

       
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIConstants.LAYOUT_GAP, 0));
        rightBtns.setBackground(bg);

        btnHesapSil.setIcon(IconUtil.loadSVG("icons/delete.svg", 16, 16));
        btnHesapSil.setForeground(Color.BLACK);
        btnHesapSil.setBorderPainted(false);
        btnHesapSil.setContentAreaFilled(false);
        btnHesapSil.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHesapSil.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(
                this, "Hesabınızı silmek istediğinize emin misiniz?", "Hesabı Sil", JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION) {
                Kullanici.oturumuKapat();
                pencere.goster(PanelTipi.GIRIS_PANELI.getKey());
            }
        });
        rightBtns.add(btnHesapSil);

        btnCikis.setIcon(IconUtil.loadSVG("icons/logout.svg", 16, 16));
        btnCikis.setForeground(Color.BLACK);
        btnCikis.setBorderPainted(false);
        btnCikis.setContentAreaFilled(false);
        btnCikis.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCikis.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(
                this, "Çıkış yapmak istediğinize emin misiniz?", "Çıkış Yap", JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION) {
                Kullanici.oturumuKapat();
                pencere.goster(PanelTipi.GIRIS_PANELI.getKey());
            }
        });
        rightBtns.add(btnCikis);

        header.add(rightBtns);
        add(header, BorderLayout.NORTH);

      
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = UIManager.getInsets("TextField.margin");
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int y = 0;

        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Eski Şifre:"), gbc);
        gbc.gridx=1; form.add(pfEski, gbc);
        gbc.gridx=2; form.add(cbShowEski, gbc);

        y++; gbc.gridy=y; gbc.gridx=0; form.add(new JLabel("Yeni Şifre:"), gbc);
        gbc.gridx=1; pfYeni.setTransferHandler(null); form.add(pfYeni, gbc);
        gbc.gridx=2; form.add(cbShowYeni, gbc);

        y++; gbc.gridy=y; gbc.gridx=0; gbc.gridwidth=3;
        lblPolicy.setFont(lblPolicy.getFont().deriveFont(Font.ITALIC,11f));
        form.add(lblPolicy, gbc); gbc.gridwidth=1;

        y++; gbc.gridy=y; gbc.gridx=0; form.add(strengthBar, gbc);
        gbc.gridx=1; form.add(lblStrength, gbc);

        y++; gbc.gridy=y; gbc.gridx=0; form.add(new JLabel("Yeni Şifre Tekrar:"), gbc);
        gbc.gridx=1; pfYeniTekrar.setTransferHandler(null); form.add(pfYeniTekrar, gbc);
        gbc.gridx=2; form.add(cbShowTekrar, gbc);

        y++; gbc.gridy=y; gbc.gridx=0; gbc.gridwidth=3;
        lblError.setForeground(Color.RED);
        form.add(lblError, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.LAYOUT_GAP, 0));
        btnPanel.setBackground(bg);
        btnDegistir.setIcon(IconUtil.loadSVG("icons/lock.svg",16,16));
        btnDegistir.setForeground(Color.BLACK);
        btnDegistir.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnDegistir.setIconTextGap(UIConstants.ICON_TEXT_GAP);
        btnDegistir.addActionListener(e -> changePassword());
        btnPanel.add(btnDegistir);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        center.setBackground(bg);
        center.add(form);
        center.add(Box.createVerticalStrut(UIConstants.LAYOUT_GAP/2));
        center.add(btnPanel);
        add(center,BorderLayout.CENTER);

        
        cbShowEski.addActionListener(e -> toggleEcho(pfEski, cbShowEski));
        cbShowYeni.addActionListener(e -> toggleEcho(pfYeni, cbShowYeni));
        cbShowTekrar.addActionListener(e -> toggleEcho(pfYeniTekrar, cbShowTekrar));

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateStrength(); }
            @Override public void removeUpdate(DocumentEvent e) { updateStrength(); }
            @Override public void changedUpdate(DocumentEvent e) { updateStrength(); }
        };
        pfYeni.getDocument().addDocumentListener(dl);
        KeyAdapter enter = new KeyAdapter(){
            public void keyPressed(KeyEvent e){ if(e.getKeyCode()==KeyEvent.VK_ENTER) btnDegistir.doClick(); }
        };
        pfEski.addKeyListener(enter); pfYeni.addKeyListener(enter); pfYeniTekrar.addKeyListener(enter);
    }

    @Override
    public void refreshData() {
       
    }

    private void toggleEcho(JPasswordField field, JCheckBox cb) {
        char echo = ((Character)UIManager.get("PasswordField.echoChar")).charValue();
        field.setEchoChar(cb.isSelected() ? (char)0 : echo);
    }

    private void updateStrength() {
        String s = new String(pfYeni.getPassword());
        int score = 0;
        if (s.length() >= 8) score++;
        if (s.matches(".*[A-Z].*")) score++;
        if (s.matches(".*[a-z].*")) score++;
        if (s.matches(".*\\d.*")) score++;
        if (s.matches(".*\\W.*")) score++;
        strengthBar.setValue(score);
        lblStrength.setText("Güç: " + (score < 3 ? "Zayıf" : score < 5 ? "Orta" : "Güçlü"));
    }

    private void changePassword() {
        lblError.setText(" ");
        pfEski.setBorder(UIManager.getBorder("PasswordField.border"));
        pfYeni.setBorder(UIManager.getBorder("PasswordField.border"));
        pfYeniTekrar.setBorder(UIManager.getBorder("PasswordField.border"));

        String eski = new String(pfEski.getPassword()).trim();
        String yeni = new String(pfYeni.getPassword()).trim();
        String tekrar = new String(pfYeniTekrar.getPassword()).trim();
        Kullanici aktif = Kullanici.getAktifKullanici();

        if (!aktif.getParola().equals(eski)) {
            lblError.setText("Eski şifre hatalı.");
            pfEski.setBorder(new LineBorder(Color.RED,1));
            return;
        }
        if (yeni.equals(eski)) {
            lblError.setText("Yeni şifre eskiyle aynı olamaz.");
            pfYeni.setBorder(new LineBorder(Color.RED,1));
            return;
        }
        List<String> prev = aktif.getOncekiParolalar();
        if (prev != null && prev.contains(yeni)) {
            lblError.setText("Yeni şifre son 3 şifreden farklı olmalı.");
            pfYeni.setBorder(new LineBorder(Color.RED,1));
            return;
        }
        if (!PASSWORD_PATTERN.matcher(yeni).matches()) {
            lblError.setText("Parola politikası karşılanmıyor.");
            pfYeni.setBorder(new LineBorder(Color.RED,1));
            return;
        }
        if (!yeni.equals(tekrar)) {
            lblError.setText("Parolalar uyuşmuyor.");
            pfYeniTekrar.setBorder(new LineBorder(Color.RED,1));
            return;
        }

        btnDegistir.setEnabled(false);
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() {
                List<String> history = aktif.getOncekiParolalar();
                if (history == null) history = new ArrayList<>();
                history.add(0, eski);
                while (history.size() > 3) history.remove(history.size()-1);
                aktif.setOncekiParolalar(history);
                aktif.setParola(yeni);

                List<Kullanici> all = DosyaYoneticisi.kullanicilariGetir();
                for (int i=0; i<all.size(); i++) {
                    if (all.get(i).getKullaniciAdi().equals(aktif.getKullaniciAdi())) {
                        all.set(i, aktif);
                        break;
                    }
                }
                DosyaYoneticisi.yaz(Sabitler.PATH_KULLANICILAR, all);
                return null;
            }
            @Override protected void done() {
                JOptionPane.showMessageDialog(
                    AyarlarPaneli.this,
                    "Şifre başarıyla güncellendi.",
                    "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE
                );
                kartLayout.show(parentPanel, geriPanelKey);
            }
        }.execute();
    }
}
