package src.Ihm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import src.Controleur;
import src.utils.ErrorUtils;

public class panelButton extends JPanel implements ActionListener {

    private Controleur ctrl;
    private PanelMPM   panelMPM;
    private boolean    cheminCritique;

    private JButton    btnPlusTot;
    private JButton    btnPlusTard;
    private JButton    btnReset;
    private JButton    btnTheme;
    private JButton    btnCritique;

    public panelButton(Controleur ctrl, PanelMPM panelMPM) {
        this.ctrl = ctrl;
        this.panelMPM = panelMPM;
        this.cheminCritique = false;
        
        this.setBackground(new Color(45, 45, 55));
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnPlusTot  = creerBtn("Plus tôt", new Color(0, 183, 14), "Afficher les dates au plus tôt");
        btnPlusTard = creerBtn("Plus tard", new Color(255, 27, 14), "Afficher les dates au plus tard");
        btnReset    = creerBtn("Réinitialiser", new Color(255, 193, 7), "Remettre à zéro les positions");
        btnCritique = creerBtn("Chemin critique", new Color(220, 53, 69), "Afficher/masquer le chemin critique");
        btnTheme    = creerBtn("Changer thème", new Color(23, 162, 184), "Basculer entre les thèmes");
        
        btnPlusTard.setEnabled(false);

        this.add(btnPlusTot);
        this.add(btnPlusTard);
        this.add(btnReset);
        this.add(btnCritique);
        this.add(btnTheme);

        this.btnPlusTot. addActionListener(this);
        this.btnPlusTard.addActionListener(this);
        this.btnReset   .addActionListener(this);
        this.btnTheme   .addActionListener(this);
        this.btnCritique.addActionListener(this);
    }
    
    
    private JButton creerBtn(String text, Color baseColor, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient;
                if (getModel().isPressed())
                {
                    gradient = new GradientPaint(0, 0, baseColor.darker(), 0, getHeight(), baseColor.darker().darker());
                } 
                else if (getModel().isRollover() && isEnabled()) 
                {
                    gradient = new GradientPaint(0, 0, baseColor.brighter(), 0, getHeight(), baseColor);
                } 
                else 
                {
                    gradient = new GradientPaint(0, 0, baseColor, 0, getHeight(), baseColor.darker());
                }
                
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
            public void mouseEntered(MouseEvent e) 
            {
                if (button.isEnabled()) button.repaint();

            }
            
            @Override
            public void mouseExited(MouseEvent e) 
            {
                button.repaint();
            }
        });
        
        return button;
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        animateButtonClick(sourceButton);
        
        if (e.getSource() == btnPlusTot) 
        {
            panelMPM.afficherDateTot();
            if (panelMPM.estGriseTot()) 
            {
                btnPlusTard.setEnabled(true);
                btnPlusTot.setEnabled(false);
                updateButtonStates();
            }
        }
        else if (e.getSource() == btnPlusTard) 
        {
            panelMPM.afficherDateTard();
            if (panelMPM.estGriseTard()) 
            {
                btnPlusTard.setEnabled(false);
                updateButtonStates();
            }
        }
        else if (e.getSource() == btnReset) 
        {
            ctrl.resetPositions();
            btnPlusTard.setEnabled(false);
            btnPlusTot.setEnabled(true);
            panelMPM.cacherDates();
            updateButtonStates();
            ErrorUtils.showSucces("Les tâches ont été réinitialisées !");
        
        }
        else if (e.getSource() == btnCritique) 
        {
            this.cheminCritique = !this.cheminCritique;
            panelMPM.afficherCheminCritique(this.cheminCritique);
            btnCritique.setText(this.cheminCritique ? "Masquer critique" : "Chemin critique");
        }
        else if (e.getSource() == btnTheme) 
        {
            ctrl.changerTheme();
            panelMPM.repaint();
            ErrorUtils.showSucces("Le thème a été modifié !");
        }
    }
    
    private void animateButtonClick(JButton button) 
    {
        Timer timer = new Timer(100, e -> button.repaint());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void updateButtonStates() 
    {
        SwingUtilities.invokeLater(() -> 
        {
            btnPlusTot.repaint();
            btnPlusTard.repaint();
        });
    }
    
    public void setCritiqueButton(boolean critique) 
    { 
        this.cheminCritique = critique;
        btnCritique.setText(critique ? "Masquer critique" : "Chemin critique");
    }
    
    public void updateTheme(boolean darkMode) {
        Color newBackground = darkMode ? new Color(45, 45, 55) : new Color(240, 240, 245);
        this.setBackground(newBackground);
        this.repaint();
    }
}