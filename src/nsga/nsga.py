__author__ = 'pawel'

import random

from deap import base
from deap import creator
from deap import tools
from deap import algorithms
import matplotlib.pyplot as plt
import numpy

creator.create("FitnessMin", base.Fitness, weights=(-1.0,))


class Individual(list):
    def __init__(self):
        super().__init__()
        self.fitness = creator.FitnessMin()


class GeneticFunctions(object):
    def eval_individual(self):
        pass

    def mate(self):
        pass

    def mutate(self):
        pass

    def individual(self):
        return Individual()


def solver(genetic_functions: GeneticFunctions, population_size=100, generations_num=500,
           verbose=False, chart=False):

    toolbox = base.Toolbox()
    toolbox.register("individual", genetic_functions.individual)
    toolbox.register("population", tools.initRepeat, list, toolbox.individual)

    toolbox.register("evaluate", genetic_functions.eval_individual)
    # toolbox.register("mate", genetic_functions.crossover)
    toolbox.register("mate", genetic_functions.mate)
    # toolbox.register("mate", tools.cxOnePoint)
    toolbox.register("mutate", genetic_functions.mutate, percentage_clients=0.05)
    # toolbox.register("mutate", tools.mutUniformInt, low=0, up=(len(problem.warehouses)-1), indpb=0.05)
    toolbox.register("select", tools.selTournament, tournsize=int(population_size * 0.15))
    # toolbox.register("select", tools.selBest)

    population = toolbox.population(n=population_size)
    hof = tools.HallOfFame(1)
    stats = tools.Statistics(lambda ind: ind.fitness.values)
    stats.register("avg", numpy.mean)
    stats.register("std", numpy.std)
    stats.register("min", numpy.min)
    stats.register("max", numpy.max)

    population, log = algorithms.eaMuCommaLambda(population, toolbox, mu=int(population_size * 0.3),
                                                 lambda_=int(population_size * 0.5),
                                                 cxpb=0.1, mutpb=0.8, ngen=generations_num, stats=stats, halloffame=hof,
                                                 verbose=verbose)

    if chart:
        generations_num = log.select("gen")
        fit_mins = log.select("min")
        size_avgs = log.select("avg")
        # print (fit_mins, " ", size_avgs)
        fig, ax1 = plt.subplots()
        line1 = ax1.plot(generations_num, fit_mins, "b-", label="Minimum Fitness")
        ax1.set_xlabel("Generation")
        ax1.set_ylabel("Fitness", color="b")
        for tl in ax1.get_yticklabels():
            tl.set_color("b")

        ax2 = ax1.twinx()
        line2 = ax2.plot(generations_num, size_avgs, "r-", label="Average")
        ax2.set_ylabel("Average", color="r")
        for tl in ax2.get_yticklabels():
            tl.set_color("r")

        lns = line1 + line2
        labs = [l.get_label() for l in lns]
        ax1.legend(lns, labs, loc="center right")

        plt.show()

    return hof[0]

