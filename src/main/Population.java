package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// La classe population englobe plusieurs solution potentielle du probleme
public class Population {
    private ArrayList<Chromosome> individus;
    private int taille_pop;
    private int[] ordre;

    Random rand;

    public Population(int taille_pop, int nbFormations, int nbInterfaces) {
        this.individus = new ArrayList<>(taille_pop);
        this.taille_pop = taille_pop;
        this.ordre = new int[taille_pop];
        for(int i=0; i<taille_pop; i++) {
            ordre[i] = i;
            individus.set(i, new Chromosome(nbFormations, nbInterfaces));
        }
        ordonner();
    }

    public void ordonner()
    {
        int inter;
        for(int i=0; i<taille_pop-1; i++)
            for(int j=i+1; j<taille_pop; j++)
                if(individus.get(ordre[i]).getFitness() > individus.get(ordre[j]).getFitness()) {
                    inter    = ordre[i];
                    ordre[i] = ordre[j];
                    ordre[j] = inter;
                }
    }

    // SELECTION PAR ROULETTE BIAISEE
//opérateur de sélection basé sur la fonction fitness
    public Chromosome selection_roulette()
    {
        double somme_fitness = individus.get(0).getFitness();
        double fitness_max   = individus.get(0).getFitness();
        double somme_portion;

        for(int i=1; i<taille_pop; i++) {
            somme_fitness += individus.get(i).getFitness();
            if (fitness_max < individus.get(i).getFitness())
                fitness_max = individus.get(i).getFitness();
        }
        somme_portion = fitness_max*taille_pop - somme_fitness;

        double variable_alea = rand.nextInt(1000)/1000.0;

        int ind = 0;
        double portion = (fitness_max - individus.get(0).getFitness()) /somme_portion;
        while ((ind<taille_pop-1) && (variable_alea>=portion)) {
            ind++;
            portion += (fitness_max - individus.get(ind).getFitness()) /somme_portion;
        }
        return individus.get(ind);
    }

// opérateur de remplacement basé sur la roulette biaisée d'un individu de la population
//   par un nouveau individu donné en argument
    public void remplacement_roulette(Chromosome individu)
    {
        double somme_fitness = individus.get(0).getFitness();
        for(int i=1; i<taille_pop; i++)
            somme_fitness += individus.get(i).getFitness();

        double variable_alea;
        int ind = ordre[0];
        double portion;

        while (ordre[0]==ind)
        {
            variable_alea = rand.nextInt(1000)/1000.0;
            ind = 0;
            portion = individus.get(0).getFitness() /somme_fitness;
            while ((ind<taille_pop-1) && (variable_alea>portion)) {
                ind++;
                portion += individus.get(ind).getFitness() /somme_fitness;
            }
        }
        individus.set(ind, new Chromosome(individu));
    }

// SELECTION ALEATOIRE
//opérateur de sélection aléatoire
    public Chromosome selection_aleatoire() {
        int ind_alea = rand.nextInt(taille_pop);
        return individus.get(ind_alea);
    }

// opérateur de remplacement alétoire d'un individu de la population
//   par un nouveau individu donné en argument
    public void remplacement_aleatoire(Chromosome individu) {
        int ind_alea = rand.nextInt(taille_pop);
        individus.set(ind_alea, new Chromosome(individu));
    }

// SELECTION PAR RANKING
// opérateur de sélection basé sur le ranking, les individus de la
//   population sont ordonnés de façon à ce que le meilleur est le rang 0
//   le taux de ranking permet de régler la préssion de sélection :
//   0 forte pression et +INFINI faible pression (probabilite = 1/nb qlq soit l'individu)
    public Chromosome selection_ranking(float taux_ranking) {
        ordonner();

        double variable_aleatoire = rand.nextInt(1000)/1000.0;
        int nb = taille_pop;
        int i = 0;
        taux_ranking = taux_ranking/100;

        double portion = (((nb-i)*2.0/(nb*(nb+1)))+taux_ranking)/(1.0+nb*taux_ranking);

        while(variable_aleatoire>portion) {
            i++;
            portion += (((nb-i)*2.0/(nb*(nb+1)))+taux_ranking)/(1.0+nb*taux_ranking);
        }

        if(i>=nb)
            i = rand.nextInt(nb);
        return individus.get(ordre[i]);
    }

// opérateur de remplacement basé sur le ranking d'un individu de la population
//   par un nouveau individu donné en argument
    public void remplacement_ranking(Chromosome individu, float taux_ranking)
    {
        double variable_aleatoire = rand.nextInt(1000)/1000.0;
        int T = taille_pop;
        int i = 0;
        taux_ranking   = taux_ranking/100;
        double portion = ((i*2.0/(T*(T-1)))+taux_ranking)/(1.0+T*taux_ranking);

        while(variable_aleatoire>portion) {
            i++;
            portion += ((i*2.0/(T*(T-1)))+taux_ranking)/(1.0+T*taux_ranking);
        }
        if(i>=T)
            i = rand.nextInt(T);
        int ind = ordre[i];
        individus.set(ind, new Chromosome(individu));
    }

    @Override
    public String toString() {
        String string = "Population de " + taille_pop + " individus :\n";
        for (int i=0; i<taille_pop; i++) {
            string += "individu " + i + ", rang : " + ordre[i] + "\n";
            string += individus.get(i) + "\n";
        }
        return string;
    }
}
