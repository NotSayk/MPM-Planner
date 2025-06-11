package src.metier;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un chemin critique dans le graphe MPM
 * 
 * Cette classe gère :
 * - La liste des tâches formant un chemin critique
 * - La vérification des liens critiques entre les tâches
 * - Le calcul de la durée totale du chemin
 */
public class CheminCritique
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private List<TacheMPM> taches;    // Liste des tâches du chemin critique
    
    /*--------------*
     * Constructeur *
     *--------------*/
    /**
     * Crée un nouveau chemin critique vide
     */
    public CheminCritique()
    {
        this.taches = new ArrayList<>();
    }

    /*---------------------------------*
     * Méthodes de gestion des tâches  *
     *---------------------------------*/
    /**
     * Ajoute une tâche au chemin critique
     * 
     * @param tache La tâche à ajouter
     */
    public void ajouterTache(TacheMPM tache)
    {
        if (tache == null)
            throw new IllegalArgumentException("La tâche ne peut pas être nulle");
        this.taches.add(tache);
    }

    /**
     * Vérifie si un lien entre deux tâches est critique
     * Un lien est critique si la date de fin de la tâche précédente
     * est égale à la date de début de la tâche suivante
     * 
     * @param tache1 La première tâche
     * @param tache2 La deuxième tâche
     * @return true si le lien est critique, false sinon
     */
    public static boolean estLienCritique(TacheMPM tache1, TacheMPM tache2)
    {
        if (tache1 == null || tache2 == null)
            return false;
        return tache1.getDateTot() + tache1.getDuree() == tache2.getDateTot();
    }

    /**
     * Vérifie si une liste de tâches forme un chemin critique
     * Un chemin est critique si tous les liens entre les tâches sont critiques
     * 
     * @param taches La liste des tâches à vérifier
     * @return true si le chemin est critique, false sinon
     */
    public static boolean estCheminCritique(List<TacheMPM> taches)
    {
        if (taches == null || taches.size() < 2)
            return false;

        for (int i = 0; i < taches.size() - 1; i++)
            if (!estLienCritique(taches.get(i), taches.get(i + 1)))
                return false;

        return true;
    }

    /*---------------------------------*
     * Accesseurs - Getters            *
     *---------------------------------*/
    /**
     * Récupère la liste des tâches du chemin critique
     * 
     * @return La liste des tâches
     */
    public List<TacheMPM> getTaches()
    {
        return this.taches;
    }

    /**
     * Calcule la durée totale du chemin critique
     * 
     * @return La durée totale en jours
     */
    public int getDureeTotale()
    {
        if (this.taches.isEmpty())
            return 0;

        TacheMPM derniereTache = this.taches.get(this.taches.size() - 1);
        return derniereTache.getDateTot() + derniereTache.getDuree();
    }

    /*---------------------------------*
     * Méthodes de représentation      *
     *---------------------------------*/
    /**
     * Retourne une représentation textuelle du chemin critique
     * 
     * @return Une chaîne de caractères représentant le chemin
     */
    @Override
    public String toString()
    {
        if (this.taches.isEmpty())
            return "Chemin vide";

        StringBuilder sb = new StringBuilder();
        sb.append("Chemin critique : ");
        
        for (int i = 0; i < this.taches.size(); i++)
        {
            if (i > 0)
                sb.append(" -> ");
            sb.append(this.taches.get(i).getNom());
        }
        
        sb.append("\nDurée totale : ").append(this.getDureeTotale()).append(" jours");
        return sb.toString();
    }

    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    public boolean isEmpty() 
    {
        return this.taches.isEmpty();
    }

    public int size() 
    {
        return this.taches.size();
    }

    public void clear() 
    {
        this.taches.clear();
    }
}