from deap.tools import selection
import itertools

__author__ = 'pawel'

from functools import cmp_to_key


def first_is_dominating_second(first, second):
    return first.fitness.dominates(second.fitness)
    is_better = False
    for val1, val2 in zip(first.fitness.values, second.fitness.values):
        if val1 > val2:
            return False
        if val1 < val2:
            is_better = True
    return is_better


def get_dist(i1, i2, param):
    return abs(i1.fitness.values[param] - i2.fitness.values[param])


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

def crowd_cmp(first, second):
    return second.crowd - first.crowd

def sort_by_domination_and_crowd(population):
    calc_crowd(population)
    return sorted(population, key=cmp_to_key(nsga2cmp), reverse=True)


def find_non_dominated(population):
    return [x for x in population if len([y for y in population if first_is_dominating_second(y, x)]) == 0]


def sort_by_domination(population:list, k):
    sorted_population = []
    layers_num = 0
    sorted_len = 0
    while len(population) != 0:
        layers_num += 1
        non_dominated = find_non_dominated(population)
        sorted_population.append(non_dominated)
        sorted_len += len(non_dominated)
        for x in non_dominated:
            population.remove(x)
        if sorted_len >= k:
            break
    print(layers_num)
    return sorted_population


def select_tournament(individuals, k, tournsize):
    calc_crowd(individuals)
    sorted_ind = sort_by_domination(individuals, k)

    barage = sorted_ind[-1]
    del sorted_ind[-1]
    chosen = []
    flatenned_sorted = list(itertools.chain(*sorted_ind))
    for i in range(k - len(flatenned_sorted)):
        aspirants = selection.selRandom(barage, tournsize)
        chosen.append(max(aspirants, key=cmp_to_key(crowd_cmp)))

    flatenned_sorted.extend(chosen)
    return flatenned_sorted
