package com.edutrack;

import com.formdev.flatlaf.FlatLightLaf;
import com.edutrack.arayuz.GirisPaneli;
import com.edutrack.arayuz.KayitPaneli;
import com.edutrack.util.PanelTipi;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


public class AnaPencere extends JFrame {
    private static final long serialVersionUID = 1L;

   
    private static AnaPencere instance;

    private final CardLayout kartDizilimi = new CardLayout();
    private final JPanel kartPanel = new JPanel(kartDizilimi);

    private AnaPencere() {
        super("EduTrack");

        
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("FlatLaf yüklenemedi: " + ex);
        }

        
        Color primaryGreen = new Color(0x9B, 0xE2, 0x00);
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.background", primaryGreen);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Table.selectionBackground", primaryGreen);
        UIManager.put("Table.selectionForeground", Color.BLACK);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Uygulama simgesi (logo)
        try {
            URL logoUrl = getClass().getResource("/logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaled = logoIcon.getImage()
                                      .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                setIconImage(scaled);
            }
        } catch (Exception e) {
            System.err.println("Logo yüklenemedi: " + e);
        }

        
        kartPanel.add(
            new GirisPaneli(this),
            PanelTipi.GIRIS_PANELI.getKey()
        );
        kartPanel.add(
            new KayitPaneli(this),
            PanelTipi.KAYIT_PANELI.getKey()
        );
        

        setContentPane(kartPanel);
       
        kartDizilimi.show(
            kartPanel,
            PanelTipi.GIRIS_PANELI.getKey()
        );
    }

    
    public static synchronized AnaPencere getInstance() {
        if (instance == null) {
            instance = new AnaPencere();
        }
        return instance;
    }

   
    public void goster(String panelAdi) {
        kartDizilimi.show(kartPanel, panelAdi);
    }

   
    public void gosterPanel(JPanel panel, String isim) {
        kartPanel.add(panel, isim);
        kartDizilimi.show(kartPanel, isim);
        kartPanel.revalidate();
        kartPanel.repaint();
    }

    
    public static void giriseDon() {
        if (instance != null) {
            instance.kartDizilimi.show(
                instance.kartPanel,
                PanelTipi.GIRIS_PANELI.getKey()
            );
            instance.setTitle("EduTrack Giriş");
        }
    }

    
    public CardLayout getCardLayout() {
        return kartDizilimi;
    }

    
    public JPanel getCardPanel() {
        return kartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            () -> getInstance().setVisible(true)
        );
    }
}