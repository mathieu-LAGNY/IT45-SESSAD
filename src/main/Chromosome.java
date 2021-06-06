package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Chromosome {
    private int taille; // la taille du chromosome = nombre de gènes = nbFormations
    private int[] genes; // les gènes du chromosome/solution
    private int max_value; // valeur des gènes entre 0 et max_value, indices des formations dans l'instance
    private double fitness; // la valeur de la fonction objectif (fitness) de la solution

    private JavaIG instance;
    private float[] distances;
    private int penalite;

    Random rand = new Random();

    // constructeur aléatoire
    public Chromosome(JavaIG instance) {
        this.taille = instance.getNbFormations();
        this.instance = instance;
        this.penalite = 0;
        this.genes = new int[taille];
        // un chromosome est composé de 'taille' gènes,
        // les gènes sont caratérisé par un entier compris entre 0 et 'max_value'
        this.max_value = instance.getNbInterfaces();

        distances = new float[max_value];
        for(int i=0; i<max_value; i++)
            this.distances[i] = 0;

        boolean recommence = true;
        while(recommence) {
            for(int i=0; i<taille; i++) {
                // on tire aléatoirement le gène suivante
                genes[i] = rand.nextInt(max_value);
            }
            recommence = this.valide();
        }
        calculerPenalite();
        evaluer();
        genererDistances();
    }

    // constructeur par recopie
    public Chromosome(Chromosome c) {
        this.genes = new int[c.taille];
        for(int i=0; i<c.taille; i++)
            this.genes[i] = c.genes[i];
        this.taille = c.taille;
        this.max_value = c.max_value;
        this.fitness = c.fitness;
        this.instance = c.instance;
        this.distances = new float[c.max_value];
        for(int i=0; i<c.max_value; i++)
            this.distances[i] = c.distances[i];
        this.penalite = c.penalite;
    }

    // criteres :
    // - l'interface a toujours la competence necessaire pour la mission
    // - pas de chevauchements
    // - nb d'heures < 35 h
    public boolean valide() {
        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<Interface> interfaces = instance.getInterfaces();

        ArrayList<Formation> formations_interface;
        ArrayList<Formation> formations_jour;

        // variables de stockage temporaires
        Formation f1;
        Formation f2;
        int htotal_interface;
        // pour chaque interface
        for (int i = 0; i < max_value; i++) {
            htotal_interface = 0;
            formations_interface = new ArrayList<>();
            // pour chaque mission
            for (int j = 0; j < taille; j++) {
                // si l'interface est liée à la mission
                if (genes[j] == i) {
                    f1 = formations.get(j);
                    // test de competence de l'interface
                    if (!interfaces.get(i).competent(f1.getCompetence()))
                        return false;
                    formations_interface.add(f1);
                }
            }
            // tri des formations de l'interface par jour et par heure de début
            quickSortFormations(formations_interface, 0, formations_interface.size()-1);
            // pour chaque jour de la semaine
            for (int j = 0; j <= 6; j++) {
                formations_jour = new ArrayList<>();
                // on selectionne les formations de la journée
                for (Formation formation : formations_interface) {
                    if (formation.getJour() == j)
                        formations_jour.add(formation);
                }
                if (!formations_jour.isEmpty()) {
                    // on a pas besoin de tester toutes les missions entres elles car elles sont triées
                    // test de compatibilite des horaires
                    for (int k = 1; k < formations_jour.size(); k ++) {
                        f1 = formations_jour.get(k-1);
                        f2 = formations_jour.get(k);
                        if (!f1.compatible(f2))
                            return false;
                    }
                    // on ajoute le total des heures de la journee entre
                    // le debut de la premiere mission et la fin de la derniere
                    // on considere qu'il s'agit du temps de travail de la journée;
                    htotal_interface += formations_jour.get(formations_jour.size()-1).getHfin() -
                            formations_jour.get(0).getHdebut();
                }
            }
            // test du temps de travail hebdomadaire
            if (htotal_interface > 35)
                return false;
        }
        return true;
    }

    // on considere une solution valide
    // pas d'interface sur des missions simultanées
    public void genererDistances() {
        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<ArrayList<Float>> coord = instance.getCoord();
        ArrayList<Interface> interfaces = instance.getInterfaces();
        // centre sessad
        ArrayList<Float> sessad = coord.get(0);

        ArrayList<Formation> formations_interface;
        ArrayList<Formation> formations_jour;

        float home_sessad;
        int spe1;
        int spe2;
        // pour chaque interface
        for (int i = 0; i < max_value; i++) {
            formations_interface = new ArrayList<>();
            // pour chaque mission
            for (int j = 0; j < taille; j++) {
                // si l'interface est liée à la mission
                if (genes[j] == i) {
                    formations_interface.add(formations.get(j));
                }
            }
            // tri des formations de l'interface par jour et par heure de début
            quickSortFormations(formations_interface, 0, formations_interface.size()-1);
            // pour chaque jour de la semaine
            for (int j = 0; j <= 6; j++) {
                formations_jour = new ArrayList<>();
                // on selectionne les formations de la journée
                for (Formation formation : formations_interface) {
                    if (formation.getJour() == j)
                        formations_jour.add(formation);
                }
                if (!formations_jour.isEmpty()) {
                    home_sessad = distanceEntreAB(interfaces.get(i).getCoordinates(),sessad);
                    distances[i] += home_sessad * 2; // aller + retour au sessad
                    // trajet entre le sessad et le centre consacré à la spéciaité de la première formation de la journée
                    distances[i] += distanceEntreAB(sessad, coord.get(formations_jour.get(0).getSpecialite()));
                    // trajets entre les différents centres des missions
                    for (int k = 1; k < formations_jour.size(); k ++) {
                        spe1 = formations_jour.get(k-1).getSpecialite();
                        spe2 = formations_jour.get(k).getSpecialite();
                        if (spe1 != spe2)
                            distances[i] += distanceEntreAB(coord.get(spe1), coord.get(spe2));
                    }
                    // trajet entre le dernier centre et le sessad
                    distances[i] += distanceEntreAB(coord.get(formations_jour.get(formations_jour.size()-1).getSpecialite()), sessad);
                }
            }
        }
    }

    public void calculerPenalite() {
        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<Interface> interfaces = instance.getInterfaces();

        // pour chaque interface
        for (int i = 0; i < max_value; i++)
            // pour chaque mission
            for (int j = 0; j < taille; j++)
                // si l'interface est liée à la mission mais n'a pas cette specialite
                if (genes[j] == i && interfaces.get(i).specialise(formations.get(j).getSpecialite()))
                    this.penalite += 1;
    }

    public float distanceEntreAB(ArrayList<Float> a, ArrayList<Float> b) {
        return (float) Math.sqrt(Math.pow(a.get(0)-b.get(0),2)+Math.pow(a.get(1)-b.get(1),2));
    }

    private void quickSortFormations(ArrayList<Formation> formations, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(formations, begin, end);

            quickSortFormations(formations, begin, partitionIndex-1);
            quickSortFormations(formations, partitionIndex+1, end);
        }
    }

    private int partition(ArrayList<Formation> arr, int begin, int end) {
        Formation pivot = arr.get(end);
        int i = (begin-1);
        for (int j = begin; j < end; j++) {
            if (arr.get(j).precede(pivot)) {
                i++;

                Formation swapTemp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, swapTemp);
            }
        }
        Formation swapTemp = arr.get(i + 1);
        arr.set(i + 1, arr.get(end));
        arr.set(end, swapTemp);

        return i+1;
    }

    public void evaluer() {
        float moy = 0;
        float somme = 0;
        for (float distance : distances)
            somme += distance;
        moy = somme / distances.length;

        double ecart = 0;
        for (float distance : distances)
            ecart += Math.pow(distance - moy, 2);
        ecart /= distances.length;
        ecart = Math.sqrt(ecart);

        float f = somme / taille;

        this.fitness = 0.5 * (moy + ecart) + 0.5 * f * penalite;
    }
    // fonction d'évaluation du chromosome (c-à-d calcul la fitness)
    //   Elle doit etre lancée à la creation des solution et apres
    //   l'exécution des operateurs de mutation et de croisement

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chromosome that = (Chromosome) o;
        return taille == that.taille && Double.compare(that.fitness, fitness) == 0 && Arrays.equals(genes, that.genes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(taille, fitness);
        result = 31 * result + Arrays.hashCode(genes);
        return result;
    }

    public double getFitness() {
        return fitness;
    }

    public int[] getGenes() {
        return genes.clone();
    }

    public void affichageSolution() {
        System.out.println("fitness = " + fitness + "\n");

        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<Interface> interfaces = instance.getInterfaces();

        ArrayList<Formation> formations_interface;
        ArrayList<Formation> formations_jour;

        // pour chaque interface
        for (int i = 0; i < max_value; i++) {
            formations_interface = new ArrayList<>();
            System.out.println(interfaces.get(i).toString());
            // pour chaque mission
            for (int j = 0; j < taille; j++) {
                // si l'interface est liée à la mission
                if (genes[j] == i) {
                    formations_interface.add(formations.get(j));
                }
            }
            // tri des formations de l'interface par jour et par heure de début
            quickSortFormations(formations_interface, 0, formations_interface.size()-1);
            // pour chaque jour de la semaine
            for (int j = 0; j <= 6; j++) {
                formations_jour = new ArrayList<>();
                // on selectionne les formations de la journée
                for (Formation formation : formations_interface) {
                    if (formation.getJour() == j)
                        formations_jour.add(formation);
                }
                for (Formation formation : formations_jour) {
                    formation.affichageTableau();
                }
            }
        };
    }

    // OPERATEURS DE MUTATION
    // on échange les 2 gènes
    public  void echange_2_genes(int gene1, int gene2) {
        int inter    = genes[gene1];
        genes[gene1] = genes[gene2];
        genes[gene2] = inter;
    }

    public void echange_2_genes_consecutifs() {
        // on sélectionne un gène aléatoirement entre le premier et l'avant dernier
        // Rappel : rand.nextInt(taille-1) retourne un entier aléatoire compris entre 0 et taille-2
        int i = rand.nextInt(taille-1);

        // on échange le gène sélectionné aléatoirement avec le gène suivant
        echange_2_genes(i, i+1);
    }

    void echange_2_genes_quelconques() {
        int i = rand.nextInt(taille-1);
        int j = rand.nextInt(taille-1);

        // on échange les deux  gènes séléctionnés aléatoirement
        echange_2_genes(i, j);
    }

    void deplacement_1_gene() {
        int i = rand.nextInt(taille-1);
        int j = rand.nextInt(taille-1);

        int imin = min(i,j);
        int imax = max(i,j);

        int inter = genes[imin];

        while(imin<imax) {
            genes[imin] = genes[imin + 1];
            imin++;
        }
        genes[imax] =  inter;
    }

    void inversion_sequence_genes() {
        int i = rand.nextInt(taille-1);
        int j = rand.nextInt(taille-1);

        int imin = min(i,j);
        int imax = max(i,j);

        while(imin<imax) {
            genes[imin] = genes[imax];
            imax--;
            imin++;
        }
    }
}
