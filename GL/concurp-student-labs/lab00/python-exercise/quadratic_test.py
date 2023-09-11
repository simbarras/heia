from quadratic_solver import Solver as solver


def test_noRoot():
    a, b, c = 1, 2, 3
    assert solver.quadratic(a, b, c) == "This equation has no roots"
    a, b, c = -1, 2, -10
    assert solver.quadratic(a, b, c) == "This equation has no roots"
    a, b, c = -1, -2, -10
    assert solver.quadratic(a, b, c) == "This equation has no roots"
    a, b, c = -100000, -200, -10
    assert solver.quadratic(a, b, c) == "This equation has no roots"
    a, b, c = 100000, 200, 10
    assert solver.quadratic(a, b, c) == "This equation has no roots"
    