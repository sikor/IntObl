from math import sqrt, sin
import math
import random

from deap import tools

from src.template import Problem
from zdt_solutions import zdt3_solution, zdt2_solution, zdt1_solution


class Point(object):
    def __init__(self):
        self.attributes = None


class ZdtProblem(Problem):
    def __init__(self):
        self.n = 30

    def mate(self, p1, p2):
        zipped = zip(p1.attributes, p2.attributes)
        attrs1 = [random.choice(options) for options in zipped]
        attrs2 = [random.choice(options) for options in zipped]
        #attrs1, attrs2 = tools.cxTwoPoint(p1.attributes, p2.attributes)
        #a1_0, a2_0 = attrs1[0], attrs2[0]
        #attrs1[0] = a2_0
        #attrs2[0] = a1_0
        p1.attributes = attrs1
        p2.attributes = attrs2
        return p1, p2

    def mutate(self, item):
        def swap(attributes):
            attr1 = random.randint(0, len(attributes) - 1)
            attr2 = random.randint(0, len(attributes) - 1)
            val1 = attributes[attr1]
            val2 = attributes[attr2]
            attributes[attr1] = val2
            attributes[attr2] = val1

        def identity(attributes):
            attributes[0] = self._initialise_attribute()

        def change_one(attributes):
            #attributes[random.randint(0, len(attributes) - 1)] = self._initialise_attribute()
            pass

        for i in range(10):
            random.choice([swap, identity, change_one])(item.attributes)
        #if len(list(filter(lambda x: x > 1 or x < 0, item.attributes))) > 0:
        #    print('WARN')
        return item,

    def calculate_parameters(self, item):
        return self.F(item.attributes)

    def _initialise_attribute(self):
        """
        range or random() is equal to generated variables range
        :return: Solution with random values
        """
        return random.random()

    def individual_type(self):
        return Point

    def individual_initializer(self):
        def init_point(point):
            point.attributes = [self._initialise_attribute() for i in range(self.n)]

        return init_point

    def f1(self, x1):
        return x1

    def g(self, tail):
        return 1 + (9 / (self.n - 1)) * sum(tail)

    def h(self, a, b):
        pass

    def f2(self, x):
        g_result = self.g(x[1:])
        return g_result * self.h(self.f1(x[0]), g_result)

    def F(self, x):
        return self.f1(x[0]), self.f2(x)


class Zdt1(ZdtProblem):
    def h(self, a, b):
        return 1 - sqrt(a / b)

    def get_solution(self):
        return zdt1_solution

    def get_hv(self):
        return 120.666666666666


class Zdt2(ZdtProblem):
    def h(self, a, b):
        return 1 - pow(a / b, 2)

    def get_solution(self):
        return zdt2_solution

    def get_hv(self):
        return 120.333333333333333333


class Zdt3(ZdtProblem):
    def h(self, a, b):
        return 1 - sqrt(a / b) - (a / b) * sin(10 * math.pi * a)

    def get_solution(self):
        return zdt3_solution

    def get_hv(self):
        return 128.77811613069076060



