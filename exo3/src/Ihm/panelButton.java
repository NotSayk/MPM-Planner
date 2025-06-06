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

    private JButton    btnPlusTot;
    private JButton    btnPlusTard;
    private JButton    btnReset;
    private JButton    btnTheme;
    private JButton    btnCritique;
    private boolean    cheminCritique;
    private PanelMPM panelMPM;
    
    // Couleurs modernes
    private static final Color BACKGROUND_COLOR = new Color(45, 45, 55);
    private static final Color BUTTON_PRIMARY = new Color(100, 149, 237);
    private static final Color BUTTON_SECONDARY = new Color(75, 175, 79);
    private static final Color BUTTON_WARNING = new Color(255, 193, 7);
    private static final Color BUTTON_DANGER = new Color(220, 53, 69);
    private static final Color BUTTON_INFO = new Color(23, 162, 184);
    private static final Color BUTTON_HOVER = new Color(255, 255, 255, 30);
    private static final Color TEXT_COLOR = Color.WHITE;

    public panelButton(Controleur ctrl, PanelMPM panelMPM) {
        this.ctrl = ctrl;
        this.panelMPM = panelMPM;
        this.cheminCritique = true;
        
        setupPanel();
        createButtons();
        addButtons();
        setupEventListeners();
    }
    
    private void setupPanel() {
        this.setBackground(BACKGROUND_COLOR);
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void createButtons() {
        btnPlusTot = createStyledButton("⏰ Plus tôt", BUTTON_PRIMARY, "Afficher les dates au plus tôt");
        btnPlusTard = createStyledButton("⏳ Plus tard", BUTTON_SECONDARY, "Afficher les dates au plus tard");
        btnReset = createStyledButton("🔄 Réinitialiser", BUTTON_WARNING, "Remettre à zéro les positions");
        btnCritique = createStyledButton("🎯 Chemin critique", BUTTON_DANGER, "Afficher/masquer le chemin critique");
        btnTheme = createStyledButton("🎨 Changer thème", BUTTON_INFO, "Basculer entre les thèmes");
        
        btnPlusTard.setEnabled(false);
    }
    
    private JButton createStyledButton(String text, Color baseColor, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Couleur de fond avec gradient
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, baseColor.darker(), 0, getHeight(), baseColor.darker().darker());
                } else if (getModel().isRollover() && isEnabled()) {
                    gradient = new GradientPaint(0, 0, baseColor.brighter(), 0, getHeight(), baseColor);
                } else {
                    gradient = new GradientPaint(0, 0, baseColor, 0, getHeight(), baseColor.darker());
                }
                
                if (!isEnabled()) {
                    g2.setColor(new Color(100, 100, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                } else {
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
                
                // Effet de survol
                if (getModel().isRollover() && isEnabled()) {
                    g2.setColor(BUTTON_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        // Style du bouton
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(140, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        // Bordure subtile
        Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        );
        button.setBorder(border);
        
        // Effets de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    private void addButtons() {
        this.add(btnPlusTot);
        this.add(btnPlusTard);
        this.add(btnReset);
        this.add(btnCritique);
        this.add(btnTheme);
    }
    
    private void setupEventListeners() {
        btnPlusTot.addActionListener(this);
        btnPlusTard.addActionListener(this);
        btnReset.addActionListener(this);
        btnTheme.addActionListener(this);
        btnCritique.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Animation de clic
        JButton sourceButton = (JButton) e.getSource();
        animateButtonClick(sourceButton);
        
        if (e.getSource() == btnPlusTot) {
            panelMPM.afficherDateTot();
            if (panelMPM.estGriseTot()) {
                btnPlusTard.setEnabled(true);
                btnPlusTot.setEnabled(false);
                updateButtonStates();
            }
        }
        else if (e.getSource() == btnPlusTard) {
            panelMPM.afficherDateTard();
            if (panelMPM.estGriseTard()) {
                btnPlusTard.setEnabled(false);
                updateButtonStates();
            }
        }
        else if (e.getSource() == btnReset) {
            ctrl.resetPositions();
            btnPlusTard.setEnabled(false);
            btnPlusTot.setEnabled(true);
            panelMPM.cacherDates();
            updateButtonStates();
            showStyledMessage("Les tâches ont été réinitialisées !", BUTTON_SECONDARY);
        }
        else if (e.getSource() == btnCritique) {
            panelMPM.afficherCheminCritique(cheminCritique);
            cheminCritique = !cheminCritique;
            // Mise à jour du texte du bouton
            btnCritique.setText(cheminCritique ? "🎯 Chemin critique" : "🎯 Masquer critique");
        }
        else if (e.getSource() == btnTheme) {
            ctrl.changerTheme();
            panelMPM.repaint();
            showStyledMessage("🎨 Thème modifié !", BUTTON_INFO);
        }
    }
    
    private void animateButtonClick(JButton button) {
        Timer timer = new Timer(100, e -> button.repaint());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void updateButtonStates() {
        // Mise à jour visuelle des boutons désactivés
        SwingUtilities.invokeLater(() -> {
            btnPlusTot.repaint();
            btnPlusTard.repaint();
        });
    }
    
    private void showStyledMessage(String message, Color color) {
        // Création d'une notification stylisée
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(color);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        messagePanel.add(messageLabel);
        
        // Affichage temporaire (vous pouvez adapter selon vos besoins)
        ErrorUtils.showInfo(message);
    }
    
    public void setCritiqueButton(boolean critique) { 
        this.cheminCritique = critique;
        btnCritique.setText(critique ? "🎯 Chemin critique" : "🎯 Masquer critique");
    }
    
    // Méthode pour mettre à jour les couleurs selon le thème
    public void updateTheme(boolean darkMode) {
        Color newBackground = darkMode ? new Color(45, 45, 55) : new Color(240, 240, 245);
        this.setBackground(newBackground);
        this.repaint();
    }
}