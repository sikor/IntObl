from template import solver
from zdt_problems import Zdt1

__author__ = 'pawel'

if __name__ == '__main__':
    problem = Zdt1()
    solver(problem, population_size=100, generations_num=500, verbose=True, chart=True, weights=(-1.0, -1.0))