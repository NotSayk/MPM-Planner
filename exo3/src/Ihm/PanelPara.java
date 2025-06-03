package src.Ihm;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class PanelPara extends JPanel 
{

    JTextField   txtDateRef;
    JRadioButton rbDateDebut;
    JRadioButton rbDateFin;

    public PanelPara() 
    {
        // Panel référence
        JPanel panelRef = new JPanel();
        JLabel labelRef = new JLabel("Date de référence :");
        this.txtDateRef = new JTextField(10);

        // Panel type de date
        JPanel panelType = new JPanel();

        JLabel labelType = new JLabel("Type de date :");
        this.rbDateDebut = new JRadioButton("Date de début");
        this.rbDateFin   = new JRadioButton("Date de fin");
        this.rbDateDebut.setSelected(true); // Par défaut, on choisit la date de début

        ButtonGroup group = new ButtonGroup();
        group.add(this.rbDateDebut);
        group.add(this.rbDateFin);

        // Ajout des composants au panel

        panelRef.add(labelRef);
        panelRef.add(this.txtDateRef);
        


        this.add(panelRef);
        this.setVisible(true);
    }
    
}
