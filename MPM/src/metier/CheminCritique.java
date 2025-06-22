package src.metier;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un chemin critique dans un graphe MPM.
 * Un chemin critique est une séquence de tâches où chaque tâche a une marge nulle
 * et où les tâches sont liées de manière à former un chemin continu.
 * Cette classe gère la création et la manipulation des chemins critiques.
 */
public class CheminCritique
{
    private ArrayList<TacheMPM> cheminCritique;
    
    /**
     * Constructeur de la classe CheminCritique.
     * Initialise une liste vide pour stocker les tâches du chemin.
     */
    public CheminCritique()
    {
        this.cheminCritique = new ArrayList<TacheMPM>();
    }

    /**
     * Ajoute une tâche au chemin critique.
     * @param tache Tâche à ajouter au chemin
     */
    public void ajouterTache(TacheMPM tache) 
    { 
        this.cheminCritique.add(tache); 
    }

    /**
     * Vérifie si le lien entre deux tâches est critique.
     * Un lien est critique si les deux tâches ont une marge nulle
     * et si la fin de la tâche précédente correspond au début de la tâche suivante.
     * @param precedent Tâche précédente
     * @param successeur Tâche suivante
     * @return true si le lien est critique, false sinon
     */
    public static boolean estLienCritique(TacheMPM precedent, TacheMPM successeur) 
    {
        return precedent.getMarge() == 0 && 
               successeur.getMarge() == 0 &&
               precedent.getDateTot() + precedent.getDuree() == successeur.getDateTot();
    }

    /**
     * Vérifie si une liste de tâches forme un chemin critique.
     * Un chemin est critique si toutes ses tâches ont une marge nulle.
     * @param chemin Liste des tâches à vérifier
     * @return true si le chemin est critique, false sinon
     */
    public static boolean estCheminCritique(List<TacheMPM> chemin) 
    {
        for (TacheMPM tache : chemin) 
            if (tache.getMarge() != 0) 
                return false;
        return true;
    }

    /*---------------------------------*
    /**
     * Retourne la liste des tâches du chemin critique
     * @return La liste des tâches du chemin
     */
    public ArrayList<TacheMPM> getLstTaches() { return this.cheminCritique; }

    /*---------------------------------*
     * Méthodes de représentation     *
     *---------------------------------*/
    /**
     * Retourne une représentation textuelle du chemin critique.
     * Les tâches sont séparées par des flèches (->).
     * @return Chaîne de caractères représentant le chemin
     */
    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Chemin Critique :\n");
        for (int i = 0; i < this.cheminCritique.size(); i++) 
        {
            TacheMPM tache = this.cheminCritique.get(i);
            sb.append(tache.getNom());
            if (i < this.cheminCritique.size() - 1) 
            {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    /**
     * Vérifie si le chemin critique est vide
     * @return true si le chemin est vide, false sinon
     */
    public boolean isEmpty() 
    {
        return this.cheminCritique.isEmpty();
    }

    /**
     * Retourne le nombre de tâches dans le chemin critique
     * @return Le nombre de tâches
     */
    public int size() 
    {
        return this.cheminCritique.size();
    }

    /**
     * Vide le chemin critique
     */
    public void clear() 
    {
        this.cheminCritique.clear();
    }
}