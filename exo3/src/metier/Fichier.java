package src.metier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.Controleur;
import src.ihm.PanelMPM;
import src.utils.ErrorUtils;

public class Fichier 
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur ctrl;
    private String     nomFichier;
    private boolean    estCritique;
    private String     theme;

    /*--------------*
     * Constructeur *
     *--------------*/
    public Fichier(Controleur ctrl, String nomFichier) 
    {
        this.ctrl       = ctrl;
        this.nomFichier = nomFichier;
        this.initTache(nomFichier);
    }

    /*---------------------------------*
     * Méthodes d'initialisation       *
     *---------------------------------*/
    public void initTache(String nomFichier) 
    {
        Scanner  scMPM;
        String   ligne;
        String   nom;
        int      duree;
        String[] precedents;

        this.ctrl.getTaches().clear();

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
                    
                    List<TacheMPM> tachesPrecedentes = new ArrayList<>();
                    for (String precedent : precedents) 
                    {
                        TacheMPM tachePrecedente = this.trouverTache(precedent.trim());
                        if (tachePrecedente != null) 
                            tachesPrecedentes.add(tachePrecedente);
                    }
                    
                    TacheMPM tache = new TacheMPM(nom, duree, tachesPrecedentes);
                    this.ctrl.getTaches().add(tache);    
                }
                else
                {
                    if (parties[0].equals("false"))      this.estCritique = false;
                    else if (parties[0].equals("true"))  this.estCritique = true;
                    else                                 this.theme = parties[0];
                }
            }
            
            scMPM.close();
            this.etablirRelationsSuivants();
            
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void etablirRelationsSuivants() 
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
        {
            List<TacheMPM> suivants  = new ArrayList<>();
            String         nomTache  = tache.getNom();
            
            for (TacheMPM autreTache : this.ctrl.getTaches()) 
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
    public void chargerFichierB() 
    {
        File             fichierSelectionner = null;
        JFileChooser     selectionFichier    = new JFileChooser();
        
        FileNameExtensionFilter filterTxt = new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt");
        FileNameExtensionFilter filterMC  = new FileNameExtensionFilter("Fichiers MPM (*.MC)", "MC");
        
        selectionFichier.addChoosableFileFilter(filterTxt);
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
            if (extension.equals("MC"))
            {
                this.ctrl.initComplet(this.ctrl.getDateType(), fichierSelectionner.getPath());
                ErrorUtils.showSucces("Chargement d'un fichier de données complexe réussi");
            }
            else
            {
                this.ctrl.initProjet(this.ctrl.getDateRef(), this.ctrl.getDateType(), fichierSelectionner.getPath());
                ErrorUtils.showSucces("Chargement d'un fichier de données simple réussi");
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

    public void sauvegarder() 
    {
        try 
        {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.nomFichier), "UTF8"));

            for (TacheMPM tache : this.ctrl.getTaches()) 
            {
                String precedentsStr = "";
                if (!tache.getPrecedents().isEmpty()) 
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < tache.getPrecedents().size(); i++) 
                    {
                        sb.append(tache.getPrecedents().get(i).getNom());
                        if (i < tache.getPrecedents().size() - 1)
                            sb.append(",");
                    }
                    precedentsStr = sb.toString();
                }

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

            for (TacheMPM tache : this.ctrl.getTaches()) 
            {
                String precedentsStr = "";
                if (!tache.getPrecedents().isEmpty()) 
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < tache.getPrecedents().size(); i++) 
                    {
                        sb.append(tache.getPrecedents().get(i).getNom());
                        if (i < tache.getPrecedents().size() - 1)
                            sb.append(",");
                    }
                    precedentsStr = sb.toString();
                }

                int    dateRefNum    = Integer.parseInt(dateRef.substring(0, 2));
                String dateRefSuffix = dateRef.substring(2);

                pw.println(tache.getNom() + "|" + 
                          tache.getDuree() + "|" + 
                          precedentsStr + "|" +
                          panelMere.getEntiteParNom(tache.getNom()).getX() + "|" + 
                          panelMere.getEntiteParNom(tache.getNom()).getY() + "|" +
                          (tache.getDateTot() + dateRefNum) + dateRefSuffix + "|" +
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
        for (TacheMPM tache : this.ctrl.getTaches()) 
        {
            if (tache.getNom().equals(tacheAjout.getNom())) 
            {
                tacheExiste = true;
                break;
            }
        }
        
        if (!tacheExiste) 
            this.ctrl.getTaches().add(tacheAjout);
        
        this.etablirRelationsSuivants();
        this.sauvegarder();
    }

    public void modifierTacheFichier(TacheMPM tacheModif) 
    {
        for (int i = 0; i < this.ctrl.getTaches().size(); i++) 
        {
            if (this.ctrl.getTaches().get(i).getNom().equals(tacheModif.getNom())) 
            {
                this.ctrl.getTaches().set(i, tacheModif);
                break;
            }
        }
        this.etablirRelationsSuivants();
        this.sauvegarder();
    }

    public void supprimerTacheFichier(TacheMPM tacheSuppr) 
    {
        this.ctrl.getTaches().removeIf(tache -> tache.getNom().equals(tacheSuppr.getNom()));

        this.sauvegarder();
        this.initTache(this.nomFichier);

        this.ctrl.getGraphe().calculerDates();
        this.ctrl.getGraphe().initCheminCritique();
        this.ctrl.getGraphe().initNiveauTaches();
    }



    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    public int[] getLocation(TacheMPM tache, String fichier) 
    {
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
                    int[] pos = new int[2];
                    pos[0] = Integer.parseInt(parties[3]);
                    pos[1] = Integer.parseInt(parties[4]);
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
            Scanner sc           = new Scanner(new File(this.nomFichier), "UTF-8");
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

    private TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    /*---------------------------------*
     * Accesseurs - Getters            *
     *---------------------------------*/
    public String  getNomFichier() { return this.nomFichier;  }
    public String  getTheme()      { return this.theme;       }
    public boolean isCritique()    { return this.estCritique; }
}