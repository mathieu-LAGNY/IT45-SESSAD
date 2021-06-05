package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Olivier Grunder, Mathieu LAGNY
 */
public class JavaIG {

    public static int NBR_APPRENANTS = 20*4;
    public static int NBR_FORMATIONS_PAR_SEMAINE = 1;

    public static int DIMENSION_ZONE_GEOGRAPHIQUE = 200;

    public static int NBR_INTERFACES = (int) (NBR_APPRENANTS/4 * 1.2);
    public static int NBR_FORMATIONS = NBR_APPRENANTS * NBR_FORMATIONS_PAR_SEMAINE;

    public static String FILENAME = "instance-" + NBR_FORMATIONS + "formations.c";

    public static int NBR_COMPETENCES = 2;
    public static ArrayList<String> NOMS_COMPETENCES = new ArrayList<String>(Arrays.asList(
            "SIGNES",
            "CODAGE"));

    public static int NBR_CENTRES_FORMATION = 5;
    public static int NBR_SPECIALITES = NBR_CENTRES_FORMATION;
    public static ArrayList<String> NOMS_SPECIALITES = new ArrayList<String>(Arrays.asList(
            "MENUISERIE",
            "ELECTRICITE",
            "MECANIQUE",
            "INFORMATIQUE",
            "CUISINE"));

    public static ArrayList<String> JOURS_SEMAINE = new ArrayList<String>(Arrays.asList(
            "LUNDI",
            "MARDI",
            "MERCREDI",
            "JEUDI",
            "VENDREDI",
            "SAMEDI"));

    private ArrayList<Interface> interfaces;

    private float[][] coord;
    private float[] coord_sessad;

    private ArrayList<Formation> formations;

    private Random rand;

    public JavaIG() {
        rand = new Random();
        interfaces = new ArrayList<>();
        formations = new ArrayList<>();
        coord = new float[NBR_CENTRES_FORMATION][2];

        createInterfaces();
        writeSpecialiteInterfaces();
        writeCoord();
        createFormations();
    }

    // competences des interfaces en SIGNES et CODAGE
    private void createInterfaces() {
        for (int i = 0; i < NBR_INTERFACES; i++) {
            double f = rand.nextDouble() ;
            if (f < 0.1) {
                interfaces.add(new Interface(1, 1));
            } else if (f < 0.55) {
                interfaces.add(new Interface(1, 0));
                // compétence en langages des SIGNES mais pas en CODAGE LPC
            } else {
                interfaces.add(new Interface(0, 1));
                // pas de compétence en langages des SIGNES mais compétence en CODAGE LPC
            }
        }
    }

    private void writeSpecialiteInterfaces() {
        for (int i = 0; i < NBR_INTERFACES; i++) {
            int[] specialites = new int[NBR_SPECIALITES];
            for (int j = 0; j < NBR_SPECIALITES; j++) {
                if (rand.nextDouble() < 0.2) {
                    specialites[j] = 1;
                } else {
                    specialites[j] = 0;
                }
            }
            interfaces.get(i).setSpecialites(specialites);
        }
    }

    // coordonnées du sessad, et des centres
    private void writeCoord() {
        // Coord du sessad

        int x_sessad = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);
        int y_sessad = (int) (rand.nextDouble() * DIMENSION_ZONE_GEOGRAPHIQUE);
        coord_sessad = new float[]{x_sessad, y_sessad};

        // Coord des centres de formation
        for (int i = 0; i < NBR_CENTRES_FORMATION; i++) {
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

    @Override
    public String toString() {
        return  "  interfaces   = \n" + interfaces +
                "\n  coord        = " + Arrays.deepToString(coord) +
                "\n  coord_sessad = " + Arrays.toString(coord_sessad) +
                "\n  formations   = \n" + formations;
    }

    public int getNbFormations() {
        return NBR_FORMATIONS;
    }

    public int getNbInterfaces() {
        return NBR_INTERFACES;
    }
}
