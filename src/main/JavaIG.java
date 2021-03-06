package main;

import java.util.*;

/**
 *
 * @author Olivier Grunder, Mathieu LAGNY
 */
public class JavaIG {

    public int NBR_APPRENANTS = 20*4;
    public int NBR_FORMATIONS_PAR_SEMAINE = 1;

    public int DIMENSION_ZONE_GEOGRAPHIQUE = 200;

    public int NBR_INTERFACES = (int) (NBR_APPRENANTS/4 * 1.2);
    public int NBR_FORMATIONS = NBR_APPRENANTS * NBR_FORMATIONS_PAR_SEMAINE;

    public static int NBR_COMPETENCES = 2;
    public static ArrayList<String> NOMS_COMPETENCES = new ArrayList<>(Arrays.asList(
            "SIGNES",
            "CODAGE"));

    public static int NBR_CENTRES_FORMATION = 5;
    public static int NBR_SPECIALITES = NBR_CENTRES_FORMATION;
    public static ArrayList<String> NOMS_SPECIALITES = new ArrayList<>(Arrays.asList(
            "MENUISERIE",
            "ELECTRICITE",
            "MECANIQUE",
            "INFORMATIQUE",
            "CUISINE"));

    public static ArrayList<String> JOURS_SEMAINE = new ArrayList<>(Arrays.asList(
            "LUNDI",
            "MARDI",
            "MERCREDI",
            "JEUDI",
            "VENDREDI",
            "SAMEDI"));

    private ArrayList<Interface> interfaces;

    // indices de début des interfaces ayant une double competence
    private int i_iDouble;
    // indices de début des interfaces ayant la competence signes
    private int i_iCodage;

    // coordonnees du sessad et des centres
    private final float[][] coord;

    // matrice de compatibilite des formations
    private final boolean[][] compatibilite;

    private final ArrayList<Formation> formations;
    // indices de début des formations necessitant la competence codage
    private int i_fCodage;

    private Random rand;

    public JavaIG() {
        rand = new Random();
        interfaces = new ArrayList<>();
        formations = new ArrayList<>();
        coord = new float[NBR_CENTRES_FORMATION+1][2];
        compatibilite = new boolean[NBR_FORMATIONS][NBR_FORMATIONS];

        createInterfaces();
        coordCentresEtSESSAD();
        createFormations();
        trierInterfaces();
        trierFormations();
        createCompatibilite();
    }

    public JavaIG(int NBR_APPRENANTS) {
        this.NBR_APPRENANTS = NBR_APPRENANTS;
        rand = new Random();
        interfaces = new ArrayList<>();
        formations = new ArrayList<>();
        coord = new float[NBR_CENTRES_FORMATION+1][2];
        compatibilite = new boolean[NBR_FORMATIONS][NBR_FORMATIONS];

        createInterfaces();
        coordCentresEtSESSAD();
        createFormations();
        trierInterfaces();
        trierFormations();
        createCompatibilite();

    }

    // competences des interfaces en SIGNES et CODAGE
    private void createInterfaces() {
        for (int i = 0; i < NBR_INTERFACES; i++) {
            double f = rand.nextDouble() ;
        // Coord de l'interface
            int x = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);
            int y = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);

            int[] specialites = new int[NBR_SPECIALITES];
            for (int j = 0; j < NBR_SPECIALITES; j++) {
                if (rand.nextDouble() < 0.2) {
                    specialites[j] = 1;
                } else {
                    specialites[j] = 0;
                }
            }

            if (f < 0.1) {
                interfaces.add(new Interface(1, 1, x, y, specialites));
            } else if (f < 0.55) {
                interfaces.add(new Interface(1, 0, x, y, specialites));
                // compétence en langages des SIGNES mais pas en CODAGE LPC
            } else {
                interfaces.add(new Interface(0, 1, x, y, specialites));
                // pas de compétence en langages des SIGNES mais compétence en CODAGE LPC
            }
        }
    }

    // coordonnées du sessad, et des centres
    private void coordCentresEtSESSAD() {
        // Coord du sessad
        int x_sessad = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);
        int y_sessad = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);
        coord[0] = new float[]{x_sessad, y_sessad};

        // Coord des centres de formation
        for (int i = 1; i < NBR_CENTRES_FORMATION+1; i++) {
            int x = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);
            int y = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);
            coord[i] = new float[]{x, y};
        }
    }

    // formation : specialite, competence, horaire debut formation, horaire fin formation
    private void createFormations() {
        for (int i = 0; i < NBR_FORMATIONS; i++) {
            int specialite = rand.nextInt(NBR_SPECIALITES);
            int competence = rand.nextInt(NBR_COMPETENCES);
            int jour = rand.nextInt(6);
            int matin = rand.nextInt(2);
            int hdebut, hfin;
            if (matin == 1) {
                hdebut = 8 + rand.nextInt(3);
                hfin = hdebut + rand.nextInt(11 - hdebut) + 2;
            } else {
                hdebut = 13 + rand.nextInt(4);
                hfin = hdebut + rand.nextInt(18 - hdebut) + 2;
            }
            formations.add(new Formation(specialite, competence, jour, hdebut, hfin));
        }
    }

    // les interfaces sont triées, codage, double compétence et ensuite signes
    private void trierInterfaces() {
        ArrayList<Interface> codage = new ArrayList<>();
        ArrayList<Interface> signes = new ArrayList<>();
        ArrayList<Interface> both = new ArrayList<>();
        for (Interface inter: interfaces) {
            if (inter.codage()) {
                if (inter.signes()) both.add(inter);
                else codage.add(inter);
            } else signes.add(inter);
        }
        int i = 0;
        for (Interface inter: signes) {
            interfaces.set(i, inter);
            i ++;
        }
        this.i_iDouble = i;
        for (Interface inter: both) {
            interfaces.set(i, inter);
            i ++;
        }
        this.i_iCodage = i;
        for (Interface inter: codage) {
            interfaces.set(i, inter);
            i ++;
        }
    }

    // les formations sont triées, signes, et ensuite codage
    private void trierFormations() {
        ArrayList<Formation> codage = new ArrayList<>();
        ArrayList<Formation> signes = new ArrayList<>();
        for (Formation form: formations) {
            if (form.getCompetence() == 0) {
                signes.add(form);
            } else codage.add(form);
        }
        int i = 0;
        for (Formation form: signes) {
            formations.set(i, form);
            i ++;
        }
        this.i_fCodage = i;
        for (Formation form: codage) {
            formations.set(i, form);
            i ++;
        }
    }

    private void createCompatibilite() {
        int i = 0;
        int j;
        for (Formation f1 : formations) {
            j = 0;
            for (Formation f2 : formations) {
                compatibilite[i][j] = f1.compatible(f2);
                j++;
            }
            i++;
        }
    }

    @Override
    public String toString() {
        return  "  interfaces   = \n" + interfaces +
                "\n  coord        = " + Arrays.deepToString(coord) +
                "\n  formations   = \n" + formations;
    }

    public void affichage() {
        System.out.println("Interfaces : " + NBR_INTERFACES + " = " + interfaces.size());
        for (Interface inter: interfaces)
            System.out.println(inter);
        System.out.println("\nFormations : " + NBR_FORMATIONS + " = " + formations.size());
        for (Formation f: formations)
            f.affichageTableau();
    }

    public int getNbFormations() {
        return NBR_FORMATIONS;
    }

    public int getNbInterfaces() {
        return NBR_INTERFACES;
    }

    // on part du principe que les valeurs des tableaux ne doivent pas être modifiés en utilisant un getter
    protected ArrayList<Interface> getInterfaces() {
        return new ArrayList<>(interfaces);
    }

    protected ArrayList<Formation> getFormations() {
        return new ArrayList<>(formations);
    }

    public boolean[][] getCompatibilite() {
        return compatibilite;
    }

    protected ArrayList<ArrayList<Float>> getCoord() {
        ArrayList<ArrayList<Float>> copy = new ArrayList<>();
        for (float[] duo: coord)
            copy.add(new ArrayList<>(Arrays.asList(duo[0], duo[1])));
        return copy;
    }

    public int getI_iDouble() {
        return i_iDouble;
    }

    public int getI_iCodage() {
        return i_iCodage;
    }

    public int getI_fCodage() {
        return i_fCodage;
    }
}
