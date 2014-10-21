__author__ = 'pawel'

from functools import cmp_to_key


parameters = ['negativePrice', 'value']


class Problem:

    def __init__(self, parameters):
        """

        :param parameters: name of cost parameters available as attributes in items
        :return:
        """
        self.parameters = parameters


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
        calculate cost parameters which are avaiable in self.parameters for gieven item
        :param item:
        :return: void
        """

    def generate_initial_population(self, n):
        """

        :param n: size of population
        :return: list of population items
        """
        pass

class NsgaSorting:
    def __init__(self, parameters):
        self.parameters = parameters

    def first_is_dominating_second(self, first, second):
        is_better = False
        for param in self.parameters:
            val1 = getattr(first, param)
            val2 = getattr(second, param)
            if val1 > val2:
                return False
            if val1 < val2:
                is_better = True
        return is_better

    @staticmethod
    def get_dist(i1, i2, param):
        return abs(getattr(i1, param) - getattr(i2, param))

    def calc_crowd(self, pop):
        for item in pop:
            item.crowd = 0

        for param in self.parameters:
            sorted_by_param = sorted(pop, key=lambda i: getattr(i, param))
            for i1, i2, i3 in zip(sorted_by_param[::3], sorted_by_param[1::3], sorted_by_param[2::3]):
                i2.crowd += self.get_dist(i1, i3, param)
            sorted_by_param[0].crowd += self.get_dist(sorted_by_param[1], sorted_by_param[2], param)
            sorted_by_param[-1].crowd += self.get_dist(sorted_by_param[-2], sorted_by_param[-3], param)

    def nsga2cmp(self, first, second):
        if self.first_is_dominating_second(first, second):
            return 1

        if self.first_is_dominating_second(second, first):
            return -1

        return second.crowd - first.crowd  # less crowd is better

    def sort_by_domination_and_crowd(self, population):
        self.calc_crowd(population)
        return sorted(population, key=cmp_to_key(self.nsga2cmp), reverse=True)
