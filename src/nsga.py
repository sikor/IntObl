__author__ = 'pawel'


parameters = ['negativePrice', 'value']

def first_is_dominating_second(first, second):
    is_better = False
    for param in parameters:
        val1 = getattr(first, param)
        val2 = getattr(second, param)
        if val1 < val2:
            return False
        if val1 > val2:
            is_better = True
    return is_better

def sort_by_domination_and_crowd(population):
    pass
