from deap.tools import selection

__author__ = 'pawel'

from functools import cmp_to_key


def first_is_dominating_second(first, second):
    is_better = False
    for val1, val2 in zip(first.fitness.values, second.fitness.values):
        if val1 > val2:
            return False
        if val1 < val2:
            is_better = True
    return is_better


def get_dist(i1, i2, param):
    return abs(getattr(i1, param) - getattr(i2, param))


def calc_crowd(pop):
    for item in pop:
        item.crowd = 0

    for param in range(len(pop[0].fitness.values)):
        sorted_by_param = sorted(pop, key=lambda i: i.fitness.values[param])
        for i1, i2, i3 in zip(sorted_by_param[::3], sorted_by_param[1::3], sorted_by_param[2::3]):
            i2.crowd += get_dist(i1, i3, param)
        sorted_by_param[0].crowd += get_dist(sorted_by_param[1], sorted_by_param[2], param)
        sorted_by_param[-1].crowd += get_dist(sorted_by_param[-2], sorted_by_param[-3], param)


def nsga2cmp(first, second):
    if first_is_dominating_second(first, second):
        return 1

    if first_is_dominating_second(second, first):
        return -1

    return second.crowd - first.crowd  # less crowd is better


def sort_by_domination_and_crowd(population):
    calc_crowd(population)
    return sorted(population, key=cmp_to_key(nsga2cmp), reverse=True)


def select_tournament(individuals, k, tournsize):
    calc_crowd(individuals)
    chosen = []
    for i in range(k):
        aspirants = selection.selRandom(individuals, tournsize)
        chosen.append(max(aspirants, key=cmp_to_key(nsga2cmp)))
    return chosen
