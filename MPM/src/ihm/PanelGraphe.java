package src.ihm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import src.Controleur;
import src.ihm.composants.Entite;
import src.metier.TacheMPM;
import src.utils.DateUtils;
import src.utils.ErrorUtils;
import src.utils.Utils;


public class PanelGraphe extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, 
                                                        ActionListener
    {
        /*--------------------*
         * Constantes         *
         *--------------------*/
        private static final int MARGE = 50;

        /*--------------------*
         * Attributs privés   *
         *--------------------*/
        private PanelMPM     parentPanel;  // Référence vers le panel parent
        private Controleur   ctrl;
        private List<Entite> lstEntites;
        private boolean      afficherDateTot;
        private boolean      afficherDateTard;
        private boolean      afficher;
        private int          numNiveauxTot;
        private int          numNiveauxTard;
        
        private JPopupMenu   popup;
        private JPopupMenu   popupEdit;

        private JMenuItem    jmCopier;
        private JMenuItem    jmSuprimer;
        private JMenuItem    jmDuree;
        private JMenuItem    jmNom;

        private Entite       entiteSelectionnee;
        private int          offsetX, offsetY;
        private Entite       dernierEntite;
        private double       scale;

        /*--------------*
         * Constructeur *
         *--------------*/
        public PanelGraphe(PanelMPM parent, Controleur ctrl)
        {
            this.parentPanel = parent;
            this.ctrl        = ctrl;
            this.lstEntites  = new ArrayList<>();
            
            this.popup       = new JPopupMenu();
            this.popupEdit   = new JPopupMenu();

            this.jmCopier    = new JMenuItem("Copier");
            this.jmSuprimer  = new JMenuItem("Supprimer");
            this.jmDuree     = new JMenuItem("Modifier durée");
            this.jmNom       = new JMenuItem("Modifier nom");

            this.popupEdit.add(this.jmCopier);
            this.popupEdit.add(this.jmSuprimer);
            this.popupEdit.addSeparator();
            this.popupEdit.add(this.jmDuree);
            this.popupEdit.add(this.jmNom);

            this.scale            = 1.0;
            this.afficherDateTot  = false;
            this.afficherDateTard = false;
            this.afficher         = false;
            this.numNiveauxTot    = -1;
            this.numNiveauxTard   = 0;

            this.setBackground         (Color.WHITE);
            this.addMouseListener      (this);
            this.addMouseMotionListener(this);
            this.addMouseWheelListener (this);

            this.jmCopier  .addActionListener(this);
            this.jmSuprimer.addActionListener(this);
            this.jmDuree   .addActionListener(this);
            this.jmNom     .addActionListener(this);

            this.updateSize();
        }

        /*----------------------------------*
         * Méthodes publiques               *
         *----------------------------------*/
        public void setEntites(List<Entite> entites) 
        {
            this.lstEntites = entites;
            this.updateSize();
        }

        public void setAfficherDateTot (boolean afficher) { this.afficherDateTot  = afficher; }
        public void setAfficherDateTard(boolean afficher) { this.afficherDateTard = afficher; }
        public void setAfficher        (boolean afficher) { this.afficher         = afficher; }

        public void setNumNiveauxTot (int num) { this.numNiveauxTot  = num; }
        public void setNumNiveauxTard(int num) { this.numNiveauxTard = num; }

        public double getScale()             { return this.scale;  }
        public void   setScale(double scale) { this.scale = scale; }

        /*----------------------------------*
         * Méthodes de gestion de la taille *
         *----------------------------------*/
        public void updateSize()
        {
            // Calculer la taille nécessaire en fonction des entités
            int[] taillePanneau = calculerTaillePanneau();
            
            this.setPreferredSize(new Dimension(taillePanneau[0], taillePanneau[1]));
            this.revalidate();
        }

        private int[] calculerTaillePanneau() 
        {
            int maxX = 0, maxY = 0;
            for (Entite entite : lstEntites) 
            {
                maxX = Math.max(maxX, entite.getX() + entite.getLargeur());
                maxY = Math.max(maxY, entite.getY() + entite.getHauteur());
            }
            
            // Ajouter une marge supplémentaire
            maxX += MARGE * 2;
            maxY += MARGE * 2;
            
            // Définir une taille minimale
            maxX = Math.max(maxX, 1000);
            maxY = Math.max(maxY, 800);
            
            return new int[]{maxX, maxY};
        }

        @Override
        public Dimension getPreferredSize() 
        {
            Dimension base = super.getPreferredSize();
            return new Dimension((int)(base.width * scale), (int)(base.height * scale));
        }

        /*----------------------------------*
         * Méthode de dessin                *
         *----------------------------------*/
        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.scale(scale, scale);
            super.paintComponent(g);
            
            dessinerEntites(g2);
            dessinerConnexions(g2);
            g2.dispose();
        }

        private void dessinerEntites(Graphics2D g2) 
        {
            for (Entite entite : lstEntites)
            {
                entite.paint(g2);
                this.dessinerDatesSurEntite(g2, entite);
            }
        }

        private void dessinerDatesSurEntite(Graphics2D g2, Entite entite) 
        {
            FontMetrics fm = g2.getFontMetrics();

            if ( afficherDateTot  && entite.getNiveauTache() <= numNiveauxTot  ) this.dessinerDateTot (g2, entite, fm);
            if ( afficherDateTard && entite.getNiveauTache() >= numNiveauxTard ) this.dessinerDateTard(g2, entite, fm);
        }

        private void dessinerDateTot(Graphics2D g2, Entite entite, FontMetrics fm) 
        {
            String texte = obtenirTexteDate(entite.getTache().getDateTot());
            int[] coordonnees = calculerCoordonneesTexte(entite, fm, texte, 16, 55);
            
            g2.setColor(new Color(0, 183, 14));
            g2.drawString(texte, coordonnees[0], coordonnees[1]);
        }

        private void dessinerDateTard(Graphics2D g2, Entite entite, FontMetrics fm) 
        {
            String texte = obtenirTexteDate(entite.getTache().getDateTard());
            int[] coordonnees = calculerCoordonneesTexte(entite, fm, texte, 52, 55);
            
            g2.setColor(new Color(255, 27, 14));
            g2.drawString(texte, coordonnees[0], coordonnees[1]);
        }

        private String obtenirTexteDate(int date) 
        {
            if(this.ctrl.isFormatDateTexte())
            {
                String dateStr = DateUtils.ajouterJourDate(this.ctrl.getDateRef(), date);
                if (dateStr.length() >= 5) return dateStr.substring(0, 5); // JJ/MM
                else                       return dateStr;
            }
            else 
                return "" + date;
        }

        private int[] calculerCoordonneesTexte(Entite entite, FontMetrics fm, String texte, int offsetX, int offsetY) 
        {
            int textWidth = fm.stringWidth(texte);
            int x = entite.getX() + offsetX - textWidth / 2;
            int y = entite.getY() + offsetY;
            return new int[]{x, y};
        }

        private void dessinerConnexions(Graphics2D g2) { this.parentPanel.dessinerConnexions(g2); }

        private Entite trouverEntiteAuPoint(int x, int y) 
        {
            for (Entite entite : this.lstEntites) 
            {
                if (x >= entite.getX() && x <= entite.getX() + entite.getLargeur() &&
                    y >= entite.getY() && y <= entite.getY() + entite.getHauteur()) 
                {
                    return entite;
                }
            }
            return null;
        }

        /*----------------------------------*
         * Gestionnaires d'événements souris *
         *----------------------------------*/
        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) 
            {
                int[] coordonneesEchelle = obtenirCoordonneesEchelle(e);
                this.gererSelectionEntite(coordonneesEchelle[0], coordonneesEchelle[1]);
            }
        }

        private int[] obtenirCoordonneesEchelle(MouseEvent e) 
        {
            int Xscale = (int)(e.getX() / scale);
            int Yscale = (int)(e.getY() / scale);
            return new int[]{Xscale, Yscale};
        }

        private void gererSelectionEntite(int x, int y) 
        {

            this.entiteSelectionnee = trouverEntiteAuPoint(x, y);
            if (entiteSelectionnee == null) return;

            offsetX = x - entiteSelectionnee.getX();
            offsetY = y - entiteSelectionnee.getY();
            popup.setVisible(false);
        }

        @Override
        public void mouseClicked(MouseEvent e) 
        {
            int[] coordonneesEchelle = obtenirCoordonneesEchelle(e);
            Entite entiteCliquee = trouverEntiteAuPoint(coordonneesEchelle[0], coordonneesEchelle[1]);

            if (e.getButton() == MouseEvent.BUTTON1) this.gererClicGauche(entiteCliquee);
            if (e.getButton() == MouseEvent.BUTTON3) this.gererClicDroit(e, entiteCliquee);
        }

        private void gererClicGauche(Entite entiteCliquee) 
        {
            if (entiteCliquee != null) 
            {
                this.parentPanel.reinitialiserCouleursEntites();
                this.parentPanel.setTacheSelectionnee(entiteCliquee.getTache());
                entiteCliquee.setCouleurContour(Color.BLUE);
                repaint();
            }
        }

        private void gererClicDroit(MouseEvent e, Entite entiteCliquee) 
        {
            this.popup.setVisible(false);
            if (entiteCliquee != null) 
                this.popupEdit.show(e.getComponent(),e.getX(),e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) 
        {
            if (this.entiteSelectionnee != null) 
            {
                int[] coordonneesEchelle = this.obtenirCoordonneesEchelle(e);
                this.deplacerEntite(coordonneesEchelle[0], coordonneesEchelle[1], e);
            }
        }

        private void deplacerEntite(int x, int y, MouseEvent e) 
        {
            int newX = x - offsetX;
            int newY = y - offsetY;

            if(newX < 0) newX = 0;
            if(newY < 0) newY = 0;

            this.entiteSelectionnee.setPosition(newX, newY);
            
            this.updateSize();
            this.scrollRectToVisible(new Rectangle(e.getX() - 100, e.getY() - 100, 100, 100));
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) 
        {
            // Ajuster les coordonnées en fonction du zoom
            int Xscale = (int)(e.getX() / scale);
            int Yscale = (int)(e.getY() / scale);

            Point screenPoint;
            int   entiteScreenX;
            int   entiteScreenY;
            int   entiteLargeurScaled;

            int   newX;
            int   newY;   
            
            Entite entite = this.trouverEntiteAuPoint(Xscale, Yscale);
            
            if (entite != null) 
            {
                if (entite != this.dernierEntite) 
                {
                    TacheMPM tache = entite.getTache();

                    String anterieur = Utils.formatVirgulePrecedents(tache);

                    // Popup menu
                    
                    popup.setVisible(false);
                    popup.removeAll();
                    popup.add(new JLabel("Infos sur: " + entite.getTache().getNom()));
                    popup.add(new JSeparator());
                    popup.add(new JLabel("• Antériorité: " + (anterieur.isEmpty() ? "Aucune" : anterieur)));
                    popup.add(new JLabel("• Date au plus tot: "  + DateUtils.ajouterJourDate(ctrl.getDateRef(), entite.getTache().getDateTot()) ));
                    popup.add(new JLabel("• Date au plus tard: " + DateUtils.ajouterJourDate(ctrl.getDateRef(), entite.getTache().getDateTard())));
                    popup.add(new JLabel("• Durée: "  + tache.getDuree()));
                    popup.add(new JSeparator());
                    popup.add(new JLabel("• Niveau: " + tache.getNiveau()));
                    popup.add(new JLabel("• Position: (" + entite.getX() + ", " + entite.getY() + ")"));
                    popup.add(new JLabel("• Chemin critique: " + (tache.estCritique() ? "Oui" : "Non")));
                    popup.add(new JSeparator());
                    popup.add(new JLabel("Clic droit pour modifier la tâche"));

                    popup.revalidate(); 
                    popup.pack();

                    this.dernierEntite = entite;

                    // Calculer la position du popup en tenant compte du zoom
                    screenPoint = this.getLocationOnScreen();
                    
                    // Position de l'entité à l'écran avec le zoom appliqué
                    entiteScreenX       = (int)(entite.getX() * scale);
                    entiteScreenY       = (int)(entite.getY() * scale);
                    entiteLargeurScaled = (int)(entite.getLargeur() * scale);

                    newX                = screenPoint.x + entiteScreenX + entiteLargeurScaled + 10;
                    newY                = screenPoint.y + entiteScreenY;                        

                    popup.setLocation(newX, newY);
                    popup.setVisible(true);
                }
            } 
            else 
            {
                if (popup.isVisible()) popup.setVisible(false);
                this.dernierEntite = null;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {}
        
        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        /*----------------------------------*
         * Gestionnaire de zoom             *
         *----------------------------------*/
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) 
        {
            if (e.isControlDown() || e.isMetaDown() || e.getPreciseWheelRotation() != 0) 
            {
                if (e.getWheelRotation() < 0) this.scale *= 1.1;
                else                          this.scale /= 1.1;
                this.scale = Math.max(0.2, Math.min(this.scale, 5.0)); // Limite le zoom
                revalidate();
                repaint();
            }
        }

        /*----------------------------------*
         * Gestionnaire d'actions           *
         *----------------------------------*/
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if (e.getSource() == this.jmCopier) this.ctrl.copierTache();
            if (e.getSource() == this.jmSuprimer) 
            {
                ErrorUtils.showSucces("La tâche a été supprimée avec succès !");

                if (this.entiteSelectionnee == null) return;

                if (this.entiteSelectionnee.getTache().getNom().equals("DEBUT") || 
                    this.entiteSelectionnee.getTache().getNom().equals("FIN")) 
                    return;
                
                this.parentPanel.supprimerTache(entiteSelectionnee.getTache());
                this.ctrl.getGrilleDonneesModel().refreshTab();
            }
            else if (e.getSource() == this.jmDuree) 
            {
                String dureeTache = JOptionPane.showInputDialog(this, "Modifier la durée de la tâche :", 
                                                                this.entiteSelectionnee.getTache().getDuree());
                if (this.entiteSelectionnee.getTache().getNom().equals("DEBUT") || 
                    this.entiteSelectionnee.getTache().getNom().equals("FIN")) 
                {
                    JOptionPane.showMessageDialog(this, "Impossible de modifier la durée de la tâche DEBUT ou FIN.", 
                                                  "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (dureeTache != null && !dureeTache.isEmpty())
                {
                    try 
                    {
                        int duree = Integer.parseInt(dureeTache);
                        if( duree < 0 ) 
                        {
                            JOptionPane.showMessageDialog(this, "La durée ne peut pas être négative.", 
                                                          "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        this.entiteSelectionnee.getTache().setDuree(duree);
                        this.ctrl.getGraphe().calculerDates();
                        this.ctrl.getGraphe().initCheminCritique();
                        this.ctrl.modifierTacheFichier(this.entiteSelectionnee.getTache());
                        this.parentPanel.initEntites();
                        repaint();
                        revalidate();   
                    } 
                    catch (NumberFormatException ex) 
                    {
                        JOptionPane.showMessageDialog(this, "Durée invalide. Veuillez entrer un nombre entier.", 
                                                      "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else if (e.getSource() == this.jmNom) 
            {
                String nomTache = JOptionPane.showInputDialog(this, "Modifier le nom de la tâche :", 
                                                              entiteSelectionnee.getTache().getNom());
                if (nomTache != null && !nomTache.isEmpty())
                {
                    if (this.ctrl.trouverTache(nomTache) != null) 
                    {
                        JOptionPane.showMessageDialog(this, "Une tâche avec ce nom existe déjà.", 
                                                      "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    this.entiteSelectionnee.getTache().setNom(nomTache);
                    this.ctrl.modifierTacheFichier(this.entiteSelectionnee.getTache());
                    this.parentPanel.initEntites();
                    repaint();
                }
            }
        }
    }