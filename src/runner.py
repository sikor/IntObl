from template import solver

__author__ = 'pawel'

if __name__ == '__main__':
    problem = None
    solver(problem, population_size=100, generations_num=500, verbose=True, chart=True, weights=(-1.0, -1.0))