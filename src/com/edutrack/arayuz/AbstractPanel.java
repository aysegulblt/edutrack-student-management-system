package com.edutrack.arayuz;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPanel extends JPanel {

   
    protected AbstractPanel(String title) {
        
        setLayout(new BorderLayout(UIConstants.LAYOUT_GAP, UIConstants.LAYOUT_GAP));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.PADDING, UIConstants.PADDING,
            UIConstants.PADDING, UIConstants.PADDING
        ));

       
        if (title != null && !title.isEmpty()) {
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(UIConstants.HEADER_FONT);
            lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, UIConstants.LAYOUT_GAP, 0));
            add(lblTitle, BorderLayout.NORTH);
        }
    }

    
    public abstract void refreshData();
}
