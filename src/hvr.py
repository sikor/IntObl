from functools import reduce


def mul(elements):
    return reduce(lambda x, y: x * y, elements)


def hv(solution, w):
    return sum([volume(point, w) for point in solution])


def volume(point, w):
    return mul([abs(x1 - x2) for x1, x2 in zip(point, 2)])


def zdt_hv(solution):
    return hv(solution, [0.5] * 30)