from functools import reduce


def mul(elements):
    return reduce(lambda x, y: x * y, elements)


def hv(solution, w):
    return sum([volume(point, w) for point in solution])/len(solution)


def volume(point, w):
    return mul([abs(x1 - x2) for x1, x2 in zip(point.fitness.values, w)])


def zdt_hv(solution):
    return hv(solution, (11, 11))


def zdt_hvr(solution: list, problem):
    return zdt_hv(solution) / problem.get_hv()