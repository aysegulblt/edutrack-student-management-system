package com.edutrack.arayuz;

import com.edutrack.AnaPencere;
import com.edutrack.Yonlendirici;
import com.edutrack.model.Kullanici;
import com.edutrack.util.PanelTipi;
import com.edutrack.veri.DosyaYoneticisi;
import com.edutrack.yardimci.IconUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.prefs.Preferences;

public class GirisPaneli extends JPanel {
    private static final String PREF_KEY_USERS = "savedUsers";
    private final Preferences prefs = Preferences.userRoot()
        .node(GirisPaneli.class.getName());
    private final DefaultComboBoxModel<String> userModel = new DefaultComboBoxModel<>();

    private final AnaPencere pencere;
    private JComboBox<String> cbKullaniciAdi;
    private JPasswordField pfParola;
    private JComboBox<String> cbRol;
    private JCheckBox cbShowPassword;
    private JCheckBox cbRemember;
    private JButton btnDelete;
    private JButton btnGiris;
    private JButton btnKayit;

    private int failureCount = 0;
    private char defaultEcho;

    public GirisPaneli(AnaPencere pencere) {
        this.pencere = pencere;
        setPreferredSize(new Dimension(600, 500));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 245, 255));
        setBorder(new EmptyBorder(20, 50, 20, 50));

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                clearFields();
                pencere.setTitle("");
            }
        });

        
        URL logoUrl = getClass().getResource("/logo.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image img = icon.getImage()
                            .getScaledInstance(260, 260, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            lblLogo.setAlignmentX(CENTER_ALIGNMENT);
            add(lblLogo);
        }
        add(Box.createVerticalStrut(5));

       
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(CENTER_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        int y = 0;

        
        gc.gridx=0; gc.gridy=y; gc.weightx=0;
        form.add(new JLabel("Kullanıcı Adı:"), gc);
        gc.gridx=1; gc.weightx=1;
        cbKullaniciAdi = new JComboBox<>(userModel);
        cbKullaniciAdi.setEditable(true);
        form.add(cbKullaniciAdi, gc);
        gc.gridx=2; gc.weightx=0;
        btnDelete = new JButton("Sil", IconUtil.loadSVG("icons/delete.svg",16,16));
        btnDelete.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnDelete.setIconTextGap(6);
        form.add(btnDelete, gc);

        
        y++;
        gc.gridx=0; gc.gridy=y; gc.weightx=0;
        form.add(new JLabel("Parola:"), gc);
        gc.gridx=1; gc.weightx=1;
        pfParola = new JPasswordField();
        defaultEcho = pfParola.getEchoChar();
        form.add(pfParola, gc);
        gc.gridx=2; gc.weightx=0;
        cbShowPassword = new JCheckBox("Şifreyi Göster");
        form.add(cbShowPassword, gc);

        
        y++;
        gc.gridx=0; gc.gridy=y; gc.weightx=0;
        form.add(new JLabel("Rol:"), gc);
        gc.gridx=1; gc.weightx=1;
        cbRol = new JComboBox<>(new String[]{
            "Öğrenci","Okul Yetkilisi","Sponsor","Etkinlik Sorumlusu"
        });
        form.add(cbRol, gc);
        gc.gridx=2; gc.weightx=0;
        cbRemember = new JCheckBox("Beni Hatırla");
        form.add(cbRemember, gc);

        add(form);
        add(Box.createVerticalStrut(5));

        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,4));
        btnPanel.setOpaque(false);
        btnGiris = new JButton("Giriş Yap", IconUtil.loadSVG("icons/login.svg",16,16));
        btnGiris.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnGiris.setIconTextGap(6);
        btnKayit = new JButton("Kayıt Ol", IconUtil.loadSVG("icons/user-plus.svg",16,16));
        btnKayit.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnKayit.setIconTextGap(6);
        btnPanel.add(btnGiris);
        btnPanel.add(btnKayit);
        btnPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(btnPanel);
        add(Box.createVerticalStrut(5));

        
        loadSavedUsers();
        btnDelete.addActionListener(e -> removeSavedUser(
            ((JTextComponent)cbKullaniciAdi.getEditor()
                .getEditorComponent()).getText().trim()));
        cbShowPassword.addActionListener(e ->
            pfParola.setEchoChar(cbShowPassword.isSelected() ? (char)0 : defaultEcho));
        btnGiris.addActionListener(e -> onLogin());
        btnKayit.addActionListener(e ->
            pencere.goster(PanelTipi.KAYIT_PANELI.getKey()));
        setKeyBindings();
        clearFields();
    }

    private void onLogin() {
        btnGiris.setEnabled(false);
        String ad = ((JTextComponent)cbKullaniciAdi.getEditor()
                        .getEditorComponent()).getText().trim();
        String parola = new String(pfParola.getPassword()).trim();
        String rol = cbRol.getSelectedItem().toString();

        if (ad.isEmpty() || parola.isEmpty()) {
            showError("Giriş başarısız. Bilgilerinizi kontrol edip tekrar deneyin.");
            blockIfNeeded();
            return;
        }

        Optional<Kullanici> opt = DosyaYoneticisi.kullanicilariGetir().stream()
            .filter(u -> u.getKullaniciAdi().equals(ad)
                      && u.getParola().equals(parola)
                      && u.getRol().equals(rol))
            .findFirst();

        if (opt.isEmpty()) {
            showError("Giriş başarısız. Bilgilerinizi kontrol edip tekrar deneyin.");
            blockIfNeeded();
            return;
        }

        
        if (cbRemember.isSelected()) saveUser(ad);
        Kullanici.setAktifKullanici(opt.get());
        pencere.setTitle("Hoş geldin, " + opt.get().getAd());
        failureCount = 0;
        Yonlendirici.kullaniciYonlendir(opt.get());
    }

    private void showError(String msg) {
        JOptionPane.showOptionDialog(
            this,
            msg,
            "Giriş Hatası",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            new Object[]{"Tamam"},
            "Tamam"
        );
        pfParola.setText("");
    }

    private void blockIfNeeded() {
        failureCount++;
        if (failureCount >= 3) {
            JOptionPane.showMessageDialog(
                this,
                "3 başarısız denemeden dolayı 5 saniye boyunca bekleyin.",
                "Kilitlenme",
                JOptionPane.WARNING_MESSAGE
            );
            btnGiris.setEnabled(false);
            Timer t = new Timer(5000, e -> {
                failureCount = 0;
                btnGiris.setEnabled(true);
            });
            t.setRepeats(false);
            t.start();
        } else {
            btnGiris.setEnabled(true);
        }
    }

    private void loadSavedUsers() {
        String all = prefs.get(PREF_KEY_USERS, "");
        if (!all.isBlank()) Arrays.stream(all.split(","))
                                   .forEach(userModel::addElement);
    }

    private void saveUser(String u) {
        if (!u.isBlank() && userModel.getIndexOf(u) < 0) {
            userModel.addElement(u);
            persistUsers();
        }
    }

    private void removeSavedUser(String u) {
        if (userModel.getIndexOf(u) >= 0) {
            userModel.removeElement(u);
            persistUsers();
        }
    }

    private void persistUsers() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userModel.getSize(); i++) {
            if (i > 0) sb.append(',');
            sb.append(userModel.getElementAt(i));
        }
        if (sb.isEmpty()) prefs.remove(PREF_KEY_USERS);
        else prefs.put(PREF_KEY_USERS, sb.toString());
    }

    private void setKeyBindings() {
        ((JTextComponent)cbKullaniciAdi.getEditor()
            .getEditorComponent()).addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        btnGiris.doClick();
                }
        });
    }

    private void clearFields() {
        cbKullaniciAdi.setSelectedItem("");
        pfParola.setText("");
        cbShowPassword.setSelected(false);
        cbRemember.setSelected(false);
        failureCount = 0;
        btnGiris.setEnabled(true); 
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) root.setDefaultButton(btnGiris);
    }
}