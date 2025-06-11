package src.metier;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une tâche dans le graphe MPM
 * 
 * Cette classe est le modèle de base pour les tâches du projet.
 * Elle gère :
 * - Les propriétés de base d'une tâche (nom, durée, dates)
 * - Les relations avec les autres tâches (prédécesseurs, successeurs)
 * - Les calculs de dates et de marges
 */
public class TacheMPM
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private String         nom;           // Nom de la tâche
    private int           duree;          // Durée en jours
    private int           dateTot;        // Date au plus tôt
    private int           dateTard;       // Date au plus tard
    private int           niveau;         // Niveau dans le graphe
    private boolean       critique;       // Indique si la tâche est critique
    private List<TacheMPM> precedents;    // Liste des tâches prédécesseurs
    private List<TacheMPM> suivants;      // Liste des tâches successeurs

    /*--------------*
     * Constructeur *
     *--------------*/
    /**
     * Crée une nouvelle tâche avec les paramètres spécifiés
     * 
     * @param nom Nom de la tâche
     * @param duree Durée en jours
     * @param precedents Liste des tâches prédécesseurs
     */
    public TacheMPM(String nom, int duree, List<TacheMPM> precedents)
    {
        this.nom = nom;
        this.duree = duree;
        this.dateTot = 0;
        this.dateTard = 0;
        this.niveau = 0;
        this.critique = false;
        this.precedents = precedents;
        this.suivants = new ArrayList<>();
    }

    /*---------------------------------*
     * Méthodes de calcul des dates    *
     *---------------------------------*/

    /*---------------------------------*
     * Accesseurs - Getters            *
     *---------------------------------*/
    public String         getNom()        { return this.nom;        }
    public int           getDuree()       { return this.duree;      }
    public int           getDateTot()     { return this.dateTot;    }
    public int           getDateTard()    { return this.dateTard;   }
    public int           getNiveau()      { return this.niveau;     }
    public boolean       isCritique()     { return this.critique;   }
    public List<TacheMPM> getPrecedents() { return this.precedents; }
    public List<TacheMPM> getSuivants()   { return this.suivants;   }
    public int           getMarge()  { return this.dateTard - this.dateTot; }

    /*---------------------------------*
     * Accesseurs - Setters            *
     *---------------------------------*/
    public void setNom(String nom)                { this.nom = nom;           }
    public void setDuree(int duree)               { this.duree = duree;       }
    public void setDateTot(int dateTot)           { this.dateTot = dateTot;   }
    public void setDateTard(int dateTard)         { this.dateTard = dateTard; }
    public void setNiveau(int niveau)             { this.niveau = niveau;     }
    public void setCritique(boolean critique)     { this.critique = critique; }
    public void setPrecedents(List<TacheMPM> p)   { this.precedents = p;      }
    public void setSuivants(List<TacheMPM> s)     { this.suivants = s;        }

    /*---------------------------------*
     * Méthodes de comparaison         *
     *---------------------------------*/
    /**
     * Compare deux tâches par leur nom
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        TacheMPM other = (TacheMPM) obj;
        return this.nom.equals(other.nom);
    }

    /**
     * Génère un hash code basé sur le nom de la tâche
     */
    @Override
    public int hashCode()
    {
        return this.nom.hashCode();
    }

    /**
     * Retourne une représentation textuelle de la tâche
     */
    @Override
    public String toString()
    {
        return this.nom + " (Durée: " + this.duree + " jours)";
    }
}