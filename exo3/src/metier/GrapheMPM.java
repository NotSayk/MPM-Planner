package src.metier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import src.utils.DateUtils;
import src.utils.ErrorUtils;

/**
 * Classe représentant le graphe MPM (Méthode des Potentiels Métra)
 * 
 * Cette classe est le modèle principal de l'application.
 * Elle gère :
 * - Les calculs des dates (au plus tôt, au plus tard)
 * - La gestion des tâches et leurs relations
 * - Les chemins critiques
 * - Les niveaux des tâches
 */
public class GrapheMPM
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private String               dateRef;         // Date de référence du projet
    private char                 dateType;        // Type de date (Début/Fin)
    private int[]                niveaux;         // Tableau des niveaux des tâches
    private List<CheminCritique> lstChemins;      // Liste des chemins critiques
    private List<TacheMPM>       lstTaches;       // Liste des tâches du graphe
    private TacheMPM             tacheCopiee;     // Tâche en cours de copie
    private TacheMPM             tacheSelectionnee; // Tâche actuellement sélectionnée
    private boolean              formatDateTexte = false; // Format d'affichage des dates

    /*--------------*
     * Constructeur *
     *--------------*/
    public GrapheMPM()
    {
        this.niveaux    = new int[1000];
        this.lstChemins = new ArrayList<>();
        this.lstTaches  = new ArrayList<>();
    }

    /*---------------------------------*
     * Méthodes de calcul des dates    *
     *---------------------------------*/
    /**
     * Calcule les dates au plus tôt et au plus tard pour toutes les tâches
     */
    public void calculerDates() 
    {
        this.initDateTot();
        this.initDateTard();
    }

    /**
     * Définit la date de fin du projet et met à jour la date de référence
     */
    public void setDateFin(String dateFin) 
    {
        this.dateRef = DateUtils.ajouterJourDate(dateFin, -this.getDureeProjet());
        System.out.println("Durée du projet : " + this.getDureeProjet() + " jours");
        System.out.println("Date de référence mise à jour : " + this.dateRef);
        this.calculerDates();
    }

    /**
     * Initialise les dates au plus tôt pour toutes les tâches
     * Parcours le graphe en largeur en partant des tâches sans précédents
     */
    private void initDateTot() 
    {
        // Réinitialiser toutes les dates au plus tôt
        for (TacheMPM tache : this.lstTaches) 
            tache.setDateTot(0);

        // Trouver les tâches sans précédents (niveau 0)
        List<TacheMPM> tachesNiveau0 = new ArrayList<>();
        for (TacheMPM tache : this.lstTaches) 
            if (tache.getPrecedents().isEmpty()) 
                tachesNiveau0.add(tache);

        // Calculer les dates au plus tôt niveau par niveau
        int niveau = 0;
        while (!tachesNiveau0.isEmpty()) 
        {
            List<TacheMPM> tachesNiveauSuivant = new ArrayList<>();
            
            for (TacheMPM tache : tachesNiveau0) 
            {
                // Calculer la date au plus tôt de la tâche
                int dateTot = 0;
                for (TacheMPM precedent : tache.getPrecedents()) 
                    dateTot = Math.max(dateTot, precedent.getDateTot() + precedent.getDuree());
                
                tache.setDateTot(dateTot);
                this.niveaux[niveau]++;

                // Ajouter les tâches suivantes au niveau suivant
                for (TacheMPM suivant : tache.getSuivants()) 
                {
                    boolean tousPrecedentsTraites = true;
                    for (TacheMPM precedent : suivant.getPrecedents()) 
                        if (precedent.getDateTot() == 0 && precedent != tache) 
                        {
                            tousPrecedentsTraites = false;
                            break;
                        }
                    
                    if (tousPrecedentsTraites) 
                        tachesNiveauSuivant.add(suivant);
                }
            }
            
            tachesNiveau0 = tachesNiveauSuivant;
            niveau++;
        }
    }

    /**
     * Initialise les dates au plus tard pour toutes les tâches
     * Parcours le graphe en largeur en partant des tâches sans suivants
     */
    private void initDateTard() 
    {
        // Trouver la durée totale du projet
        int dureeProjet = 0;
        for (TacheMPM tache : this.lstTaches) 
            dureeProjet = Math.max(dureeProjet, tache.getDateTot() + tache.getDuree());

        // Réinitialiser toutes les dates au plus tard
        for (TacheMPM tache : this.lstTaches) 
            tache.setDateTard(dureeProjet);

        // Trouver les tâches sans suivants
        List<TacheMPM> tachesSansSuivants = new ArrayList<>();
        for (TacheMPM tache : this.lstTaches) 
            if (tache.getSuivants().isEmpty()) 
                tachesSansSuivants.add(tache);

        // Calculer les dates au plus tard niveau par niveau
        while (!tachesSansSuivants.isEmpty()) 
        {
            List<TacheMPM> tachesNiveauPrecedent = new ArrayList<>();
            
            for (TacheMPM tache : tachesSansSuivants) 
            {
                // Calculer la date au plus tard de la tâche
                int dateTard = dureeProjet;
                for (TacheMPM suivant : tache.getSuivants()) 
                    dateTard = Math.min(dateTard, suivant.getDateTard() - tache.getDuree());
                
                tache.setDateTard(dateTard);

                // Ajouter les tâches précédentes au niveau précédent
                for (TacheMPM precedent : tache.getPrecedents()) 
                {
                    boolean tousSuivantsTraites = true;
                    for (TacheMPM suivant : precedent.getSuivants()) 
                        if (suivant.getDateTard() == dureeProjet && suivant != tache) 
                        {
                            tousSuivantsTraites = false;
                            break;
                        }
                    
                    if (tousSuivantsTraites) 
                        tachesNiveauPrecedent.add(precedent);
                }
            }
            
            tachesSansSuivants = tachesNiveauPrecedent;
        }
    }

    /*---------------------------------*
     * Méthodes de gestion des niveaux *
     *---------------------------------*/
    /**
     * Initialise les niveaux de toutes les tâches
     */
    public void initNiveauTaches() 
    {
        for (TacheMPM tache : this.lstTaches) 
            tache.setNiveau(0);
        
        for (TacheMPM tache : this.lstTaches) 
        {
            for (TacheMPM predecesseur : tache.getPrecedents()) 
                if (predecesseur.getNiveau() + 1 > tache.getNiveau()) 
                    tache.setNiveau(predecesseur.getNiveau() + 1);
            this.niveaux[tache.getNiveau()] += 1;
        }
    }

    /**
     * Définit le niveau d'une tâche spécifique
     */
    public void setNiveauTache(TacheMPM tache, int niveau) 
    {
        if (niveau < 0 || niveau >= this.niveaux.length) 
        {
            System.err.println("Niveau invalide : " + niveau);
            return;
        }
        tache.setNiveau(niveau);
        this.niveaux[niveau] += 1;
    }

    /**
     * Génère une représentation textuelle des chemins critiques
     */
    public String afficherCheminsCritiques() 
    {
        StringBuilder sRet = new StringBuilder();
        sRet.append("=== CHEMINS CRITIQUES ===\n\n");
        
        List<CheminCritique> chemins = this.lstChemins;
        
        if (chemins.isEmpty()) 
            sRet.append("Aucun chemin critique trouvé.\n");
        else 
            for (CheminCritique chemin : chemins) 
                sRet.append(chemin.toString()).append("\n");
        
        return sRet.toString();
    }

    /*----------------------------------*
     * Méthodes de recherche de tâches  *
     *----------------------------------*/
    /**
     * Recherche une tâche par son nom
     */
    public TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.lstTaches) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    /**
     * Interface utilisateur pour rechercher une tâche
     */
    public void chercherTache() 
    {
        String nomTache = JOptionPane.showInputDialog(null, "Entrez le nom de la tâche à chercher :");
        if (nomTache == null || nomTache.trim().isEmpty()) 
        {
            ErrorUtils.showError("Le nom de la tâche ne peut pas être vide.");
            return;
        }
        
        TacheMPM tache = this.trouverTache(nomTache);
        
        if (tache == null) 
        {
            ErrorUtils.showError("Aucune tâche trouvée avec le nom : " + nomTache);
            return;
        }
        
        this.tacheSelectionnee = tache;
    }

    /*---------------------------------*
     * Méthodes du chemin critique     *
     *---------------------------------*/
    /**
     * Initialise les chemins critiques du graphe
     */
    public void initCheminCritique() 
    {
        this.lstChemins.clear();
        List<TacheMPM> cheminActuel = new ArrayList<>();
        Set<TacheMPM> tachesVisitees = new HashSet<>();
        
        // Trouver les tâches sans précédents
        for (TacheMPM tache : this.lstTaches) 
        {
            if (tache.getPrecedents().isEmpty()) 
            {
                this.explorerCheminCritique(tache, cheminActuel, tachesVisitees);
            }
        }
    }
  
    /**
     * Explore récursivement les chemins critiques à partir d'une tâche
     */
    private void explorerCheminCritique(TacheMPM tache, List<TacheMPM> cheminActuel, Set<TacheMPM> tachesVisitees) 
    {
        cheminActuel.add(tache);
        tachesVisitees.add(tache);
        
        // Si la tâche n'a pas de suivants, c'est une fin de chemin
        if (tache.getSuivants().isEmpty()) 
        {
            // Vérifier si le chemin est critique
            boolean estCritique = true;
            for (TacheMPM t : cheminActuel) 
            {
                if (t.getDateTard() - t.getDateTot() != 0) 
                {
                    estCritique = false;
                    break;
                }
            }
            
            if (estCritique) 
            {
                CheminCritique cheminCritique = new CheminCritique();
                for (TacheMPM t : cheminActuel)
                    cheminCritique.ajouterTache(t);
                this.lstChemins.add(cheminCritique);
            }
        }
        else 
        {
            // Explorer les tâches suivantes
            for (TacheMPM suivant : tache.getSuivants()) 
            {
                if (!tachesVisitees.contains(suivant)) 
                {
                    this.explorerCheminCritique(suivant, cheminActuel, tachesVisitees);
                }
            }
        }
        
        cheminActuel.remove(cheminActuel.size() - 1);
        tachesVisitees.remove(tache);
    }

    /*---------------------------------*
     * Méthodes de gestion des tâches  *
     *---------------------------------*/
    /**
     * Ajoute une tâche à une position donnée
     */
    public void ajouterTacheAPosition(TacheMPM tache, int position) 
    {
        for (TacheMPM tacheCourante : this.lstTaches) 
            if (tacheCourante.getNom().equals(tache.getNom())) 
                return;
        
        List<TacheMPM> taches = this.lstTaches;
        TacheMPM fin = taches.remove(taches.size() - 1);
        
        if (position > taches.size()) 
            position = taches.size();
        
        taches.add(position, tache);
        taches.add(fin);
        
        List<TacheMPM> precedents = new ArrayList<>();
        precedents.add(taches.get(position - 1));
        tache.setPrecedents(precedents);
                
        this.initNiveauTaches(); 
        this.calculerDates();
        this.initCheminCritique();
        this.lstTaches.add(tache);
        this.calculerDates();
        this.initCheminCritique();
        this.initNiveauTaches();
    }

    /**
     * Met à jour la durée d'une tâche et recalcule les dates
     */
    public void mettreAJourDureeTache(int index, int duree) 
    {
        if (index >= 0 && index < this.lstTaches.size()) 
        {
            this.lstTaches.get(index).setDuree(duree);
            this.calculerDates();
            this.initCheminCritique();
        }
    }

    /*---------------------------------*
     * Méthodes de modification        *
     *---------------------------------*/
    /**
     * Modifie le nom d'une tâche
     */
    public void modifierNom(TacheMPM tache, String nouveauNom) 
    {
        if (nouveauNom == null || nouveauNom.trim().isEmpty()) 
            throw new IllegalArgumentException("Le nom de la tâche ne peut pas être vide.");
        
        for (TacheMPM tacheCourante : this.lstTaches) 
            if (tacheCourante.getNom().equals(nouveauNom)) 
                throw new IllegalArgumentException("Une tâche avec ce nom existe déjà.");
        
        tache.setNom(nouveauNom);
    }

    /**
     * Modifie les prédécesseurs d'une tâche
     */
    public void modifierPrecedents(TacheMPM tacheModifier, String nouvelleValeur) 
    {
        List<TacheMPM> nouveauxPrecedents = new ArrayList<>();
        String[] nomsPrecedents = nouvelleValeur.split(",");
        
        for (String nom : nomsPrecedents) 
        {
            nom = nom.trim();
            if (!nom.isEmpty()) 
            {
                TacheMPM precedent = this.trouverTache(nom);
                if (precedent != null) 
                    nouveauxPrecedents.add(precedent);
            }
        }
        
        tacheModifier.setPrecedents(nouveauxPrecedents);
        this.calculerDates();
        this.initCheminCritique();
    }

    /**
     * Modifie les successeurs d'une tâche
     */
    public void modifierSuivants(TacheMPM tacheModifier, String nouvelleValeur) 
    {
        List<TacheMPM> nouveauxSuivants = new ArrayList<>();
        String[] nomsSuivants = nouvelleValeur.split(",");
        
        for (String nom : nomsSuivants) 
        {
            nom = nom.trim();
            if (!nom.isEmpty()) 
            {
                TacheMPM suivant = this.trouverTache(nom);
                if (suivant != null) 
                    nouveauxSuivants.add(suivant);
            }
        }
        
        tacheModifier.setSuivants(nouveauxSuivants);
        this.calculerDates();
        this.initCheminCritique();
    }

    /*---------------------------------*
     * Méthodes copier/coller          *
     *---------------------------------*/
    /**
     * Copie la tâche sélectionnée
     */
    public void copierTache() 
    {
        if (this.tacheSelectionnee != null) 
            this.tacheCopiee = this.tacheSelectionnee;
    }

    /**
     * Colle la tâche précédemment copiée
     */
    public void collerTache() 
    {
        if (this.tacheCopiee != null) 
        {
            TacheMPM nouvelleTache = new TacheMPM(
                this.tacheCopiee.getNom() + "_copie",
                this.tacheCopiee.getDuree(),
                new ArrayList<>(this.tacheCopiee.getPrecedents())
            );
            
            this.lstTaches.add(nouvelleTache);
            this.calculerDates();
            this.initCheminCritique();
        }
    }

    /**
     * Colle une tâche spécifique
     */
    public void collerTache(TacheMPM tacheOriginale)
    {
        if (tacheOriginale == null) 
            throw new IllegalArgumentException("La tâche à coller ne peut pas être nulle.");
        
        String nouveauNom = tacheOriginale.getNom() + "_copie";
        
        int    compteur = 1;
        String nomFinal = nouveauNom;
        while (this.trouverTache(nomFinal) != null) 
        {
            nomFinal = nouveauNom + compteur;
            compteur++;
        }
        
        List<TacheMPM> precedentsVides = new ArrayList<>();
        TacheMPM nouvelleTache = new TacheMPM(nomFinal, tacheOriginale.getDuree(), precedentsVides);
        
        List<TacheMPM> taches = this.lstTaches;
        TacheMPM fin = taches.remove(taches.size() - 1);
        taches.add(nouvelleTache);
        taches.add(fin);
        
        this.calculerDates();
        this.initCheminCritique();
        this.initNiveauTaches();
    }

    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    /**
     * Charge les entités depuis un fichier
     */
    public void chargerEntites(String nomFichier)
    {
        // Assuming getEntites is called elsewhere in the code
        // This method should be implemented to set positions for Entite objects
    }

    /**
     * Calcule la durée totale du projet
     */
    public int getDureeProjet() 
    {
        int dureeMax = 0;
        for (TacheMPM tache : this.lstTaches) 
        {
            if (tache.getSuivants().isEmpty()) 
            {
                int finTache = tache.getDateTot() + tache.getDuree();
                if (finTache > dureeMax) 
                    dureeMax = finTache;
            }
        }
        return dureeMax;
    }

    /*---------------------------------*
     * Accesseurs - Getters            *
     *---------------------------------*/
    public String         getDateRef()                      { return dateRef;              }
    public char           getDateType()                     { return dateType;             }
    public int            getNiveauTache(TacheMPM tache)    { return tache.getNiveau();    }
    public int[]          getNiveaux()                      { return niveaux;              }
    public List<TacheMPM> getTaches()                       { return this.lstTaches;       }
    public boolean        isFormatDateTexte()               { return this.formatDateTexte; }
    public List<CheminCritique> getCheminsCritiques()       { return this.lstChemins;      }
    public TacheMPM       getTacheSelectionnee()            { return this.tacheSelectionnee; }
    
    /*---------------------------------*
     * Accesseurs - Setters            *
     *---------------------------------*/
    public void setDateRef(String dateRef)                  { this.dateRef = dateRef;              }
    public void setDateType(char dateType)                  { this.dateType = dateType;            }
    public void setFormatDateTexte(boolean format)          { this.formatDateTexte = format;       }
    public void setTacheSelectionnee(TacheMPM tache)        { this.tacheSelectionnee = tache;      }
}