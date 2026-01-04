package com.edutrack;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;


public class EduTrack {
    public static void main(String[] args) {
       
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("FlatLaf yüklenemedi, varsayılan tema kullanılacak: " + ex);
        }

        
        setGlobalUI();

        
        SwingUtilities.invokeLater(() -> {
            AnaPencere pencere = AnaPencere.getInstance();
            pencere.setVisible(true);
        });
    }

    private static void setGlobalUI() {
        
        Font baseFont = new Font("SansSerif", Font.PLAIN, 14);

        UIManager.put("Label.font",        baseFont.deriveFont(Font.BOLD,   14f));
        UIManager.put("Button.font",       baseFont);
        UIManager.put("TextField.font",    baseFont);
        UIManager.put("PasswordField.font",baseFont);
        UIManager.put("ComboBox.font",     baseFont);
        UIManager.put("Table.font",        baseFont);
        UIManager.put("TableHeader.font",  baseFont.deriveFont(Font.BOLD,   13f));

      
        UIManager.put("Panel.background",       Color.WHITE);
        UIManager.put("ScrollPane.background",  Color.WHITE);
        UIManager.put("Viewport.background",    Color.WHITE);

        
        UIManager.put("Button.arc",        8);
        UIManager.put("Component.arc",     6);
        UIManager.put("TextComponent.arc", 6);

       
        UIManager.put("Button.background",         Color.decode("#2D9CDB"));
        UIManager.put("Table.selectionBackground", Color.decode("#56CCF2"));

        
        Insets defaultInsets = new Insets(8, 8, 8, 8);
        UIManager.put("Button.margin",    defaultInsets);
        UIManager.put("TextField.margin", defaultInsets);
        UIManager.put("ComboBox.margin",  defaultInsets);
    }
}