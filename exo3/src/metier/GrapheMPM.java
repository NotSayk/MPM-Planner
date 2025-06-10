package src.metier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import src.Controleur;
import src.ihm.composants.Entite;
import src.utils.DateUtils;

public class GrapheMPM
{
    private Controleur           ctrl;

    private String               dateRef ; 
    private char                 dateType;
    private int[]                niveaux ;

    private List<CheminCritique> lstChemins;
    private List<TacheMPM>       lstTacheMPMs;

    public GrapheMPM(Controleur ctrl)
    {
        this.ctrl         = ctrl;
        this.niveaux      = new int[1000];
        this.lstChemins   = new ArrayList<CheminCritique>();
        this.lstTacheMPMs = new ArrayList<TacheMPM>();
    }

    public void calculerDates() 
    {
        this.initDateTot ();
        this.initDateTard();
    }

    public void setDateFin(String dateFin) 
    {
        this.dateRef = DateUtils.ajouterJourDate(dateFin, -this.getDureeProjet());
        System.out.println("Durée du projet : " + this.getDureeProjet() + " jours");
        System.out.println("Date de référence mise à jour : " + this.dateRef);
        this.calculerDates();
    }

    private void initDateTot() 
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
        {
            if (!tache.getPrecedents().isEmpty()) 
            {
                int maxFinPrecedent = 0;
                for (TacheMPM precedent : tache.getPrecedents())  
                {
                    int finPrecedent = precedent.getDateTot() + precedent.getDuree();
                    if (finPrecedent > maxFinPrecedent)
                        maxFinPrecedent = finPrecedent;
                }
                tache.setDateTot(maxFinPrecedent);
            }
        }
    }

    private void initDateTard() 
    {
        for (int i = this.ctrl.getTaches().size() - 1; i >= 0; i--) 
        {
            TacheMPM tache = this.ctrl.getTaches().get(i);

            if (!tache.getSuivants().isEmpty()) 
            {
                int minDateTard = Integer.MAX_VALUE;
                for(TacheMPM tacheSuivantes : tache.getSuivants())
                {
                    if (tacheSuivantes.getDateTard() < minDateTard) 
                        minDateTard = tacheSuivantes.getDateTard();
                }
                tache.setDateTard(minDateTard - tache.getDuree());
                continue;
            }
            tache.setDateTard(tache.getDateTot());
        }
    }

    public void initNiveauTaches() 
    {
        for (TacheMPM tache : ctrl.getTaches()) tache.setNiveau(0);
        
        for (TacheMPM tache : ctrl.getTaches()) 
        {
            for (TacheMPM predecesseur : tache.getPrecedents()) 
                if (predecesseur.getNiveau() + 1 > tache.getNiveau()) tache.setNiveau(predecesseur.getNiveau() + 1);
            this.niveaux[tache.getNiveau()] += 1;
        }

    }

    public TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    // Getters et setters

    public int getDureeProjet() 
    {
        int dureeMax = 0;
        for (TacheMPM tache : this.ctrl.getTaches()) 
        {
            if (tache.getSuivants().isEmpty()) 
            {
                int finTache = tache.getDateTot() + tache.getDuree();
                if (finTache > dureeMax) dureeMax = finTache;
            }
        }
        return dureeMax;
    }

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

    public void initCheminCritique() 
    {
        TacheMPM fin   = this.ctrl.getTaches().get(this.ctrl.getTaches().size() - 1);
        TacheMPM debut = this.ctrl.getTaches().get(0);

        List<List<TacheMPM>> tousChemin   = new ArrayList<>();
        List<TacheMPM>       cheminActuel = new ArrayList<>();
        
        // Recherche récursive de tous les chemins critiques
        this.trouverTousCheminsCritiques(debut, fin, cheminActuel, tousChemin);
        
        // Afficher tous les chemins trouvés
        for (int i = 0; i < tousChemin.size(); i++) definirCritique(tousChemin.get(i));
    }
  
    private void trouverTousCheminsCritiques(TacheMPM actuelle, TacheMPM fin, 
                                            List<TacheMPM> cheminActuel, 
                                            List<List<TacheMPM>> tousChemin) 
    {
        // Ajouter la tâche actuelle au chemin
        cheminActuel.add(actuelle);
        
        // Si on atteint la fin, vérifier si le chemin est critique
        if (actuelle.equals(fin)) 
        {
            if (CheminCritique.estCheminCritique(cheminActuel))
                tousChemin.add(new ArrayList<>(cheminActuel));
        } 
        else 
        {
            // Continuer avec les successeurs critiques
            for (TacheMPM successeur : getSuccesseurs(actuelle))
                if (CheminCritique.estLienCritique(actuelle, successeur))
                    trouverTousCheminsCritiques(successeur, fin, cheminActuel, tousChemin);
        }
        
        cheminActuel.remove(cheminActuel.size() - 1);
    }

    private void definirCritique(List<TacheMPM> chemin) 
    {
        CheminCritique cheminCritique = new CheminCritique();
        
        for (TacheMPM tache : chemin) 
        {
            cheminCritique.ajouterTache(tache);
            tache.setCritique(true);
        }
        
        this.lstChemins.add(cheminCritique);
    }

    public void chargerEntites(String nomFichier)
    {
        for(Entite e : this.ctrl.getEntites())
        {
            int[] pos = this.ctrl.getFichier().getLocation(e.getTache(), nomFichier);
            e.setPosition(pos[0], pos[1]);
        }
    }

    // Dans GrapheMPM.java - Méthode corrigée
public void mettreAJourDureeTache(int index, int duree) 
{
    List<TacheMPM> taches = this.ctrl.getTaches();
    if (index >= 0 && index < taches.size()) 
    {
        TacheMPM tache = taches.get(index);
        tache.setDuree(duree);
        this.calculerDates();
        this.initCheminCritique();
        this.initNiveauTaches();
        this.ctrl.getFichier().modifierTacheFichier(tache);
        this.ctrl.afficherGraphe();
    } 
    else 
    {
        System.err.println("Index de tâche invalide : " + index);
    }
}
    
    public void modifierPrecedents(TacheMPM tache, String nouveauxPrecedents) {
        Set<TacheMPM> nouveauxPrecedentsSet = new HashSet<>();
        
        if (!nouveauxPrecedents.isEmpty()) {
            for (String nomTache : nouveauxPrecedents.split(",")) {
                TacheMPM precedent = this.trouverTache(nomTache.trim());
                if (precedent != null && !precedent.equals(tache)) {
                    nouveauxPrecedentsSet.add(precedent);
                }
            }
        }
        
        // Mettre à jour les relations
        for (TacheMPM ancienPrecedent : tache.getPrecedents()) {
            ancienPrecedent.getSuivants().remove(tache);
        }
        
        tache.setPrecedents(new ArrayList<>(nouveauxPrecedentsSet));
        for (TacheMPM precedent : nouveauxPrecedentsSet) {
            precedent.getSuivants().add(tache);
        }
        
        // Mettre à jour le graphe et l'interface
        this.ctrl.getFichier().modifierTacheFichier(tache);
        this.ctrl.initProjet(this.getDateRef(), this.getDateType(), this.ctrl.getFichier().getNomFichier());
    }

    
    public void modifierSuivants(TacheMPM tache, String nouveauxSuivants) {
        Set<TacheMPM> nouveauxSuivantsSet = new HashSet<>();
        
        if (!nouveauxSuivants.isEmpty()) {
            for (String nomTache : nouveauxSuivants.split(",")) {
                TacheMPM suivant = this.trouverTache(nomTache.trim());
                if (suivant != null && !suivant.equals(tache)) {
                    nouveauxSuivantsSet.add(suivant);
                }
            }
        }
        
        // Mettre à jour les relations
        for (TacheMPM ancienSuivant : tache.getSuivants()) {
            ancienSuivant.getPrecedents().remove(tache);
        }
        
        tache.setSuivants(new ArrayList<>(nouveauxSuivantsSet));
        for (TacheMPM suivant : nouveauxSuivantsSet) {
            suivant.getPrecedents().add(tache);
        }
        
        // Mettre à jour le graphe et l'interface
        this.ctrl.getFichier().modifierTacheFichier(tache);
        this.ctrl.initProjet(this.getDateRef(), this.getDateType(), this.ctrl.getFichier().getNomFichier());
    }

    private List<TacheMPM> getSuccesseurs(TacheMPM tache) 
    {
        List<TacheMPM> successeurs = new ArrayList<>();
        
        for (TacheMPM autreTache : this.ctrl.getTaches())
            if (autreTache.getPrecedents().contains(tache)) successeurs.add(autreTache);
        
        return successeurs;
    }
    

    public String              getDateRef     ()               { return dateRef          ; }
    public char                getDateType    ()               { return dateType         ; }
    public int                 getNiveauTache (TacheMPM tache) { return tache.getNiveau(); }
    public int[]               getNiveaux     ()               { return niveaux          ; }
    public List<TacheMPM>      getTaches      ()               { return this.lstTacheMPMs; }
    
    public void setDateRef(String dateRef) { this.dateRef = dateRef  ; }
    public void setDateType(char dateType) { this.dateType = dateType; }
}