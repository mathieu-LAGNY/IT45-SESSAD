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
}
