package main;

import java.util.ArrayList;
import java.util.Random;

public class Ae {
    private int nbgenerations;       // nombre de générations après quoi la recherche est arrétée
    private int taille_pop;          // nombre d'individus dans la population
    private double taux_croisement;  // taux de croisement : valeur entre 0 et 1
    private double taux_mutation;    // taux de mutation : valeur entre 0 et 1
    private int nb_genes;   // nombre de gènes dans le chromosome
    private int max_value;           // valeur max d'un gène
    private Population pop;         // liste des individus de la population

    private JavaIG instance;         // instance du problème, référence partagée par la population et ses individus

    private Random rand = new Random();

// initialisation des paramètres de l'AG et génération de la population initiale
    public Ae(int nbg, int tp, double tcroisement, double tmutation, JavaIG instance)
    {
        nbgenerations     = nbg;
        taille_pop        = tp;
        taux_croisement   = tcroisement;
        taux_mutation     = tmutation;
        nb_genes = instance.getNbFormations();
        max_value         = instance.getNbInterfaces();
        pop = new Population(taille_pop, instance);
    }

// procédure principale de la recherche
    public Chromosome optimiser() {
        int amelioration = 0;
        Chromosome fils1 = null;
        Chromosome fils2 = null;
        Chromosome pere1;
        Chromosome pere2;
        double best_fitness;

        // évaluation des individus de la population initiale
        pop.evaluer();

        // on ordonne les individus selon leur fitness
        pop.ordonner();

        best_fitness = pop.getBestFitness();

        //tant que le nombre de générations limite n'est pas atteint
        for(int g=0; g<nbgenerations; g++) {
            boolean invalide = true;
            while (invalide) {
                //sélection de deux individus de la population courante
                pere1 = pop.selection_roulette();
                pere2 = pop.selection_roulette();

                // ont copie les peres dans les fils
                fils1 = new Chromosome(pere1);
                fils2 = new Chromosome(pere2);

                // On effectue un croisementavec une probabilité "taux_croisement"
                if (rand.nextInt(1000) / 1000.0 < taux_croisement)
                    croisement1X(fils1, fils2);

                // On effectue la mutation d'un enfant avec une probabilité "taux_mutation"
                if (rand.nextInt(1000) / 1000.0 < taux_mutation)
                    fils1.echange_2_genes_consecutifs();

                // On effectue la mutation de l'autre enfant avec une probabilité "taux_mutation"
                if (rand.nextInt(1000) / 1000.0 < taux_mutation)
                    fils2.echange_2_genes_consecutifs();

                invalide = fils1.valide() && fils1.valide();
            }

            // évaluation des deux nouveaux individus générés
            fils1.evaluer();
            fils2.evaluer();

            // Insertion des nouveaux individus dans la population
            pop.remplacement_roulette(fils1);
            pop.remplacement_roulette(fils2);

            // On réordonne la population selon la fitness
            pop.ordonner();

            // Si l'un des nouveaux individus-solutions est le meilleur jamais rencontré
            if (pop.getBestFitness() < best_fitness) {
                best_fitness = pop.getBestFitness();
                System.out.println("Amelioration de la meilleure solution a la generation " + g + " : " + best_fitness + "\n");
                amelioration = g;
            }
        }
        //retourner le meilleur individu rencontré pendant la recherche
        return pop.getBestChromosome();
    }

    public void affichage() {
        //  on affiche les statistiques de la population
        System.out.println("Quelques statistiques sur la population\n");
        pop.statistiques();
        //  on affiche la consanginité de la population
        pop.similitude();
    }

// opérateur de croisement à un point : croisement 1X
// l'opérateur 1X choisit de manière aléatoire un point de croisement
// les arguments sont les 2 chromosomes créés en copiant les parents
// l'opérateur 1X complète l'enfant 1 avec les gènes manquant en les plaçant dans l'ordre du parent 2
// et l'enfant 2 avec les gènes manquant en les plaçant dans l'ordre du parent 1.
//    Le 1ier fils est le produit de la partie haute du premier parent et
//    de la partie basse du deuxième parent et inversement pour le 2ème fils
    public void croisement1X(Chromosome enfant1, Chromosome enfant2) {

        int[] ordre_parent1 = new int[nb_genes];
        int[] ordre_parent2 = new int[nb_genes];

        // copie des genes des parents
        int[] c_p1 = enfant1.getGenes();
        int[] c_p2 = enfant2.getGenes();

        for (int i=0; i<nb_genes; i++) {
            ordre_parent1[c_p1[i]] = i;
            ordre_parent2[c_p2[i]] = i;
        }

        // l'opérateur 1X choisit de manière aléatoire le point de croisement
        int point = rand.nextInt(nb_genes);

        // l'opérateur 1X complète l'enfant 1 avec les gènes manquant en les plaçant dans l'ordre du parent 2
        // et l'enfant 2 avec les gènes manquant en les plaçant dans l'ordre du parent 1.
        for (int k = point + 1; k < nb_genes; k++) {
            for (int l = k + 1; l < nb_genes; l++) {
                if(ordre_parent2[c_p1[k]]>ordre_parent2[c_p1[l]])
                    enfant1.echange_2_genes(k,l);
                if(ordre_parent1[c_p2[k]]>ordre_parent1[c_p2[l]])
                    enfant2.echange_2_genes(k,l);
            }
        }
    }

    // opérateur de croisement à deux points : croisement 2X
// 1) l'opérateur 2X choisit de manière aléatoire 2 points de croisement
// 2) l'opérateur 2X recopie le début du parent 1 au début de l'enfant 1
//                        et le début du parent 2 au début de l'enfant 2.
// 3) l'opérateur 2X complète l'enfant 1 avec les gènes manquant en les plaçant dans l'ordre du parent 2
//                         et l'enfant 2 avec les gènes manquant en les plaçant dans l'ordre du parent 1.
    public void croisement2X(Chromosome parent1, Chromosome parent2,
                          Chromosome enfant_s1, Chromosome enfant_s2)
    {
    }

    public void croisement2LOX(Chromosome parent1, Chromosome parent2,
                            Chromosome enfant_s1, Chromosome enfant_s2)
    {
    }
}
