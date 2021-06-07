package main;

public class Formation {
    private int specialite;
    private int competence;
    private int jour;
    private int hdebut;
    private int hfin;

    public Formation(int specialite, int competence, int jour, int hdebut, int hfin) {
        this.specialite = specialite;
        this.competence = competence;
        this.jour = jour;
        this.hdebut = hdebut;
        this.hfin = hfin;
    }

    @Override
    public String toString() {
        return "    specialite = " + specialite +
                ", competence = " + competence +
                ", jour = " + jour +
                ", hdebut = " + hdebut +
                ", hfin = " + hfin +
                "\n";
    }

    public void affichageTableau() {
        String string = " j " + jour + " d " + hdebut;
        if (competence == 0) string += " signe ";
        else string += " codage ";
        string += " f " + hfin;
        System.out.println(string);
    }

    public int getSpecialite() {
        return specialite;
    }

    public int getCompetence() {
        return competence;
    }

    public int getJour() {
        return jour;
    }

    // comparaison sur le debut de la formation
    public boolean precede(Formation f) {
        if (this.jour < f.jour) return true;
        if (this.jour == f.jour)
            return this.hdebut < f.hdebut;
        // tout les autres cas sont faux
        return false;
    }

    // verification que deux missions ne se chevauchent pas
    // le debut d'une formation ne peut pas être en même temps ou avant la fin de la suivante
    public boolean compatible(Formation f) {
        if (this.jour != f.jour) return true;
        if (this.hdebut < f.hdebut)
            return this.hfin < f.hdebut;
        else return f.hfin < this.hdebut;
    }

    public int getHdebut() {
        return hdebut;
    }

    public int getHfin() {
        return hfin;
    }
}
