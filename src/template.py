from nsga import select_tournament

__author__ = 'pawel'

from deap import base
from deap import creator
from deap import tools
from deap import algorithms
import matplotlib.pyplot as plt
import numpy


class Problem:
    def mate(self, p1, p2):
        """
        Recombination(krzyżowanie) in place (in situ)
        :param p1: parent1
        :param p2: parent2
        :return: void, p1 and p2 are changed in place.
        """
        pass

    def mutate(self, item):
        """
        Mutate in situ
        :param item:
        :return: void
        """
        pass

    def calculate_parameters(self, item):
        """
        calculate cost parameters for given item
        :param item:
        :return: tuple of parameters
        """

    def individual_type(self):
        """

        :return: individual type
        """
        pass

    def individual_initializer(self):
        """

        :return: method which can be called like: method(item)
        """
        return id


def solver(problem: Problem, population_size=100, generations_num=500,
           verbose=False, chart=False, weights=(-1.0,)):
    toolbox = base.Toolbox()

    creator.create("Fitness", base.Fitness, weights=weights)
    creator.create("Individual", problem.individual_type(), fitness=creator.Fitness)

    initializer = problem.individual_initializer()

    def create_individual():
        individual = creator.Individual()
        initializer(individual)
        return individual

    toolbox.register("population", tools.initRepeat, list, create_individual)
    toolbox.register("evaluate", problem.calculate_parameters)
    toolbox.register("mate", problem.mate)
    toolbox.register("mutate", problem.mutate)
    toolbox.register("select", select_tournament, tournsize=3)

    population = toolbox.population(n=population_size)
    hof = tools.HallOfFame(1)
    stats = tools.Statistics(lambda ind: ind.fitness.values)
    stats.register("avg", numpy.mean)
    stats.register("std", numpy.std)
    stats.register("min", numpy.min)
    stats.register("max", numpy.max)

    population, log = algorithms.eaMuPlusLambda(population, toolbox, mu=population_size,
                                                lambda_=population_size,
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

