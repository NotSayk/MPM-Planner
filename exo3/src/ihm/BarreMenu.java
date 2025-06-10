package src.ihm;

import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

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
   private JMenuItem     menuiCopier;
   private JMenuItem     menuiColler;

   private JMenuItem     menuiZoom25;
   private JMenuItem     menuiZoom50;
   private JMenuItem     menuiZoom75;
   private JMenuItem     menuiZoom100;
   private JMenuItem     menuiZoom150;
   private JMenuItem     menuiZoom200;

   public BarreMenu(Controleur ctrl)
   {
      this.ctrl      = ctrl;


      /*----------------------------*/
      /* Création des composants    */
      /*----------------------------*/

      // les JMenu
      JMenu menuFichier   = new JMenu("Fichier"   );
      JMenu menuEdition   = new JMenu("Edition"   );
      JMenu menuAffichage = new JMenu("Affichage" );
      JMenu menuZoom      = new JMenu("Zoom"      );

      // les JItemMenu
      this.menuiCharger           = new JMenuItem("Charger"                  );
      this.menuiSauvegarder       = new JMenuItem("Sauvegarder"              );
      this.menuiQuitter           = new JMenuItem("Quitter"                  );

      this.menuiRajouterTache     = new JMenuItem("Rajouter une tâche"       );
      this.menuiSupprimerTache    = new JMenuItem("supprimer une tâche"      );
      this.menuiChangerDureeTache = new JMenuItem("Changer durée d'une tâche");

      this.menuiZoom25            = new JMenuItem("25%" );
      this.menuiZoom50            = new JMenuItem("50%" );
      this.menuiZoom75            = new JMenuItem("75%" );
      this.menuiZoom100           = new JMenuItem("100%");
      this.menuiZoom150           = new JMenuItem("150%");
      this.menuiZoom200           = new JMenuItem("200%");

      this.menuiCopier            = new JMenuItem("Copier"                  );
      this.menuiColler            = new JMenuItem("Coller"                  );

      /*-------------------------------*/
      /* positionnement des composants */
      /*-------------------------------*/

      //rajout des items dans les menus
      menuFichier.add( this.menuiCharger           );
      menuFichier.add( this.menuiSauvegarder       );
      menuFichier.add( this.menuiCopier            );
      menuFichier.add( this.menuiColler            );
      menuFichier.addSeparator();
      menuFichier.add( this.menuiQuitter           );

      menuEdition.add( this.menuiRajouterTache     );
      menuEdition.add( this.menuiSupprimerTache    );
      menuEdition.add( this.menuiChangerDureeTache );

      menuZoom.   add( this.menuiZoom25            );
      menuZoom.   add( this.menuiZoom50            );
      menuZoom.   add( this.menuiZoom75            );
      menuZoom.   add( this.menuiZoom100           );
      menuZoom.   add( this.menuiZoom150           );
      menuZoom.   add( this.menuiZoom200           );

      menuAffichage.add(menuZoom);

      //rajout des menus
      this.add( menuFichier   );
      this.add( menuEdition   );
      this.add( menuAffichage );

      /*-------------------------------*/
      /* Activation des composants     */
      /*-------------------------------*/
      this.menuiCharger          .addActionListener(this); 
      this.menuiSauvegarder      .addActionListener(this); 
      this.menuiQuitter          .addActionListener(this); 

      this.menuiRajouterTache    .addActionListener(this);
      this.menuiSupprimerTache   .addActionListener(this);
      this.menuiChangerDureeTache.addActionListener(this);

      this.menuiZoom25           .addActionListener(this);
      this.menuiZoom50           .addActionListener(this);
      this.menuiZoom75           .addActionListener(this);
      this.menuiZoom100          .addActionListener(this);
      this.menuiZoom150          .addActionListener(this);
      this.menuiZoom200          .addActionListener(this);
      this.menuiCopier           .addActionListener(this);
      this.menuiColler           .addActionListener(this);


      this.menuiCharger    .setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK ));
      this.menuiSauvegarder.setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK ));
      this.menuiQuitter    .setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_F4,InputEvent.ALT_DOWN_MASK  ));
      this.menuiCopier     .setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK ));
      this.menuiColler     .setAccelerator (KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK ));
   }

   public void actionPerformed ( ActionEvent e )
   {
      if(e.getSource() == this.menuiCharger)
      {
         File         fichierSelectionner = null;
          JFileChooser selectionFichier = new JFileChooser();
        
        FileNameExtensionFilter filterTxt = new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt");
        FileNameExtensionFilter filterMC = new FileNameExtensionFilter("Fichiers MPM (*.MC)", "MC");
        
        selectionFichier.addChoosableFileFilter(filterTxt);
        selectionFichier.addChoosableFileFilter(filterMC);
        selectionFichier.setAcceptAllFileFilterUsed(true);
        
        selectionFichier.setCurrentDirectory(new File("."));

         if (selectionFichier.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            fichierSelectionner = selectionFichier.getSelectedFile();   

         try
         {
            
            if(fichierSelectionner.getName().substring(fichierSelectionner.getName().lastIndexOf('.') + 1).equals("MC"))
            {
               this.ctrl.initComplet(this.ctrl.getDateType(), "" + fichierSelectionner);
               ErrorUtils.showSucces("chargement d'un fichier de données complexe réussi");
            }
            else
            {
               this.ctrl.initProjet(this.ctrl.getDateRef(),this.ctrl.getDateType() , "" + fichierSelectionner);
               ErrorUtils.showSucces("chargement d'un fichier de données simple réussi");
            }

         }catch (Exception exc) { ErrorUtils.showError("erreur durant le chargement du fichier"); }
      }



      if(e.getSource() == this.menuiSauvegarder)
      {
         this.ctrl.sauvegarderFichier();
      }

      if(e.getSource() == this.menuiCopier)
      {
         this.ctrl.copierTache();
      }

      if(e.getSource() == this.menuiColler)
      {
         this.ctrl.collerTache();
      }

      if(e.getSource() == this.menuiQuitter)           System.exit(0);

      if(e.getSource() == this.menuiRajouterTache)     this.ctrl.afficherModification();

      if(e.getSource() == this.menuiSupprimerTache)    this.ctrl.afficherModification();

      if(e.getSource() == this.menuiChangerDureeTache) this.ctrl.afficherModification();

      if(e.getSource() == this.menuiZoom25 )           this.ctrl.getFrameMPM().setScale(0.25);
      if(e.getSource() == this.menuiZoom50 )           this.ctrl.getFrameMPM().setScale(0.50);
      if(e.getSource() == this.menuiZoom75 )           this.ctrl.getFrameMPM().setScale(0.75);
      if(e.getSource() == this.menuiZoom100)           this.ctrl.getFrameMPM().setScale(1.00);
      if(e.getSource() == this.menuiZoom150)           this.ctrl.getFrameMPM().setScale(1.50);
      if(e.getSource() == this.menuiZoom200)           this.ctrl.getFrameMPM().setScale(2.00);
   }
}