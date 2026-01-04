package com.edutrack.yardimci;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class IconUtil {
    
    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    
    public static ImageIcon loadSVGIcon(String resourcePath, int width, int height) {
        
        String path = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        String key = path + "@" + width + "x" + height;
        return CACHE.computeIfAbsent(key, k -> {
            try {
                FlatSVGIcon svg = new FlatSVGIcon(path, width, height);
                BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = buf.createGraphics();
                svg.paintIcon(null, g2, 0, 0);
                g2.dispose();
                return new ImageIcon(buf);
            } catch (Exception ex) {
                ex.printStackTrace();
                return new ImageIcon();
            }
        });
    }

   
    public static Icon loadSVG(String resourcePath, int width, int height) {
        String path = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        try {
            return new FlatSVGIcon(path, width, height);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    
    public static Icon loadSVG(String resourcePath) {
        String path = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        try {
            return new FlatSVGIcon(path);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}