package src.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import src.Controleur;
import src.utils.BtnUtils;
import src.utils.ErrorUtils;

public class PanelPara extends JPanel implements ActionListener
{   
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur   ctrl;
    private JTextField   txtDateRef;
    private JRadioButton rbDateDebut;
    private JRadioButton rbDateFin;
    private JRadioButton rbDateFormatNum;
    private JRadioButton rbDateFormatTexte;
    private JButton      btnValider;

    /*--------------*
     * Constructeur *
     *--------------*/
    public PanelPara(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        
        // Panels vides pour centrer
        this.add(new JPanel(), BorderLayout.NORTH);
        this.add(new JPanel(), BorderLayout.SOUTH);
        this.add(new JPanel(), BorderLayout.EAST );
        this.add(new JPanel(), BorderLayout.WEST );
        
        // Panel principal centré
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        
        // Titre
        JLabel titre = new JLabel("Configuration du Projet", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(titre, BorderLayout.NORTH);
        
        // Contenu
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        
        // Date référence
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        datePanel.setBackground(Color.WHITE);
        datePanel.add(new JLabel("Date de référence : "));
        this.txtDateRef = new JTextField(this.ctrl.getDateDuJour(), 10);
        datePanel.add(this.txtDateRef);
        content.add(datePanel, BorderLayout.NORTH);
        
        // Options
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Type de date
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        typePanel.setBackground(Color.WHITE);
        typePanel.add(new JLabel("Type : "));
        this.rbDateDebut = new JRadioButton("Début", true);
        this.rbDateFin = new JRadioButton("Fin");
        this.rbDateFin.setOpaque(false);
        this.rbDateDebut.setOpaque(false);
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(this.rbDateDebut);
        typeGroup.add(this.rbDateFin);
        typePanel.add(this.rbDateDebut);
        typePanel.add(this.rbDateFin);
        optionsPanel.add(typePanel, BorderLayout.NORTH);
        
        // Format
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formatPanel.setBackground(Color.WHITE);
        formatPanel.add(new JLabel("Format : "));
        this.rbDateFormatNum = new JRadioButton("Numérique", true);
        this.rbDateFormatTexte = new JRadioButton("Texte");
        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(this.rbDateFormatNum);
        formatGroup.add(this.rbDateFormatTexte);
        formatPanel.add(this.rbDateFormatNum);
        formatPanel.add(this.rbDateFormatTexte);
        optionsPanel.add(formatPanel, BorderLayout.SOUTH);
        
        content.add(optionsPanel, BorderLayout.CENTER);
        
        // Bouton
        this.btnValider = BtnUtils.creerBtn("Valider", new Color(0, 183, 14), "Valider la configuration");
        content.add(this.btnValider, BorderLayout.SOUTH);
        
        this.rbDateFormatNum.setOpaque(false);
        this.rbDateFormatTexte.setOpaque(false);
        
        mainPanel.add(content, BorderLayout.CENTER);
        this.add(mainPanel, BorderLayout.CENTER);
        
        this.btnValider.addActionListener(this);
    }

    /*---------------------------------*
     * Gestion des événements          *
     *---------------------------------*/
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnValider) 
        {
            String  dateRef     = this.txtDateRef.getText().trim();
            char    typeDate    = this.rbDateDebut.isSelected() ? 'D' : 'F';
            boolean formatTexte = this.rbDateFormatTexte.isSelected();

            if (!dateRef.matches("\\d{2}/\\d{2}/\\d{4}")) 
            {
                dateRef = this.ctrl.getDateDuJour(); 
                ErrorUtils.showError("Date invalide, réinitialisation à la date du jour : " + dateRef);
            }

            this.ctrl.setFormatDateTexte(formatTexte);
            this.ctrl.initProjet(dateRef, typeDate, "listeTache.txt");
        }
    }
}