package src.ihm.composants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import src.metier.TacheMPM;

/**
 * Représente une entité graphique dans le graphe MPM.
 * Gère l'affichage et le positionnement d'une tâche dans l'interface.
 */
public class Entite 
{
    /*--------------------*
     * Constantes         *
     *--------------------*/
    private static final int TAILLE_CASE = 70;
    private static final int DEMI_CASE   = TAILLE_CASE / 2;

    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private TacheMPM tache;
    private Point    location;
    private Point    locationInitial;
    private int      largeur;
    private int      hauteur;
    private Color    couleurContour;

    /*--------------*
     * Constructeur *
     *--------------*/
    /**
     * Crée une nouvelle entité pour une tâche.
     * @param tache Tâche à représenter
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Entite(TacheMPM tache, int x, int y) 
    {
        this.tache           = tache;
        this.location        = new Point(x, y);
        this.locationInitial = new Point(x, y);
        this.largeur         = Entite.TAILLE_CASE;
        this.hauteur         = Entite.TAILLE_CASE;
        this.couleurContour  = Color.BLACK; 
    }

    /*----------------------------------*
     * Méthodes de positionnement       *
     *----------------------------------*/
    /**
     * Définit la position de l'entité.
     * @param x Nouvelle position X
     * @param y Nouvelle position Y
     */
    public void setPosition(int x, int y) 
    {
        this.location.setLocation(x, y);
    }

    /**
     * Réinitialise la position à sa valeur initiale.
     */
    public void resetPosition() 
    {
        this.location.setLocation(this.locationInitial);
    }

    /*----------------------------------*
     * Méthodes de dimensionnement      *
     *----------------------------------*/
    /**
     * Définit les dimensions de l'entité.
     * @param largeur Nouvelle largeur
     * @param hauteur Nouvelle hauteur
     */
    public void setDimensions(int largeur, int hauteur) 
    {
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void setCouleurContour (Color couleur) { this.couleurContour = couleur; }
    
    // Getters
    public TacheMPM getTache      () { return this.tache;             }
    public Point    getLocation   () { return this.location;          }
    public int      getX          () { return this.location.x;        }
    public int      getY          () { return this.location.y;        }
    public int      getLargeur    () { return this.largeur;           }
    public int      getHauteur    () { return this.hauteur;           }
    public int      getNiveauTache() { return this.tache.getNiveau(); }

    /*----------------------------------*
     * Méthodes de dessin               *
     *----------------------------------*/
    /**
     * Dessine l'entité sur le contexte graphique.
     * @param g Contexte graphique
     */
    public void paint(Graphics g) 
    {
        int x = this.location.x;
        int y = this.location.y;

        // Dessin du rectangle principal
        g.setColor(this.couleurContour);
        g.drawRect(x, y, largeur, hauteur);
        
        // Dessin des lignes de séparation
        g.setColor(this.couleurContour);
        g.drawLine(x + Entite.DEMI_CASE, y + Entite.DEMI_CASE - 5, x + Entite.DEMI_CASE, y + 70);
        g.drawLine(x, y + 30, x + 70, y + 30);
        
        // Affichage du nom de la tâche
        String nomTache = tache.getNom();
        if (nomTache.length() > 6) 
            nomTache = nomTache.substring(0, 6) + "...";

        // Centrage du texte
        int stringWidth = g.getFontMetrics().stringWidth(nomTache);
        int textX = x + (largeur - stringWidth) / 2;
        int textY = y + Entite.DEMI_CASE - 20;

        g.drawString(nomTache, textX, textY);
    }
}