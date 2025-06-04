package src.Ihm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import src.Controleur;

public class PanelModification extends JPanel implements ActionListener
{
	private Controleur ctrl;

	private JTable     tblGrilleDonnees;
	private JTextField txtTacheDuree;
	private JButton    btnMaj;

	public PanelModification (Controleur ctrl)
	{
		this.ctrl = ctrl;
		this.setLayout ( new BorderLayout() );

		JScrollPane spGrilleDonnees;
		
		// CrÃƒÂ©ation des composants
		this.tblGrilleDonnees = new JTable ( new GrilleDonneesModel(ctrl) );
		this.tblGrilleDonnees.setFillsViewportHeight(true);

		JPanel panelInfo = new JPanel();
		JLabel lblDuree  = new JLabel("Durée de la tâche :");
		this.txtTacheDuree = new JTextField(10);
		this.btnMaj = new JButton("Mettre à jour");
		panelInfo.add(lblDuree);
		panelInfo.add(this.txtTacheDuree);
		panelInfo.add(this.btnMaj);

 		spGrilleDonnees   = new JScrollPane( this.tblGrilleDonnees );

		// positionnement des composants
		this.add ( spGrilleDonnees, BorderLayout.NORTH );
		this.add ( panelInfo, BorderLayout.SOUTH );
	}

	public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnMaj) 
        {
			// Logique pour le bouton "+ tôt"
		}
	}

}