package src.Ihm;
import src.Controleur;


public class IhmCui 
{
    private Controleur controleur;
  

    public IhmCui(Controleur ctrl) 
    {
        System.out.println("Bienvenue dans l'application de gestion de projet MPM");
        this.controleur = ctrl                   ;
        this.afficherResultats()                 ;
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