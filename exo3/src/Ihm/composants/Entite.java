package src.Ihm.composants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import src.Metier.TacheMPM;

public class Entite 
{
    private static final int TAILLE_CASE = 70;
    private static final int DEMI_CASE   = TAILLE_CASE / 2;

    private TacheMPM tache;

    private Point location;

    private Point locationInitial;

    private int      largeur;
    private int      hauteur;

    private Color    couleurContour;

    
    public Entite(TacheMPM tache, int x, int y) 
    {
        this.tache           = tache;

        this.location        = new Point(x, y);
        this.locationInitial = new Point(x, y);

        this.largeur        = TAILLE_CASE;
        this.hauteur        = TAILLE_CASE;

        this.couleurContour = Color.BLACK; 
    }

    public void setPosition(int x, int y) 
    {
        this.location.setLocation(x, y);
    }
    
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

    public void resetPosition() 
    {
        this.location.setLocation(this.locationInitial);
    }

    public void paint(Graphics g) {
        int x = this.location.x;
        int y = this.location.y;

        // Dessin du rectangle principal
        g.setColor(this.couleurContour);
        g.drawRect(x, y, largeur, hauteur);
        
        // Dessin des lignes de séparation
        g.setColor(this.couleurContour);
        g.drawLine(x + DEMI_CASE, y + DEMI_CASE - 5, x + DEMI_CASE, y + 70);
        g.drawLine(x, y + 30, x + 70, y + 30);
        
        // Affichage du nom de la tâche
        String nomTache = tache.getNom();
        g.drawString(nomTache, x + DEMI_CASE - 4 * nomTache.length(), y + DEMI_CASE - 20);
        
    }
}