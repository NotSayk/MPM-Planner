package src.ihm;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import src.Controleur;
import src.metier.TacheMPM;
import src.utils.ErrorUtils;

public class BarreMenu extends JMenuBar implements ActionListener
{
   private Controleur ctrl;
   
   private JMenuItem menuiCharger;
   private JMenuItem menuiSauvegarder;
   private JMenuItem menuiQuitter;

   private JMenuItem menuiModifierGraphe;
   private JMenuItem menuiCopier;
   private JMenuItem menuiColler;
   private JMenuItem menuiChercherTache;

   private JMenuItem menuiInfosCritique;

   private JMenuItem menuiZoom25;
   private JMenuItem menuiZoom50;
   private JMenuItem menuiZoom75;
   private JMenuItem menuiZoom100;
   private JMenuItem menuiZoom150;
   private JMenuItem menuiZoom200;

   private JMenuItem menuiChangerAffichage;

   public BarreMenu(Controleur ctrl)
   {
      this.ctrl = ctrl;

      /*----------------------------*/
      /* Création des composants    */
      /*----------------------------*/

      // Style moderne de la barre de menu
      this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
      this.setBackground(new Color(248, 249, 250));

      // les JMenu avec icônes
      JMenu menuFichier   = new JMenu("Fichier");
      JMenu menuEdition   = new JMenu("Edition");
      JMenu menuAffichage = new JMenu("Affichage");
      JMenu menuZoom      = new JMenu("Zoom");
      JMenu menuInfos     = new JMenu("Informations");

      // Ajout d'icônes aux menus (avec gestion d'erreur)
      menuFichier.setIcon(createMenuIcon("📁"));
      menuEdition.setIcon(createMenuIcon("✏️"));
      menuAffichage.setIcon(createMenuIcon("👁️"));
      menuZoom.setIcon(createMenuIcon("🔍"));
      menuInfos.setIcon(createMenuIcon("ℹ️"));

      // Création des éléments de menu
      this.menuiCharger           = new JMenuItem("Charger", createMenuIcon("📂"));
      this.menuiSauvegarder       = new JMenuItem("Sauvegarder", createMenuIcon("💾"));
      this.menuiQuitter           = new JMenuItem("Quitter", createMenuIcon("🚪"));

      this.menuiModifierGraphe     = new JMenuItem("Modifier le graphe", createMenuIcon("➕"));

      this.menuiCopier            = new JMenuItem("Copier", createMenuIcon("📋"));
      this.menuiColler            = new JMenuItem("Coller", createMenuIcon("📌"));
      this.menuiChercherTache     = new JMenuItem("Chercher une tâche", createMenuIcon("🔎"));

      this.menuiInfosCritique     = new JMenuItem("Chemins critiques", createMenuIcon("🚧"));

      this.menuiZoom25            = new JMenuItem("25%");
      this.menuiZoom50            = new JMenuItem("50%");
      this.menuiZoom75            = new JMenuItem("75%");
      this.menuiZoom100           = new JMenuItem("100%");
      this.menuiZoom150           = new JMenuItem("150%");
      this.menuiZoom200           = new JMenuItem("200%");

      this.menuiChangerAffichage  = new JMenuItem("Changer format d'affichage");

      // Application du style moderne
      this.styleMenu(menuFichier);
      this.styleMenu(menuEdition);
      this.styleMenu(menuAffichage);
      this.styleMenu(menuZoom);
      this.styleMenu(menuInfos);

      /*-------------------------------*/
      /* positionnement des composants */
      /*-------------------------------*/

      //rajout des items dans les menus
      menuFichier.add(this.menuiCharger);
      menuFichier.add(this.menuiSauvegarder);
      menuFichier.add(this.menuiCopier);
      menuFichier.add(this.menuiColler);
      menuFichier.addSeparator();
      menuFichier.add(this.menuiQuitter);

      menuEdition.add(this.menuiModifierGraphe);
      menuEdition.addSeparator();
      menuEdition.add(this.menuiChercherTache);

      menuInfos.add(this.menuiInfosCritique);  

      menuZoom.add(this.menuiZoom25);
      menuZoom.add(this.menuiZoom50);
      menuZoom.add(this.menuiZoom75);
      menuZoom.add(this.menuiZoom100);
      menuZoom.add(this.menuiZoom150);
      menuZoom.add(this.menuiZoom200);

      menuAffichage.add(menuZoom);
      menuAffichage.add(this.menuiChangerAffichage);

      //rajout des menus
      this.add(menuFichier);
      this.add(menuEdition);
      this.add(menuAffichage);
      this.add(menuInfos);

      /*-------------------------------*/
      /* Activation des composants     */
      /*-------------------------------*/
      this.menuiCharger    .addActionListener(this); 
      this.menuiSauvegarder.addActionListener(this); 
      this.menuiQuitter    .addActionListener(this); 

      this.menuiModifierGraphe.addActionListener(this);

      this.menuiInfosCritique .addActionListener(this);

      this.menuiZoom25 .addActionListener(this);
      this.menuiZoom50 .addActionListener(this);
      this.menuiZoom75 .addActionListener(this);
      this.menuiZoom100.addActionListener(this);
      this.menuiZoom150.addActionListener(this);
      this.menuiZoom200.addActionListener(this);
      this.menuiCopier .addActionListener(this);
      this.menuiColler .addActionListener(this);

      this.menuiChercherTache   .addActionListener(this);
      this.menuiChangerAffichage.addActionListener(this);

      // Raccourcis clavier
      this.menuiCharger         .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O , InputEvent.CTRL_DOWN_MASK));
      this.menuiSauvegarder     .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S , InputEvent.CTRL_DOWN_MASK));
      this.menuiQuitter         .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
      this.menuiCopier          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C , InputEvent.CTRL_DOWN_MASK));
      this.menuiColler          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V , InputEvent.CTRL_DOWN_MASK));
      this.menuiChercherTache   .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F , InputEvent.CTRL_DOWN_MASK));
      this.menuiChangerAffichage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I , InputEvent.CTRL_DOWN_MASK));
      this.menuiInfosCritique   .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H , InputEvent.CTRL_DOWN_MASK));
      this.menuiModifierGraphe  .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M , InputEvent.CTRL_DOWN_MASK));

      styleMenuItem( this.menuiCharger           );
      styleMenuItem( this.menuiSauvegarder       );
      styleMenuItem( this.menuiQuitter           );
      styleMenuItem( this.menuiModifierGraphe    );
      styleMenuItem( this.menuiCopier            );
      styleMenuItem( this.menuiColler            );
      styleMenuItem( this.menuiChercherTache     );
      styleMenuItem( this.menuiZoom25            );
      styleMenuItem( this.menuiZoom50            );
      styleMenuItem( this.menuiZoom75            );
      styleMenuItem( this.menuiZoom100           );
      styleMenuItem( this.menuiZoom150           );
      styleMenuItem( this.menuiZoom200           );
      styleMenuItem( this.menuiChangerAffichage  );
      styleMenuItem( this.menuiInfosCritique     );
   }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
      Object source = e.getSource();
      
      if (source == this.menuiZoom25  || source == this.menuiZoom50  || 
          source == this.menuiZoom75  || source == this.menuiZoom100 || 
          source == this.menuiZoom150 || source == this.menuiZoom200) 
      {
          
         if (source == this.menuiZoom25)
            this.ctrl.setZoom(0.25);
         else if (source == this.menuiZoom50)
            this.ctrl.setZoom(0.50);
         else if (source == this.menuiZoom75)
            this.ctrl.setZoom(0.75);
         else if (source == this.menuiZoom100)
            this.ctrl.setZoom(1.0);
         else if (source == this.menuiZoom150)
            this.ctrl.setZoom(1.5);
         else if (source == this.menuiZoom200)
            this.ctrl.setZoom(2.0);
          
      } 
      else 
      {
         if (source == this.menuiCharger)
            this.ctrl.chargerFichier();
         else if (source == this.menuiSauvegarder)
            this.ctrl.sauvegarder();
         else if (source == this.menuiQuitter)
            System.exit(0);
         else if (source == this.menuiModifierGraphe)
            this.ctrl.afficherModification();
         else if (source == this.menuiCopier)
            this.ctrl.copierTache();
         else if (source == this.menuiColler)
            this.ctrl.collerTache();
         else if (source == this.menuiChercherTache)
            this.chercherTache();
         else if (source == this.menuiChangerAffichage)
            this.ctrl.changerAffichage();
         else if (source == this.menuiInfosCritique) 
            this.ctrl.afficherCritiques();
      }
    }

    /**
    * Crée une icône à partir d'un caractère Unicode ou emoji
    */
   private ImageIcon createMenuIcon(String unicode) 
   {
      try 
      {
         // Créer une image 16x16 pixels
         java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
         java.awt.Graphics2D g2 = img.createGraphics();
         
         // Améliorer le rendu
         g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         
         // Définir la couleur NOIRE pour les emojis
         g2.setColor(Color.BLACK);
         g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
         
         // Dessiner l'emoji au centre
         java.awt.FontMetrics fm = g2.getFontMetrics();
         int x = (16 - fm.stringWidth(unicode)) / 2;
         int y = (16 - fm.getHeight()) / 2 + fm.getAscent();
         
         g2.drawString(unicode, x, y);
         g2.dispose();
         
         return new ImageIcon(img);
      } 
      catch (Exception e)  { return new ImageIcon(); }
   }

   /**
    * Style moderne pour les menus
    */
   private void styleMenu(JMenu menu) 
   {
      menu.setBackground(new Color(248, 249, 250));
      menu.setForeground(new Color(60, 60, 60));
      menu.setOpaque    (true);
      menu.setBorder    (BorderFactory.createEmptyBorder(8, 12, 8, 12));
      menu.setFont       (new Font("Segoe UI", Font.PLAIN, 12));
      
      // Effet hover pour les menus
      menu.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseEntered(MouseEvent e) 
         {
            menu.setBackground(new Color(230, 240, 250));
         }
         
         @Override
         public void mouseExited(MouseEvent e) 
         {
            menu.setBackground(new Color(248, 249, 250));
         }
      });
   }

   /**
    * Style moderne pour les éléments de menu
    */
   private void styleMenuItem(JMenuItem item) 
   {
      item.setBackground(Color.WHITE);
      item.setForeground(new Color(60, 60, 60));
      item.setOpaque    (true);
      item.setBorder    (BorderFactory.createEmptyBorder(6, 12, 6, 12));
      item.setFont      (new Font("Segoe UI", Font.PLAIN, 11));
      
      // Espacement entre l'icône et le texte
      item.setIconTextGap(8);
      
      // Effet hover
      item.addMouseListener(new MouseAdapter() 
      {
         @Override
         public void mouseEntered(MouseEvent e) 
         {
            item.setBackground(new Color(240, 248, 255));
            item.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
         }
         
         @Override
         public void mouseExited(MouseEvent e) 
         {
            item.setBackground(Color.WHITE);
            item.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
         }
      });
   }

      public void chercherTache() 
    {
        String nomTache = JOptionPane.showInputDialog(this.ctrl.getFrameMPM(), "Entrez le nom de la tâche à chercher :");
        if (nomTache == null || nomTache.trim().isEmpty()) 
        {
            ErrorUtils.showError("Le nom de la tâche ne peut pas être vide.");
            return;
        }
        
        TacheMPM tache = this.ctrl.trouverTache(nomTache);
        
        if (tache == null) 
        {
            ErrorUtils.showError("Aucune tâche trouvée avec le nom : " + nomTache);
            return;
        }
        
        this.ctrl.getFrameMPM().setTacheSelectionnee(tache);
    }
}