package src.Ihm;

import src.Controleur;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class BarreMenu extends JMenuBar implements ActionListener
{
   private Controleur    ctrl;

   private JMenuItem     menuiCharger;
   private JMenuItem     menuiSauvegarder;
   private JMenuItem     menuiQuitter;

   private JMenuItem     menuiRajouterTache;
   private JMenuItem     menuiSupprimerTache;
   private JMenuItem     menuiChangerDureeTache;

   public BarreMenu(Controleur ctrl)
   {
      this.ctrl = ctrl;


      /*----------------------------*/
      /* Création des composants    */
      /*----------------------------*/

      // les JMenu
      JMenu menuFichier   = new JMenu("Fichier"  );
      JMenu menuEdition   = new JMenu("Edition"  );

      // les JItemMenu
      this.menuiCharger           = new JMenuItem("Charger"                  );
      this.menuiSauvegarder       = new JMenuItem("Sauvegarder"              );
      this.menuiQuitter           = new JMenuItem("Quitter"                  );

      this.menuiRajouterTache     = new JMenuItem("Rajouter une tâche"       );
      this.menuiSupprimerTache    = new JMenuItem("supprimer une tâche"      );
      this.menuiChangerDureeTache = new JMenuItem("Changer durée d'une tâche");



      /*-------------------------------*/
      /* positionnement des composants */
      /*-------------------------------*/

      //rajout des items dans les menus
      menuFichier.add( this.menuiCharger           );
      menuFichier.add( this.menuiSauvegarder       );
      menuFichier.addSeparator();
      menuFichier.add( this.menuiQuitter           );

      menuEdition.add( this.menuiRajouterTache     );
      menuEdition.add( this.menuiSupprimerTache    );
      menuEdition.add( this.menuiChangerDureeTache );


      //rajout des menus
      this.add( menuFichier );
      this.add( menuEdition );


      /*-------------------------------*/
      /* Activation des composants     */
      /*-------------------------------*/
      this.menuiCharger          .addActionListener(this); 
      this.menuiSauvegarder      .addActionListener(this); 
      this.menuiQuitter          .addActionListener(this); 

      this.menuiRajouterTache    .addActionListener(this);
      this.menuiSupprimerTache   .addActionListener(this);
      this.menuiChangerDureeTache.addActionListener(this);


      this.menuiCharger    .setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK ));
      this.menuiSauvegarder.setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK ));
      this.menuiQuitter    .setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_F4,InputEvent.ALT_DOWN_MASK  ));
   }

   public void actionPerformed ( ActionEvent e )
   {
      if(e.getSource() == this.menuiCharger)
      {
         JFileChooser selectionFichier = new JFileChooser();

         if (selectionFichier.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            this.ctrl.initialiserProjet(this.ctrl.getDateDuJour(),'D' , "" + selectionFichier.getSelectedFile());
      }

      if(e.getSource() == this.menuiSauvegarder)
      {
         this.ctrl.sauvegarder();
      }

      if(e.getSource() == this.menuiQuitter)
      {
         System.exit(0);
      }

      if(e.getSource() == this.menuiRajouterTache)
      {

      }

      if(e.getSource() == this.menuiSupprimerTache)
      {

      }

      if(e.getSource() == this.menuiChangerDureeTache)
      {

      }
   }

}