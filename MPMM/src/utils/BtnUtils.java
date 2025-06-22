package src.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import javax.swing.*;
import javax.swing.border.Border;

public class BtnUtils 
{

    public BtnUtils() 
    {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static JButton creerBtn(String text, Color baseColor, String tooltip) 
    {
        JButton button = new JButton(text) 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient;
                if (getModel().isPressed())
                    gradient = new GradientPaint(0, 0, baseColor.darker(), 0, getHeight(), baseColor.darker().darker());
                if (getModel().isRollover() && isEnabled()) 
                    gradient = new GradientPaint(0, 0, baseColor.brighter(), 0, getHeight(), baseColor);
                else 
                    gradient = new GradientPaint(0, 0, baseColor, 0, getHeight(), baseColor.darker());
                
                if (!isEnabled()) 
                {
                    g2.setColor(new Color(100, 100, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                } 
                else 
                {
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }

                if (getModel().isRollover() && isEnabled()) 
                {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        // Style du bouton
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(140, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        );
        button.setBorder(border);
        
        button.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) 
            {
                if (button.isEnabled()) button.repaint();

            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) 
            {
                button.repaint();
            }
        });
        
        return button;
    }

}
