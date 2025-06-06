package src.Metier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import src.Ihm.PanelMPM;
import src.utils.ErrorUtils;

public class Fichier 
{
    private GrapheMPM      graphe;
    private List<TacheMPM> lstTacheMPMs;
    private String         nomFichier;
    
    public Fichier(GrapheMPM graphe, String nomFichier) 
    {
        this.graphe = graphe;
        this.lstTacheMPMs = new ArrayList<>();
        this.nomFichier = nomFichier;
        this.initTache(nomFichier);
    }

    public void initTache(String nomFichier) 
    {
        Scanner  scMPM;
        String   ligne;
        String   nom;
        int      duree;
        String[] precedents;

        this.lstTacheMPMs.clear();

        try {
            scMPM = new Scanner(new File(nomFichier), "UTF-8");

            while (scMPM.hasNextLine()) {
                ligne = scMPM.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] parties = ligne.split("\\|", -1);

                nom = parties[0];
                duree = Integer.parseInt(parties[1]);

                if (parties.length > 2 && !parties[2].isEmpty()) {
                    precedents = parties[2].split(",");
                } else {
                    precedents = new String[0];
                }

                List<TacheMPM> tachesPrecedentes = new ArrayList<>();
                for (String precedent : precedents) {
                    TacheMPM tachePrecedente = this.trouverTache(precedent.trim());
                    if (tachePrecedente != null) {
                        tachesPrecedentes.add(tachePrecedente);
                    }
                }

                TacheMPM tache = new TacheMPM(nom, duree, tachesPrecedentes);
                this.lstTacheMPMs.add(tache);
            }
            
            scMPM.close();
            
            this.etablirRelationsSuivants();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void etablirRelationsSuivants() {
        for (TacheMPM tache : this.lstTacheMPMs) {
            List<TacheMPM> suivants = new ArrayList<>();
            String nomTache = tache.getNom();
            
            for (TacheMPM autreTache : this.lstTacheMPMs) {
                if (autreTache == tache) continue;
                
                for (TacheMPM precedent : autreTache.getPrecedents()) {
                    if (precedent.getNom().equals(nomTache)) {
                        suivants.add(autreTache);
                        break;
                    }
                }
            }
            tache.setSuivants(suivants);
        }
    }

    public void ajouterTacheFichier(TacheMPM tacheAjout) {
        this.lstTacheMPMs.add(tacheAjout);
        this.etablirRelationsSuivants();
        this.sauvegarder();
    }

    public void modifierTacheFichier(TacheMPM tacheModif) {
        for (int i = 0; i < this.lstTacheMPMs.size(); i++) {
            if (this.lstTacheMPMs.get(i).getNom().equals(tacheModif.getNom())) {
                this.lstTacheMPMs.set(i, tacheModif);
                break;
            }
        }
        this.etablirRelationsSuivants();
        this.sauvegarder();
    }

    public void sauvegarder() {
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(this.nomFichier), "UTF8"));

            for (TacheMPM tache : this.lstTacheMPMs) {
                String precedentsStr = "";
                if (!tache.getPrecedents().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < tache.getPrecedents().size(); i++) {
                        sb.append(tache.getPrecedents().get(i).getNom());
                        if (i < tache.getPrecedents().size() - 1) {
                            sb.append(",");
                        }
                    }
                    precedentsStr = sb.toString();
                }

                pw.println(tache.getNom() + "|" + 
                          tache.getDuree() + "|" + 
                          precedentsStr);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sauvegarderFichier(String theme, boolean critique, String dateRef, PanelMPM panelMere) {
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream("listeTache.MC"), "UTF8"));

            for (TacheMPM tache : this.lstTacheMPMs) {
                String precedentsStr = "";
                if (!tache.getPrecedents().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < tache.getPrecedents().size(); i++) {
                        sb.append(tache.getPrecedents().get(i).getNom());
                        if (i < tache.getPrecedents().size() - 1) {
                            sb.append(",");
                        }
                    }
                    precedentsStr = sb.toString();
                }

                int dateRefNum = Integer.parseInt(dateRef.substring(0, 2));
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

            ErrorUtils.showSucces("sauvegarde du fichier réussi");
        } catch (Exception exc) {
            ErrorUtils.showError("erreur lors de la sauvegarde du fichier");
        }
    }

    public int[] getLocation(TacheMPM tache, String fichier) {
        try {
            Scanner scMPM = new Scanner(new File(fichier), "UTF-8");
            
            while (scMPM.hasNextLine()) {
                String ligne = scMPM.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] parties = ligne.split("\\|", -1);

                if (tache.getNom().equals(parties[0]) && parties.length >= 5) {
                    int[] pos = new int[2];
                    pos[0] = Integer.parseInt(parties[3]);
                    pos[1] = Integer.parseInt(parties[4]);
                    scMPM.close();
                    return pos;
                }
            }
            scMPM.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public String getLigne(String nom) {
        try {
            Scanner sc = new Scanner(new File(this.nomFichier), "UTF-8");
            String ligneActuelle = "";
            String ligneAvant = "";
            
            while (sc.hasNextLine()) {
                ligneAvant = ligneActuelle;
                ligneActuelle = sc.nextLine();
            }
            
            sc.close();
            
            if (nom.equals("theme")) return ligneAvant;  
            else if (nom.equals("critique")) return ligneActuelle; 
            else return "";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private TacheMPM trouverTache(String nom) {
        for (TacheMPM tache : this.lstTacheMPMs) {
            if (tache.getNom().equals(nom)) {
                return tache;
            }
        }
        return null;
    }

   public void supprimerTacheFichier(TacheMPM tacheSuppr) {
    this.lstTacheMPMs.removeIf(tache -> tache.getNom().equals(tacheSuppr.getNom()));
    
    this.etablirRelationsSuivants();
    // Sauvegarder les modifications
    this.sauvegarder();
    
    // Recharger complètement à partir du fichier pour garantir la cohérence
    this.initTache(this.nomFichier);
    
    // Recalculer toutes les données du graphe MPM
    this.graphe.calculerDates();
    this.graphe.initCheminCritique();
    this.graphe.initNiveauTaches();
    }

    // Getters
    public List<TacheMPM> getLstTacheMPMs() { return this.lstTacheMPMs;                                }
    public String         getNomFichier  () { return this.nomFichier;                                  }
    public String         getTheme       () { return getLigne("theme");                            }
    public boolean        isCritique     () { return getLigne("critique").equals("true"); }
}