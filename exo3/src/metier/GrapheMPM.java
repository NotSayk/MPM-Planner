package src.metier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.Controleur;
import src.ihm.PanelMPM;
import src.ihm.composants.Entite;
import src.utils.DateUtils;
import src.utils.ErrorUtils;
import src.utils.Utils;

public class GrapheMPM
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private List<CheminCritique> lstChemins;
    private List<TacheMPM>       lstTaches;
    
    private String               dateRef;
    private char                 dateType;
    private int[]                niveaux;
    private TacheMPM             tacheCopiee;
    private boolean              formatDateTexte = false;

    private String               nomFichier;
    private boolean              estCritique;
    private String               theme;

    /*--------------*
     * Constructeur *
     *--------------*/
    public GrapheMPM()
    {
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
        for (TacheMPM tache : this.lstTaches)
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
        for (int i = this.lstTaches.size() - 1; i >= 0; i--) 
        {
            TacheMPM tache = this.lstTaches.get(i);

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

    /*----------------------------------*
     * Méthodes de recherche de tâches  *
     *----------------------------------*/
    public TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.lstTaches) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    /*---------------------------------*
     * Méthodes du chemin critique     *
     *---------------------------------*/
    public void initCheminCritique() 
    {

        this.lstChemins.clear();
        TacheMPM fin   = this.lstTaches.get(this.lstTaches.size() - 1);
        TacheMPM debut = this.lstTaches.get(0);

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
                    this.trouverTousCheminsCritiques(successeur, fin, cheminActuel, tousChemin);
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
        
        for (TacheMPM autreTache : this.lstTaches)
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
        TacheMPM          fin = taches.remove(taches.size() - 1);
        
        if (position > taches.size()) 
            position = taches.size();
        
        taches.add(position, tache);
        taches.add(fin);

        String  themeActuel = this.theme;
        
        if (tache.getPrecedents().isEmpty() && position > 0) 
        {
            List<TacheMPM> precedents = new ArrayList<>();
            precedents.add(taches.get(position - 1));
            tache.setPrecedents(precedents);
        }
               
        this.theme = themeActuel;
        
        this.initNiveauTaches(); 
        this.calculerDates();
        this.initCheminCritique();
        this.ajouterTacheFichier(tache);

    }

    public boolean mettreAJourDureeTache(int index, int duree) 
    {
        List<TacheMPM> taches = this.lstTaches;
        if (index >= 0 && index < taches.size()) 
        {
            TacheMPM tache = taches.get(index);
            tache.setDuree(duree);
            
            this.calculerDates      ();
            this.initCheminCritique ();
            this.initNiveauTaches   ();
            this.modifierTacheFichier(tache);
            
            return true;
        } 
        else 
        {
            System.err.println("Index de tâche invalide : " + index);
            return false;
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
        
        this.modifierTacheFichier(tache);

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
        
        this.modifierTacheFichier(tache);
    }

    public void modifierSuivants(TacheMPM tache, String nouveauxSuivants) 
    {
        Set<TacheMPM> nouveauxSuivantsSet = new HashSet<>();
        
        if (!nouveauxSuivants.isEmpty()) 
        {
            for (String nomTache : nouveauxSuivants.split(",")) 
            {
                TacheMPM suivant = this.trouverTache(nomTache.trim());
                if (suivant != null && !suivant.equals(tache) && !suivant.getNom().equals("FIN")) 
                    nouveauxSuivantsSet.add(suivant);
            }
        }
        
        for (TacheMPM ancienSuivant : tache.getSuivants()) 
            ancienSuivant.getPrecedents().remove(tache);
        
        tache.setSuivants(new ArrayList<>(nouveauxSuivantsSet));
        for (TacheMPM suivant : nouveauxSuivantsSet) 
            suivant.getPrecedents().add(tache);
        
        this.modifierTacheFichier(tache);
    }

    /*---------------------------------*
     * Méthodes copier/coller          *
     *---------------------------------*/
    public void copierTache(TacheMPM tacheSelectionnee) 
    {
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
            ErrorUtils.showError("Aucune tâche à coller");
            return;
        }

        if (this.tacheCopiee.getNom().equals("DEBUT") || this.tacheCopiee.getNom().equals("FIN")) 
        {
            ErrorUtils.showError("Impossible de coller la tâche DEBUT ou FIN");
            return;
        }

        boolean tacheExiste = false;
        for (TacheMPM tache : this.lstTaches) 
        {
            if (tache.getNom().equals(this.tacheCopiee.getNom())) 
            {
                tacheExiste = true;
                break;
            }
        }

        if (!tacheExiste) 
        {
            ErrorUtils.showError("La tâche copiée n'existe pas dans le fichier");
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
        
        List<TacheMPM> precedentsCopies = new ArrayList<>(this.tacheCopiee.getPrecedents());
        TacheMPM nouvelleTache = new TacheMPM(nomFinal, this.tacheCopiee.getDuree(), precedentsCopies);
        
        this.ajouterTacheAPosition(nouvelleTache, this.getTaches().size() - 1);
        
        List<TacheMPM> suivantsCopies = new ArrayList<>(this.tacheCopiee.getSuivants());
        nouvelleTache.setSuivants(suivantsCopies);
        
        for (TacheMPM suivant : suivantsCopies) 
            suivant.getPrecedents().add(nouvelleTache);
        
    }

    public void collerTache(TacheMPM tacheOriginale)
    {
        if (tacheOriginale == null) 
            throw new IllegalArgumentException("La tâche à coller ne peut pas être nulle.");
        
        String nouveauNom = tacheOriginale.getNom() + "_copie";
        int    compteur   = 1;
        String nomFinal   = nouveauNom;
        
        while (this.trouverTache(nomFinal) != null) 
        {
            nomFinal = nouveauNom + compteur;
            compteur++;
        }
        
        List<TacheMPM> precedentsVides = new ArrayList<>();
        TacheMPM       nouvelleTache   = new TacheMPM(nomFinal, tacheOriginale.getDuree(), precedentsVides);
        List<TacheMPM> taches          = this.getTaches();
        TacheMPM       fin             = taches.remove(taches.size() - 1);
        
        taches.add(nouvelleTache);
        taches.add(fin);
        
        this.ajouterTacheFichier(nouvelleTache);
        this.calculerDates();
        this.initCheminCritique();
        this.initNiveauTaches();
    }

    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    public void chargerEntites(String nomFichier, List<Entite> lstEntites)
    {
        for (Entite e : lstEntites)
        {
            int[] pos = this.getLocation(e.getTache(), nomFichier);
            e.setPosition(pos[0], pos[1]);
        }
    }

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

    public void initTache(String nomFichier) 
    {
        this.nomFichier = nomFichier;

        Scanner        scMPM;
        String         ligne;
        String         nom;
        int            duree;
        String[]       precedents;
        List<TacheMPM> tachesPrecedentes;
        TacheMPM       tachePrecedente;
        TacheMPM       tache;

        this.lstTaches.clear();

        try 
        {
            scMPM = new Scanner(new File(nomFichier), "UTF-8");

            while (scMPM.hasNextLine()) 
            {
                ligne = scMPM.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] parties = ligne.split("\\|", -1);

                if (parties.length > 1)
                {   
                    nom   = parties[0];
                    duree = Integer.parseInt(parties[1]);
                    
                    if (parties.length > 2 && !parties[2].isEmpty()) 
                        precedents = parties[2].split(",");
                    else
                        precedents = new String[0];
                    
                    tachesPrecedentes = new ArrayList<>();
                    for (String precedent : precedents) 
                    {
                        tachePrecedente = this.trouverTache(precedent.trim());
                        if (tachePrecedente != null) 
                            tachesPrecedentes.add(tachePrecedente);
                    }
                    
                    tache = new TacheMPM(nom, duree, tachesPrecedentes);
                    this.lstTaches.add(tache);    
                }
                else
                {
                    switch (parties[0]) 
                    {
                        case "false" -> this.estCritique = false;
                        case "true"  -> this.estCritique = true;
                        default      -> this.theme       = parties[0];
                    }
                }
            }
            
            scMPM.close();
            this.verifierDebutFin();
            this.etablirRelationsSuivants();
            
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void verifierDebutFin() 
    {
        boolean  trouveDebut = false;
        boolean  trouveFin   = false;
        TacheMPM debut;
        TacheMPM fin;

        for (TacheMPM tache : this.lstTaches) 
        {
            if (tache.getNom().equals("DEBUT")) trouveDebut = true;
            if (tache.getNom().equals("FIN"))   trouveFin   = true;
        }

        if (!trouveDebut) 
        {
            debut = new TacheMPM("DEBUT", 0, new ArrayList<>());
            this.ajouterTacheAPosition(debut, 0);
            for (TacheMPM tache : this.lstTaches) 
            {
                if (tache.getPrecedents().isEmpty() && !tache.getNom().equals("DEBUT") && !tache.getNom().equals("FIN")) {
                    tache.getPrecedents().add(debut);
                    debut.getSuivants().add(tache);
                }
            }
        }
        
        if (!trouveFin) 
        {
            fin = new TacheMPM("FIN", 0, new ArrayList<>());
            lstTaches.add(fin);
            this.ajouterTacheFichier(fin);
        }
    }

    private void etablirRelationsSuivants() 
    {
        for (TacheMPM tache : this.lstTaches) 
        {
            List<TacheMPM> suivants  = new ArrayList<>();
            String         nomTache  = tache.getNom();
            
            for (TacheMPM autreTache : this.lstTaches) 
            {
                if (autreTache == tache) continue;
                
                for (TacheMPM precedent : autreTache.getPrecedents()) 
                {
                    if (precedent.getNom().equals(nomTache)) 
                    {
                        suivants.add(autreTache);
                        break;
                    }
                }
            }
            tache.setSuivants(suivants);
        }
    }

    /*---------------------------------*
     * Méthodes de gestion des fichiers*
     *---------------------------------*/
public void chargerFichierB(Controleur ctrl) 
{
    File             fichierSelectionner = null;
    JFileChooser     selectionFichier    = new JFileChooser();
    
    FileNameExtensionFilter filterData   = new FileNameExtensionFilter("Fichiers data (*.data)", "data");
    FileNameExtensionFilter filterMC     = new FileNameExtensionFilter("Fichiers MPM (*.MC)", "MC");
    
    selectionFichier.addChoosableFileFilter(filterData);
    selectionFichier.addChoosableFileFilter(filterMC);
    selectionFichier.setAcceptAllFileFilterUsed(true);
    selectionFichier.setCurrentDirectory(new File("."));

    if (selectionFichier.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        fichierSelectionner = selectionFichier.getSelectedFile();   

    try
    {
        if (fichierSelectionner == null) 
        {
            ErrorUtils.showError("Aucun fichier sélectionné");
            return;
        }
        
        String extension = fichierSelectionner.getName().substring(fichierSelectionner.getName().lastIndexOf('.') + 1);
        
        if (this.dateRef == null || this.dateRef.isEmpty()) 
        {
            this.dateRef = ctrl.getDateDuJour();
            this.dateType = 'D';
        }
        
        switch (extension) 
        {
            case "MC" -> 
            {
                niveaux = new int[1000];
                ctrl.initComplet(this.getDateType(), fichierSelectionner.getPath());
                ErrorUtils.showSucces("Chargement d'un fichier de données complexe réussi");
            }
            default -> 
            {
                niveaux = new int[1000];
                ctrl.initProjet(this.getDateRef(), this.getDateType(), fichierSelectionner.getPath());
                ErrorUtils.showSucces("Chargement d'un fichier de données simple réussi");
            }
        }
    }
    catch (NullPointerException e1) 
    {
        ErrorUtils.showError("Erreur lors de l'accès au fichier : " + e1.getMessage());
    } 
    catch (SecurityException e2) 
    {
        ErrorUtils.showError("Accès refusé au fichier : " + e2.getMessage());
    } 
    catch (Exception e3) 
    {
        ErrorUtils.showError("Erreur inattendue : " + e3.getMessage());
        e3.printStackTrace();
    }
}
    public void nouveauProjet() 
    {
        this.lstTaches.clear();
        this.lstChemins.clear();

        this.formatDateTexte = false;

        String nomBase = JOptionPane.showInputDialog(null, "Entrez le nom du projet :", "Nouveau Projet", JOptionPane.QUESTION_MESSAGE);
        if (nomBase == null || nomBase.trim().isEmpty()) 
        {
            nomBase = "nouveauProjet";
            ErrorUtils.showError("Aucun nom de projet fourni, utilisation du nom par défaut 'nouveauProjet'");
            return;
        }

        String extension = ".MC";
        String nomFichierFinal = nomBase + extension;
        int compteur = 1;
        
        File fichier = new File(nomFichierFinal);
        while (fichier.exists()) 
        {
            nomFichierFinal = nomBase + compteur + extension;
            fichier = new File(nomFichierFinal);
            compteur++;
        }
        
        this.nomFichier = nomFichierFinal;
        this.estCritique     = false;
        this.theme           = "LIGHT";

        TacheMPM tacheDebut = new TacheMPM("DEBUT", 0, new ArrayList<>());
        TacheMPM tacheFin   = new TacheMPM("FIN", 0, new ArrayList<>());
        
        this.lstTaches.add(tacheDebut);
        this.lstTaches.add(tacheFin);
        
        this.calculerDates();
        this.initCheminCritique();
        this.initNiveauTaches();

        this.sauvegarder();
    }

    public void sauvegarder() 
    {
        String precedentsStr;
        try 
        {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.nomFichier), "UTF8"));

            for (TacheMPM tache : this.lstTaches)
            {
                precedentsStr = Utils.formatVirgulePrecedents(tache);

                pw.println(tache.getNom() + "|" + tache.getDuree() + "|" + precedentsStr);
            }
            pw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void sauvegarderFichier(String theme, boolean critique, String dateRef, PanelMPM panelMere) 
    {
        try 
        {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("listeTache.MC"), "UTF8"));

            for (TacheMPM tache : this.lstTaches)
            {
                String precedentsStr = Utils.formatVirgulePrecedents(tache);

                int    dateRefNum    = Integer.parseInt(dateRef.substring(0, 2));
                String dateRefSuffix = dateRef.substring(2);

                pw.println(tache.getNom  () + "|" + 
                           tache.getDuree() + "|" + 
                           precedentsStr    + "|" +
                           panelMere.getEntiteParNom(tache.getNom()).getX()   + "|" + 
                           panelMere.getEntiteParNom(tache.getNom()).getY()   + "|" +
                           (tache.getDateTot()  + dateRefNum) + dateRefSuffix + "|" +
                           (tache.getDateTard() + dateRefNum) + dateRefSuffix);
            }
            
            pw.println(theme);
            pw.println(critique);
            pw.close();

            ErrorUtils.showSucces("Sauvegarde du fichier réussi");
        } 
        catch (Exception exc) 
        {
            ErrorUtils.showError("Erreur lors de la sauvegarde du fichier");
        }
    }

    /*---------------------------------*
     * Méthodes de gestion des tâches  *
     *---------------------------------*/
    public void ajouterTacheFichier(TacheMPM tacheAjout) 
    {
        boolean tacheExiste = false;
        for (TacheMPM tache : this.lstTaches)
        {
            if (tache.getNom().equals(tacheAjout.getNom())) 
            {
                tacheExiste = true;
                break;
            }
        }
        
        if (!tacheExiste) 
            this.lstTaches.add(tacheAjout);

        // Trouver d'abord le niveau maximum parmi toutes les tâches
        int niveauMax = 0;
        for (TacheMPM tache : this.lstTaches) 
        {
            if (!tache.getNom().equals("FIN") && tache.getNiveau() > niveauMax)
                niveauMax = tache.getNiveau();
        }
        
        // Ensuite traiter chaque tâche et placer FIN au niveau le plus élevé + 1
        TacheMPM tacheFin = this.trouverTache("FIN");
        if (tacheFin != null) tacheFin.setNiveau(niveauMax + 1);
        
        for (TacheMPM tache : this.lstTaches) 
        {
            if(tache.getNom().equals("DEBUT") || tache.getNom().equals("FIN")) 
                continue;
                
            if (tache.getSuivants().isEmpty() && tacheFin != null) 
            {
                tache.getSuivants().add(tacheFin);
                tacheFin.getPrecedents().add(tache);
            }
        }
        
        this.etablirRelationsSuivants();
        this.sauvegarder();
    }

    public void modifierTacheFichier(TacheMPM tacheModif) 
    {
        for (int i = 0; i < this.lstTaches.size(); i++) 
        {
            if (this.lstTaches.get(i).getNom().equals(tacheModif.getNom())) 
            {
                this.lstTaches.set(i, tacheModif);
                break;
            }
        }
        this.etablirRelationsSuivants();
        this.sauvegarder();
    }

    public void supprimerTacheFichier(TacheMPM tacheSuppr) 
    {
        for (TacheMPM tache : this.lstTaches) 
        {
            tache.getPrecedents().removeIf(pred -> pred.getNom().equals(tacheSuppr.getNom()));
            tache.getSuivants().removeIf(suiv -> suiv.getNom().equals(tacheSuppr.getNom()));
        }
        
        this.lstTaches.removeIf(tache -> tache.getNom().equals(tacheSuppr.getNom()));

        TacheMPM tacheDebut = this.trouverTache("DEBUT");
        for (TacheMPM tache : this.lstTaches) 
        {
            if (tache.getNom().equals("DEBUT")) 
                continue;
                
            if (tache.getPrecedents().isEmpty() && tacheDebut != null) 
            {
                tache.getPrecedents().add(tacheDebut);
                tacheDebut.getSuivants().add(tache);
            }
        }

        TacheMPM tacheFin = this.trouverTache("FIN");
        for (TacheMPM tache : this.lstTaches) 
        {
            if (tache.getNom().equals("FIN") || tache.getNom().equals("DEBUT")) 
                continue;
                
            if (tache.getSuivants().isEmpty() && tacheFin != null) 
            {
                tache.getSuivants().add(tacheFin);
                tacheFin.getPrecedents().add(tache);
            }
        }

        this.sauvegarder();
        this.initTache(this.nomFichier);

        this.calculerDates();
        this.initCheminCritique();
        this.initNiveauTaches();
    }



    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    public int[] getLocation(TacheMPM tache, String fichier) 
    {
        int[] pos;
        try 
        {
            Scanner scMPM = new Scanner(new File(fichier), "UTF-8");
            
            while (scMPM.hasNextLine()) 
            {
                String   ligne   = scMPM.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] parties = ligne.split("\\|", -1);

                if (tache.getNom().equals(parties[0]) && parties.length >= 5) 
                {
                    pos = new int[2];
                    pos[0]    = Integer.parseInt(parties[3]);
                    pos[1]    = Integer.parseInt(parties[4]);
                    scMPM.close();
                    return pos;
                }
            }
            scMPM.close();
        } catch (Exception e) { e.printStackTrace(); }
        
        return null;
    }

    public String getLigne(String nom) 
    {
        try 
        {
            Scanner sc            = new Scanner(new File(this.nomFichier), "UTF-8");
            String  ligneActuelle = "";
            String  ligneAvant    = "";
            
            while (sc.hasNextLine()) 
            {
                ligneAvant    = ligneActuelle;
                ligneActuelle = sc.nextLine();
            }
            
            sc.close();
            
            if      (nom.equals("theme"))    return ligneAvant;  
            else if (nom.equals("critique")) return ligneActuelle; 
            else                             return "";
            
        } catch (Exception e) { e.printStackTrace(); return ""; }
    }

    /*---------------------------------*
     * Accesseurs - Getters            *
     *---------------------------------*/
    public String               getDateRef()                      { return dateRef;              }
    public char                 getDateType()                     { return dateType;             }
    public int                  getNiveauTache(TacheMPM tache)    { return tache.getNiveau();    }
    public int[]                getNiveaux()                      { return niveaux;              }
    public List<TacheMPM>       getTaches()                       { return this.lstTaches;       }
    public boolean              isFormatDateTexte()               { return this.formatDateTexte; }
    public List<CheminCritique> getCheminsCritiques()             { return this.lstChemins;      }

    public String  getTheme  () { return this.theme;       }
    public boolean isCritique() { return this.estCritique; }
    
    /*---------------------------------*
     * Accesseurs - Setters            *
     *---------------------------------*/
    public void setDateRef        (String dateRef) { this.dateRef = dateRef;        }
    public void setDateType       (char dateType ) { this.dateType = dateType;      }
    public void setFormatDateTexte(boolean format) { this.formatDateTexte = format; }
    public String getNomFichier   ()               { return this.nomFichier;        }
}