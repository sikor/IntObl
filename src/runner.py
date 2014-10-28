from src.template import solver
from src.zdt_problems import Zdt1, Zdt2, Zdt3

__author__ = 'pawel'

if __name__ == '__main__':
    problem = Zdt2()
    solver(problem, population_size=300, generations_num=300, verbose=True, chart=True, weights=(-1.0, -1.0))