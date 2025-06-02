public class TacheMPM 
{
    private String     nom        ;
    private String     duree      ;
    private String     dateTot    ;
    private String     dateTard   ;
    private String     marge      ;
    private TacheMPM[] precedents ;
    private TacheMPM[] suivants   ;

    public TacheMPM(String nom, String duree, TacheMPM[] precedents) 
    {
        this.nom        = nom             ;
        this.duree      = duree           ;
        this.precedents = precedents      ;
        this.suivants   = new TacheMPM[0] ;
        this.dateTot    = ""              ;
        this.dateTard   = ""              ;
        this.marge      = ""              ;
    }


    public String     getNom       () { return nom;        }
    public String     getDuree     () { return duree;      }
    public String     getDateTot   () { return dateTot;    }
    public String     getDateTard  () { return dateTard;   }
    public String     getMarge     () { return marge;      }
    public TacheMPM[] getPrecedents() { return precedents; }
    public TacheMPM[] getSuivants  () { return suivants;   }


    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Tâche: ").append(nom)
          .append(", Durée: ").append(duree)
          .append(", Date Tot: ").append(dateTot)
          .append(", Date Tard: ").append(dateTard)
          .append(", Marge: ").append(marge)
          .append(", Précédents: ");
        
          if (precedents.length == 0) {
            sb.append("Aucun ");
          }
          else {for (TacheMPM tache : precedents) {
            sb.append(tache.getNom()).append(" ");
        }}
        
        
        sb.append(", Suivants: ");
        if (suivants.length == 0) {
            sb.append("Aucun ");
          }
          else {for (TacheMPM tache : suivants) {
            sb.append(tache.getNom()).append(" ");
        }}
        
        return sb.toString().trim();
    }
    
}
