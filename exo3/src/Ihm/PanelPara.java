package src.Ihm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import src.Controleur;

public class PanelPara extends JPanel implements ActionListener
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
        JPanel panel = new JPanel( new GridLayout(3,1));
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
        JPanel panelButton = new JPanel();
        this.btnValider = new JButton("Valider");

        // Ajout des composants au panel

        panelRef.add(labelRef);
        panelRef.add(this.txtDateRef);
        
        panelType.add(labelType);
        panelType.add(this.rbDateDebut);
        panelType.add(this.rbDateFin);

        panelButton.add(this.btnValider, new FlowLayout(FlowLayout.CENTER));


        panel.add(panelRef);
        panel.add(panelType);
        panel.add(panelButton);

        this.add(panel, BorderLayout.NORTH);

        this.setVisible(true);

        // Ajout des écouteurs d'événements
        this.btnValider.addActionListener(this);
        this.rbDateDebut.addActionListener(this);
        this.rbDateFin.addActionListener(this);
        this.txtDateRef.addActionListener(this);
        this.txtDateRef.setToolTipText("Entrez la date de référence au format jj/mm/aaaa");
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnValider) 
        {
            String dateRef = this.txtDateRef.getText().trim();
            char typeDate = this.rbDateDebut.isSelected() ? 'D' : 'F';

            // Validation de la date
            if (!dateRef.matches("\\d{2}/\\d{2}/\\d{4}")) 
            {
                System.out.println("Format de date invalide. Veuillez entrer au format jj/mm/aaaa.");
                return;
            }

            // Appel du contrôleur pour initialiser le projet
            this.ctrl.initialiserProjet(dateRef, typeDate);

            //this.ctrl.getCheminCritique();
        }
    }


    
}