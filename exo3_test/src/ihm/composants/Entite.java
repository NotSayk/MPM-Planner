package ihm.composants;

import metier.TacheMPM;
import java.awt.*;

public class Entite {
    private TacheMPM tache;
    private int x;
    private int y;
    private int largeur;
    private int hauteur;
    private boolean selectionnee;

    public Entite(TacheMPM tache) {
        this.tache = tache;
        this.x = 50 + (tache.getNiveau() * 200);
        this.y = 50 + (tache.getNiveau() * 100);
        this.largeur = 100;
        this.hauteur = 50;
        this.selectionnee = false;
    }

    public void dessiner(Graphics g) {
        if (selectionnee) {
            g.setColor(Color.RED);
        } else if (tache.isCritique()) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.BLUE);
        }
        g.fillRect(x, y, largeur, hauteur);
        g.setColor(Color.WHITE);
        g.drawString(tache.getNom(), x + 10, y + 20);
        g.drawString("Durée: " + tache.getDuree(), x + 10, y + 40);
    }

    public boolean contientPoint(int px, int py) {
        return px >= x && px <= x + largeur && py >= y && py <= y + hauteur;
    }

    // Getters et Setters
    public TacheMPM getTache() { return tache; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getLargeur() { return largeur; }
    public int getHauteur() { return hauteur; }
    public boolean isSelectionnee() { return selectionnee; }
    public void setSelectionnee(boolean selectionnee) { this.selectionnee = selectionnee; }
} 