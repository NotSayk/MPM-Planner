package src.Ihm;

import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import src.Controleur;
import src.utils.ErrorUtils;

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
      this.ctrl      = ctrl;


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
         File         fichierSelectionner = null;
         JFileChooser selectionFichier    = new JFileChooser();


         if (selectionFichier.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            fichierSelectionner = selectionFichier.getSelectedFile();   

         try
         {
            
            if(fichierSelectionner.getName().substring(fichierSelectionner.getName().lastIndexOf('.') + 1).equals("MC"))
            {
               this.ctrl.initComplet(this.ctrl.getTypeDate(), "" + fichierSelectionner);
               ErrorUtils.showSucces("chargement d'un fichier de données complexe réussi");
            }
            else
            {
               this.ctrl.initProjet(this.ctrl.getDateReference(),this.ctrl.getTypeDate() , "" + fichierSelectionner);
               ErrorUtils.showSucces("chargement d'un fichier de données simple réussi");
            }

         }catch (Exception exc) { ErrorUtils.showError("erreur durant le chargement du fichier"); }
      }



      if(e.getSource() == this.menuiSauvegarder)
      {
         this.ctrl.sauvegarderFichier();
      }

      if(e.getSource() == this.menuiQuitter)           System.exit(0);

      if(e.getSource() == this.menuiRajouterTache)     this.ctrl.afficherModification();

      if(e.getSource() == this.menuiSupprimerTache)    this.ctrl.afficherModification();

      if(e.getSource() == this.menuiChangerDureeTache) this.ctrl.afficherModification();
   }
}