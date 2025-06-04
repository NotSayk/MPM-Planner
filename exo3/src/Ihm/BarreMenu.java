package src.Ihm;

import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.*;
import src.Controleur;

public class BarreMenu extends JMenuBar implements ActionListener
{
   private Controleur    ctrl;
   private PanelMPM      panelMere;

   private JMenuItem     menuiCharger;
   private JMenuItem     menuiSauvegarder;
   private JMenuItem     menuiQuitter;

   private JMenuItem     menuiRajouterTache;
   private JMenuItem     menuiSupprimerTache;
   private JMenuItem     menuiChangerDureeTache;

   public BarreMenu(Controleur ctrl, PanelMPM panelMere)
   {
      this.ctrl      = ctrl;
      this.panelMere = panelMere;


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
            this.ctrl.initialiserProjet(this.ctrl.getDateReference(),this.ctrl.getTypeDate() , "" + selectionFichier.getSelectedFile());
      }

      if(e.getSource() == this.menuiSauvegarder)
      {
         try
         {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("listeTache.MC"), "UTF8" ));

            for (src.Metier.TacheMPM tache:ctrl.getTaches() )
            {
               pw.println ( tache.getNom()                                                                                        + "|" + 
                            tache.getDuree()                                                                                      + "|" + 
                           (tache.getPrecedents().isEmpty() ? "" : String.join(",",
                            tache.getPrecedents().stream().map(src.Metier.TacheMPM::getNom).toArray(String[]::new)))              + "|" +
                            panelMere.getEntiteParNom(tache.getNom()).getX()                                                      + "|" + 
                            panelMere.getEntiteParNom(tache.getNom()).getY()                                                      + "|" +
                            (tache.getDateTot()  + Integer.parseInt(ctrl.getDateReference().substring(0, 2))) +
                             ctrl.getDateReference().substring(2)                                                      +"|" +
                            (tache.getDateTard() + Integer.parseInt(ctrl.getDateReference().substring(0, 2))) +
                             ctrl.getDateReference().substring(2));

            }
            pw.close();
         }
         catch (Exception exc){ exc.printStackTrace(); }
      }

      if(e.getSource() == this.menuiQuitter)
      {
         System.exit(0);
      }

      if(e.getSource() == this.menuiRajouterTache)
      {
         this.ctrl.afficherModification();
      }

      if(e.getSource() == this.menuiSupprimerTache)
      {
         this.ctrl.afficherModification();
      }

      if(e.getSource() == this.menuiChangerDureeTache)
      {
         this.ctrl.afficherModification();
      }
   }
}