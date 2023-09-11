# Imports
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

# Load data

dataset = pd.read_csv('lausanne-appart.csv')
y = dataset.rent_price.values
X = dataset.living_area.values


# MSE loss function
def mse_loss(y, y_hat):
    diff = y - y_hat
    total = sum([i ** 2 for i in diff])
    return total / (2 * len(y))


def predic(X, thetas):
    return thetas[0] + thetas[1] * X


# Full batch gradient descent
def gradient(X, y):
    N = len(X)
    alpha = 0.000001
    theta_0 = X[0]
    theta_1 = 1
    costs = []
    epsilon = 0.001
    while True:
        costs.append(mse_loss(y, predic(X, [theta_0, theta_1])))
        theta_0 = theta_0 - (alpha * 1 / N) * sum([predic(X[i], [theta_0, theta_1]) - y[i] for i in range(0, N)])
        theta_1 = theta_1 - (alpha * 1 / N) * sum(
            [(predic(X[i], [theta_0, theta_1]) - y[i]) * X[i] for i in range(0, N)])
        if len(costs) > 1:
            learning_improvement = (costs[-2] - costs[-1]) / costs[-1]
            print("Learning rate: " + str(learning_improvement))
            if learning_improvement < epsilon:
                break

    costs.append(mse_loss(y, predic(X, [theta_0, theta_1])))

    return costs, theta_0, theta_1


def ex_1_a():
    costs, theta_0, theta_1 = gradient(X, y)
    plt.plot(range(len(costs)), costs)
    plt.xlabel("Iterations")
    plt.ylabel("MSE Cost")
    plt.title(f'Cost evolution (final: {costs[-1]})')
    plt.show()


# Ex 1 b : créer un epsilon et sortir de la boucle quand l'amélioration est inféfrieure à epsilon

def ex_1_c():
    costs, theta_0, theta_1 = gradient(X, y)
    plt.scatter(X, y)
    plt.plot(X, predic(X, [theta_0, theta_1]), color='red')
    plt.xlabel("Living Area (m2)")
    plt.ylabel("Rent price (CHF)")
    plt.title(f'Final thetas : {theta_0}, {theta_1}')
    plt.show()


if __name__ == '__main__':
    ex_1_a()
    ex_1_c()
