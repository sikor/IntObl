from math import sqrt, sin
import math
import random

from deap import tools

from src.template import Problem


class Point(object):
    def __init__(self):
        self.attributes = None


class ZdtProblem(Problem):
    def __init__(self):
        self.n = 30

    def mate(self, p1, p2):
        attrs1, attrs2 = tools.cxTwoPoint(p1.attributes, p2.attributes)
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
            pass

        def change_one(attributes):
            attributes[random.randint(0, len(attributes) - 1)] = self._initialise_attribute()

        random.choice([swap, identity, change_one])(item.attributes)

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
        return self.g(x[1:]) * self.h(self.f1(x[0]), self.g(x[1:]))

    def F(self, x):
        return self.f1(x[0]), self.f2(x)


class Zdt1(ZdtProblem):
    def h(self, a, b):
        return 1 - sqrt(a / b)


class Zdt2(ZdtProblem):
    def h(self, a, b):
        return 1 - pow(a / b, 2)


class Zdt3(ZdtProblem):
    def h(self, a, b):
        return 1 - sqrt(a / b) - (a / b) * sin(10 * math.pi * a)

