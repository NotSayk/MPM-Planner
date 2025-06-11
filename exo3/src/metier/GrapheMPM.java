package src.metier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import src.Controleur;
import src.ihm.composants.Entite;
import src.utils.DateUtils;
import src.utils.ErrorUtils;

public class GrapheMPM
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur           ctrl;
    private String               dateRef;
    private char                 dateType;
    private int[]                niveaux;
    private List<CheminCritique> lstChemins;
    private List<TacheMPM>       lstTaches;
    private TacheMPM             tacheCopiee;
    private boolean              formatDateTexte = false;

    /*--------------*
     * Constructeur *
     *--------------*/
    public GrapheMPM(Controleur ctrl)
    {
        this.ctrl       = ctrl;
        this.niveaux    = new int[1000];
        this.lstChemins = new ArrayList<CheminCritique>();
        this.lstTaches  = new ArrayList<TacheMPM>();
    }

    /*---------------------------------*
     * Méthodes de calcul des dates    *
     *---------------------------------*/
    public void calculerDates() 
    {
        this.initDateTot();
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
                for (TacheMPM tacheSuivantes : tache.getSuivants())
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

    /*---------------------------------*
     * Méthodes de gestion des niveaux *
     *---------------------------------*/
    public void initNiveauTaches() 
    {
        for (TacheMPM tache : ctrl.getTaches()) 
            tache.setNiveau(0);
        
        for (TacheMPM tache : ctrl.getTaches()) 
        {
            for (TacheMPM predecesseur : tache.getPrecedents()) 
                if (predecesseur.getNiveau() + 1 > tache.getNiveau()) 
                    tache.setNiveau(predecesseur.getNiveau() + 1);
            this.niveaux[tache.getNiveau()] += 1;
        }
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

    public String afficherCheminsCritiques() 
    {
        String sRet = "";
        sRet += ("=== CHEMINS CRITIQUES ===\n\n");
        
        List<CheminCritique> chemins = this.ctrl.getCheminsCritiques();
        
        if (chemins.isEmpty()) 
        {
            sRet += ("Aucun chemin critique trouvé.\n");
        } 
        else 
        {
            for (CheminCritique chemin : chemins) 
                sRet += chemin.toString() + "\n";
        }
        
        return sRet;
    }

    /*----------------------------------*
     * Méthodes de recherche de tâches  *
     *----------------------------------*/
    public TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    public void chercherTache() 
    {
        String nomTache = JOptionPane.showInputDialog(this.ctrl.getFrameMPM(), "Entrez le nom de la tâche à chercher :");
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
        
        this.ctrl.getFrameMPM().setTacheSelectionnee(tache);
    }

    /*---------------------------------*
     * Méthodes du chemin critique     *
     *---------------------------------*/
    public void initCheminCritique() 
    {
        TacheMPM fin   = this.ctrl.getTaches().get(this.ctrl.getTaches().size() - 1);
        TacheMPM debut = this.ctrl.getTaches().get(0);

        List<List<TacheMPM>> tousChemin   = new ArrayList<>();
        List<TacheMPM>       cheminActuel = new ArrayList<>();
        
        this.trouverTousCheminsCritiques(debut, fin, cheminActuel, tousChemin);
        
        for (int i = 0; i < tousChemin.size(); i++) 
            definirCritique(tousChemin.get(i));
    }
  
    private void trouverTousCheminsCritiques(TacheMPM actuelle, TacheMPM fin, 
                                            List<TacheMPM> cheminActuel, 
                                            List<List<TacheMPM>> tousChemin) 
    {
        cheminActuel.add(actuelle);
        
        if (actuelle.equals(fin)) 
        {
            if (CheminCritique.estCheminCritique(cheminActuel))
                tousChemin.add(new ArrayList<>(cheminActuel));
        } 
        else 
        {
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

    private List<TacheMPM> getSuccesseurs(TacheMPM tache) 
    {
        List<TacheMPM> successeurs = new ArrayList<>();
        
        for (TacheMPM autreTache : this.ctrl.getTaches())
            if (autreTache.getPrecedents().contains(tache)) 
                successeurs.add(autreTache);
        
        return successeurs;
    }

    /*---------------------------------*
     * Méthodes de gestion des tâches  *
     *---------------------------------*/
    public void ajouterTacheAPosition(TacheMPM tache, int position) 
    {
        for (TacheMPM tacheCourante : this.getTaches()) 
        {
            if (tacheCourante.getNom().equals(tache.getNom())) 
                return;
        }
        
        List<TacheMPM> taches = this.getTaches();
        TacheMPM fin = taches.remove(taches.size() - 1);
        
        if (position > taches.size()) 
            position = taches.size();
        
        taches.add(position, tache);
        taches.add(fin);

        String  themeActuel         = this.ctrl.getTheme();
        boolean cheminCritiqueActuel = this.ctrl.getAfficher();
        
        List<TacheMPM> precedents = new ArrayList<>();
        precedents.add(taches.get(position - 1));
        tache.setPrecedents(precedents);
                
        this.initNiveauTaches(); 
        this.calculerDates();
        this.initCheminCritique();
        this.ctrl.getFichier().ajouterTacheFichier(tache);
        this.ctrl.afficherGraphe(); 
        this.ctrl.setTheme(themeActuel);
        this.ctrl.afficherCheminCritique(cheminCritiqueActuel);
    }

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
            
            double  zoom                = this.ctrl.getFrameMPM().getScale();
            String  themeActuel         = this.ctrl.getTheme();
            boolean cheminCritiqueActuel = this.ctrl.getAfficher();
            
            this.ctrl.afficherGraphe();
            this.ctrl.setTheme(themeActuel);
            this.ctrl.afficherCheminCritique(cheminCritiqueActuel);
            this.ctrl.getFrameMPM().getPanelMPM().setScale(zoom);
        } 
        else 
        {
            System.err.println("Index de tâche invalide : " + index);
        }
    }

    /*---------------------------------*
     * Méthodes de modification        *
     *---------------------------------*/
    public void modifierNom(TacheMPM tache, String nouveauNom) 
    {
        if (nouveauNom == null || nouveauNom.trim().isEmpty()) 
            throw new IllegalArgumentException("Le nom de la tâche ne peut pas être vide.");
        
        for (TacheMPM tacheCourante : this.getTaches()) 
        {
            if (tacheCourante.getNom().equals(nouveauNom)) 
                throw new IllegalArgumentException("Une tâche avec ce nom existe déjà.");
        }
        
        tache.setNom(nouveauNom);
        
        this.ctrl.getFichier().modifierTacheFichier(tache);
        this.ctrl.initProjet(this.getDateRef(), this.getDateType(), this.ctrl.getFichier().getNomFichier());
    }

    public void modifierPrecedents(TacheMPM tache, String nouveauxPrecedents) 
    {
        Set<TacheMPM> nouveauxPrecedentsSet = new HashSet<>();
        
        if (!nouveauxPrecedents.isEmpty()) 
        {
            for (String nomTache : nouveauxPrecedents.split(",")) 
            {
                TacheMPM precedent = this.trouverTache(nomTache.trim());
                if (precedent != null && !precedent.equals(tache)) 
                    nouveauxPrecedentsSet.add(precedent);
            }
        }
        
        for (TacheMPM ancienPrecedent : tache.getPrecedents()) 
            ancienPrecedent.getSuivants().remove(tache);
        
        tache.setPrecedents(new ArrayList<>(nouveauxPrecedentsSet));
        for (TacheMPM precedent : nouveauxPrecedentsSet) 
            precedent.getSuivants().add(tache);
        
        this.ctrl.getFichier().modifierTacheFichier(tache);
        this.ctrl.initProjet(this.getDateRef(), this.getDateType(), this.ctrl.getFichier().getNomFichier());
    }

    public void modifierSuivants(TacheMPM tache, String nouveauxSuivants) 
    {
        Set<TacheMPM> nouveauxSuivantsSet = new HashSet<>();
        
        if (!nouveauxSuivants.isEmpty()) 
        {
            for (String nomTache : nouveauxSuivants.split(",")) 
            {
                TacheMPM suivant = this.trouverTache(nomTache.trim());
                if (suivant != null && !suivant.equals(tache)) 
                    nouveauxSuivantsSet.add(suivant);
            }
        }
        
        for (TacheMPM ancienSuivant : tache.getSuivants()) 
            ancienSuivant.getPrecedents().remove(tache);
        
        tache.setSuivants(new ArrayList<>(nouveauxSuivantsSet));
        for (TacheMPM suivant : nouveauxSuivantsSet) 
            suivant.getPrecedents().add(tache);
        
        this.ctrl.getFichier().modifierTacheFichier(tache);
        this.ctrl.initProjet(this.getDateRef(), this.getDateType(), this.ctrl.getFichier().getNomFichier());
    }

    /*---------------------------------*
     * Méthodes copier/coller          *
     *---------------------------------*/
    public void copierTache() 
    {
        TacheMPM tacheSelectionnee = this.ctrl.getFrameMPM().getTacheSelectionnee();
        if (tacheSelectionnee != null) 
        {
            this.tacheCopiee = tacheSelectionnee;
            System.out.println("Tâche copiée : " + tacheSelectionnee.getNom());
        }
        else 
        {
            System.out.println("Aucune tâche sélectionnée pour la copie");
        }
    }

    public void collerTache() 
    {
        if (this.tacheCopiee == null) 
        {
            System.out.println("Aucune tâche à coller");
            return;
        }
        
        String nouveauNom = this.tacheCopiee.getNom() + "_copie";
        
        int    compteur = 1;
        String nomFinal = nouveauNom;
        while (this.trouverTache(nomFinal) != null) 
        {
            nomFinal = nouveauNom + compteur;
            compteur++;
        }
        
        TacheMPM nouvelleTache = new TacheMPM(nomFinal, this.tacheCopiee.getDuree(), new ArrayList<>());
        
        this.ajouterTacheAPosition(nouvelleTache, this.getTaches().size() - 1);
        
        System.out.println("Tâche collée : " + nomFinal);
        this.ctrl.getGrilleDonneesModel().refreshTab();
    }

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
        
        List<TacheMPM> taches = this.getTaches();
        TacheMPM fin = taches.remove(taches.size() - 1);
        taches.add(nouvelleTache);
        taches.add(fin);
        
        this.ctrl.getFichier().ajouterTacheFichier(nouvelleTache);
        this.calculerDates();
        this.initCheminCritique();
        this.initNiveauTaches();
        this.ctrl.afficherGraphe();
    }

    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    public void chargerEntites(String nomFichier)
    {
        for (Entite e : this.ctrl.getEntites())
        {
            int[] pos = this.ctrl.getFichier().getLocation(e.getTache(), nomFichier);
            e.setPosition(pos[0], pos[1]);
        }
    }

    public int getDureeProjet() 
    {
        int dureeMax = 0;
        for (TacheMPM tache : this.ctrl.getTaches()) 
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
    
    /*---------------------------------*
     * Accesseurs - Setters            *
     *---------------------------------*/
    public void setDateRef(String dateRef)                  { this.dateRef = dateRef;              }
    public void setDateType(char dateType)                  { this.dateType = dateType;            }
    public void setFormatDateTexte(boolean format)          { this.formatDateTexte = format;       }
}