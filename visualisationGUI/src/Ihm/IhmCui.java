package src.Ihm;
import java.util.Scanner;

import src.Controleur;


public class IhmCui 
{
    private Controleur controleur;
    private Scanner    scanner   ;

  

    public IhmCui(Controleur ctrl) 
    {
        System.out.println("Bienvenue dans l'application de gestion de projet MPM");
        this.scanner    = new Scanner(System.in) ;
        this.controleur = ctrl                   ;
        this.gererInterface()                    ;
    }

    private void gererInterface() 
    {
        String dateRef  = this.demanderDateReference() ;
        char   typeDate = this.demanderTypeDate()      ;
        
        this.controleur.initialiserProjet(dateRef, typeDate);
        
        this.afficherResultats();
    }

    private String demanderDateReference() 
    {
        System.out.print("Voulez vous utiliser la date du jour ? (O/N) : ");
        String reponse = this.scanner.nextLine().trim().toUpperCase();
        
        if (reponse.equals("O")) 
        {
            return this.controleur.getDateDuJour();
        }
        
        System.out.print("Entrez la date de référence (jj/mm/aaaa) : ");
        String dateRef = this.scanner.nextLine().trim();
        
        while (!dateRef.matches("\\d{2}/\\d{2}/\\d{4}")) 
        {
            System.out.print("Format invalide. Veuillez entrer la date au format jj/mm/aaaa : ");
            dateRef = this.scanner.nextLine().trim();
        }
        
        return dateRef;
    }

    private char demanderTypeDate() 
    {
        System.out.print("Choisissez le type de date (D pour début, F pour fin) : ");
        String choix = this.scanner.nextLine().trim().toUpperCase();

        while (!choix.equals("D") && !choix.equals("F")) 
        {
            System.out.print("Choix invalide. Veuillez entrer 'D' pour début ou 'F' pour fin : ");
            choix = this.scanner.nextLine().trim().toUpperCase();
        }

        return choix.charAt(0);
    }

    private void afficherResultats() 
    {
        System.out.println(this.controleur.getGrapheString());
        System.out.println("Ajout de la FAQ");
        System.out.println("----------------------------------------");
        System.out.println("Date de référence : " + this.controleur.getDateReference());
        System.out.println("Duree du projet : " + this.controleur.getDureeProjet() + " jours");
        System.out.println(this.controleur.getDateProjet());
    }
}