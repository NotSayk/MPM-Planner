package src.Ihm;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class BarreMenu extends JMenuBar implements ActionListener
{
   private JMenuItem     menuiCharger;
   private JMenuItem     menuiSauvegarder;
   private JMenuItem     menuiQuitter;

   private JMenuItem     menuiRajouterTache;
   private JMenuItem     menuiSupprimerTache;
   private JMenuItem     menuiChangerDureeTache;

   public BarreMenu()
   {
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


   }

   public void actionPerformed ( ActionEvent e )
   {
      return;
   }

}