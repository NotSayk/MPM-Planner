package src.ihm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import src.Controleur;
import src.utils.ErrorUtils;

public class PanelPara extends JPanel implements ActionListener
{   
    private static final Color MAIN_COLOR = new Color(70, 130, 180);
    private static final Color ACCENT_COLOR = new Color(211, 211, 211);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 12);
    
    private Controleur   ctrl;
    private JTextField   txtDateRef;
    private JRadioButton rbDateDebut;
    private JRadioButton rbDateFin;
    private JRadioButton rbDateFormatNum;
    private JRadioButton rbDateFormatTexte;
    private JButton      btnValider;

    public PanelPara(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel référence
        JPanel panelRef = createStyledPanel("Date de référence");
        this.txtDateRef = new JTextField(this.ctrl.getDateDuJour(), 15);
        txtDateRef.setFont(FIELD_FONT);
        txtDateRef.setToolTipText("Entrez la date de référence au format jj/mm/aaaa");
        panelRef.add(txtDateRef);
        
        // Panel type de date
        JPanel panelType = createStyledPanel("Type de date");
        this.rbDateDebut = createStyledRadioButton("Date de début", true);
        this.rbDateFin = createStyledRadioButton("Date de fin", false);
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(rbDateDebut);
        typeGroup.add(rbDateFin);
        
        panelType.add(rbDateDebut);
        panelType.add(rbDateFin);
        
        // Panel format
        JPanel panelFormat = createStyledPanel("Format de date");
        this.rbDateFormatNum = createStyledRadioButton("Numérique", true);
        this.rbDateFormatTexte = createStyledRadioButton("Texte", false);
        
        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(rbDateFormatNum);
        formatGroup.add(rbDateFormatTexte);
        
        panelFormat.add(rbDateFormatNum);
        panelFormat.add(rbDateFormatTexte);
        
        // Bouton Valider
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBtn.setBackground(Color.WHITE);
        this.btnValider = new JButton("Valider");
        btnValider.setFont(new Font("Arial", Font.BOLD, 12));
        btnValider.setForeground(Color.WHITE);
        btnValider.setBackground(MAIN_COLOR);
        btnValider.setFocusPainted(false);
        btnValider.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        panelBtn.add(btnValider);
        
        // Add spacing between panels
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(panelRef);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(panelType);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(panelFormat);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(panelBtn);
        
        this.add(mainPanel, BorderLayout.NORTH);
        
        // Ajout des écouteurs d'événements
        this.btnValider.addActionListener(this);
        this.rbDateDebut.addActionListener(this);
        this.rbDateFin.addActionListener(this);
        this.txtDateRef.addActionListener(this);
        this.rbDateFormatNum.addActionListener(this);
        this.rbDateFormatTexte.addActionListener(this);
    }
    
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MAIN_COLOR, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LABEL_FONT,
            MAIN_COLOR
        ));
        return panel;
    }
    
    private JRadioButton createStyledRadioButton(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(FIELD_FONT);
        rb.setSelected(selected);
        rb.setBackground(Color.WHITE);
        rb.setForeground(Color.DARK_GRAY);
        return rb;
    }

    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnValider) 
        {
            String dateRef = this.txtDateRef.getText().trim();
            char typeDate = this.rbDateDebut.isSelected() ? 'D' : 'F';
            char formatDate = this.rbDateFormatNum.isSelected() ? 'N' : 'T';

            if (!dateRef.matches("\\d{2}/\\d{2}/\\d{4}")) 
            {
                dateRef = this.ctrl.getDateDuJour(); 
                ErrorUtils.showError("Date invalide, réinitialisation à la date du jour : " + dateRef);
                txtDateRef.setText(dateRef);
            }

            // Appel du contrôleur pour initialiser le projet
            this.ctrl.initProjet(dateRef, typeDate, "listeTache.txt");
        }
    }
}