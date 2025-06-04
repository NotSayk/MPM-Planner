package src.Ihm.composants;

import java.awt.Color;
import java.awt.Graphics;
import src.Metier.TacheMPM;

public class Entite 
{
    private TacheMPM tache;
    private int x, y;
    private int largeur, hauteur;
    
    private static final int TAILLE_CASE = 70;
    private static final int DEMI_CASE = TAILLE_CASE / 2;
    
    public Entite(TacheMPM tache, int x, int y) 
    {
        this.tache = tache;
        this.x = x;
        this.y = y;
        this.largeur = TAILLE_CASE;
        this.hauteur = TAILLE_CASE;
    }

    public void setPosition(int x, int y) 
    {
        this.x = x;
        this.y = y;
    }
    
    public void setDimensions(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
    
    public void paint(Graphics g) {
        // Dessin du rectangle principal
        g.setColor(Color.BLACK);
        g.drawRect(x, y, largeur, hauteur);
        //g.fillRect(x, y, largeur, hauteur);
        
        // Dessin des lignes de séparation
        g.setColor(Color.BLACK);
        g.drawLine(x + DEMI_CASE, y + DEMI_CASE - 5, x + DEMI_CASE, y + 70);
        g.drawLine(x, y + 30, x + 70, y + 30);
        
        // Affichage du nom de la tâche
        String nomTache = tache.getNom();
        g.drawString(nomTache, x + DEMI_CASE - 4 * nomTache.length(), y + DEMI_CASE - 20);
        
        // Affichage des dates
    
        String dateTot  = String.valueOf(tache.getDateTot()) ;
        String dateTard = String.valueOf(tache.getDateTard());
        
        g.setColor(Color.GREEN);
        g.drawString(dateTot, x + 15, y + 55);
        g.setColor(Color.RED);
        g.drawString(dateTard, x + 50, y + 55);
    }
    
    // Getters
    public TacheMPM getTache() { return tache;   }
    public int getX         () { return x;       }
    public int getY         () { return y;       }
    public int getLargeur   () { return largeur; }
    public int getHauteur   () { return hauteur; }
}