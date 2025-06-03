package src.Ihm;

import java.awt.BorderLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import src.Controleur;

public class PanelPara extends JPanel 
{

    Controleur ctrl;

    JTextField   txtDateRef;
    JRadioButton rbDateDebut;
    JRadioButton rbDateFin;
    JButton      btnValider;

    public PanelPara(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setLayout(new BorderLayout());
        // Panel référence
        JPanel panelRef = new JPanel();
        JLabel labelRef = new JLabel("Date de référence :");
        this.txtDateRef = new JTextField(this.ctrl.getDateDuJour(), 10);

        // Panel type de date
        JPanel panelType = new JPanel();

        JLabel labelType = new JLabel("Type de date :");
        this.rbDateDebut = new JRadioButton("Date de début");
        this.rbDateFin   = new JRadioButton("Date de fin");
        this.rbDateDebut.setSelected(true); // Par défaut, on choisit la date de début

        ButtonGroup group = new ButtonGroup();
        group.add(this.rbDateDebut);
        group.add(this.rbDateFin);

        // Bouton Valider
        this.btnValider = new JButton("Valider");

        // Ajout des composants au panel

        panelRef.add(labelRef);
        panelRef.add(this.txtDateRef);
        
        panelType.add(labelType);
        panelType.add(this.rbDateDebut);
        panelType.add(this.rbDateFin);


        this.add(panelRef, BorderLayout.NORTH);
        this.add(panelType, BorderLayout.CENTER);
        this.add(this.btnValider, BorderLayout.SOUTH);

        this.setVisible(true);
    }
    
}
