import matplotlib.pyplot as plt
import numpy as np
import math

from sklearn import datasets
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.naive_bayes import GaussianNB, BernoulliNB, MultinomialNB

def cut(X, y, c, step):
    return X[np.where(y[...]==c)][::step], y[y==c][::step]

dataset = datasets.load_iris()
X_0, y_0 = cut(dataset.data, dataset.target, 0, 1)

print(X_0.shape, y_0.shape)
# try first what the previous line is doing, what is going to happen when changing the value of step
#Using the function cut() and numpy.concatenate()
#unbalance the dataset (keep 100% of class 0, 50% of class 1, 33% of class 2)

X_1, y_1 = cut(dataset.data, dataset.target, 1, 2)
X_2, y_2 = cut(dataset.data, dataset.target, 2, 3)

X = np.concatenate((X_0, X_1, X_2), axis=0)
y = np.concatenate((y_0, y_1, y_2), axis=0)


X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

def separateByClass(X, y):
    return {c: cut(X, y, c, 1)[0] for c in np.unique(y)}

class_data = separateByClass(X_train, y_train)
print(class_data)


# For each classes, calculate the mean and the standard deviation for each features.
# Structure of the result should be a dictionary with the following structure:
# {
#     class: [(mean 1st feature, std 1st feature), (mean 2nd feature, std 2nd feature), ...],
#     class: [...],
#     ...
# }

def summarize(X):
    return [(np.mean(X[:, i]), np.std(X[:, i])) for i in range(X.shape[1])]

def summarizeByClass(X, y):
    return {c: summarize(X[np.where(y==c)]) for c in np.unique(y)}

summary = summarizeByClass(X_train, y_train)
print(summary)

# calculate the probability that a given sample belong to each class, and select the class with the highest probability
# Use the formula of the Univariate Gaussian distribution


def computeProbability(x, mean, std):
    return (1 / (np.sqrt(2 * np.pi) * std)) * np.exp(-((x - mean) ** 2 / (2 * std ** 2)))

# Verify your probability function
x = 10
mean = 10
stdev = 1
probability = computeProbability(x, mean, stddev)
print(probability)


# compute priors
def computePriors(y):
    return {c: len(y[y==c]) / len(y) for c in np.unique(y)}


piors = computePriors(y_train)


# compute posteriors
def computePosteriors(sample, summaries):
    posteriors = {}
    for c in summaries:
        posteriors[c] = 1
        for i in range(len(summaries[c])):
            mean, std = summaries[c][i]
            x = sample[i]
            posteriors[c] *= computeProbability(x, mean, std)
    return posteriors


# Use posteriors to take decisions about best classes
def predictSample(sample, summaries):
    posteriors = computePosteriors(sample, summaries)
    best_class, best_prob = None, -1
    for c in posteriors:
        if best_class is None or posteriors[c] > best_prob:
            best_prob = posteriors[c]
            best_class = c
    return best_class

# Compute the accuracy of your classifier applied to your test set.
def predict(X, summaries):
    return np.array([predictSample(sample, summaries) for sample in X])

y_pred = predict(X_test, summary)
print(accuracy_score(y_test, y_pred))

# Compute the accuracy of your classifier applied to your test set using cross validation.
from sklearn.model_selection import cross_val_score

scores = cross_val_score(GaussianNB(), X, y, cv=5)
print(scores)


# Use the scikit-learn Gaussian Naive Bayes implementation and compare the results

gnb = GaussianNB()
y_pred = gnb.fit(X_train, y_train).predict(X_test)
print(accuracy_score(y_test, y_pred))

# For each class, and for each feature, plot the histogram of the data.

def plot_histograms(X, y):
    for c in np.unique(y):
        for i in range(X.shape[1]):
            plt.hist(X[np.where(y==c)][:, i], label=c)
        plt.legend()
        plt.show()

plot_histograms(X, y)

# find the feature with the most gaussian distribution

def find_best_feature(X, y):
    best_feature = None
    best_score = -1
    for i in range(X.shape[1]):
        score = np.mean(np.abs(np.diff(np.sort(X[:, i]))))
        if score > best_score:
            best_score = score
            best_feature = i
    return best_feature


print(find_best_feature(X, y))

# Calculate accuracy when using a Gaussian Naive Bayes classifier with feature 2 only

X_2 = X[:, 2].reshape(-1, 1)
X_train, X_test, y_train, y_test = train_test_split(X_2, y, test_size=0.2, random_state=42, stratify=y)

gnb = GaussianNB()
y_pred = gnb.fit(X_train, y_train).predict(X_test)
print(accuracy_score(y_test, y_pred))

# Calculate accuracy when using a Gaussian Naive Bayes classifier with feature 3 only

X_3 = X[:, 3].reshape(-1, 1)
X_train, X_test, y_train, y_test = train_test_split(X_3, y, test_size=0.2, random_state=42, stratify=y)

gnb = GaussianNB()
y_pred = gnb.fit(X_train, y_train).predict(X_test)
print(accuracy_score(y_test, y_pred))










print(cross_val_score(GaussianNB(), X_best, y, cv=5))






# Use the best feature to plot the decision boundary of your classifier

print(find_best_feature(X, y))
# For each class, and for each feature, plot the histogram of the data, but this time, use the same scale for all histograms.

def plot_histograms(X, y):
    for c in np.unique(y):
        for i in range(X.shape[1]):
            plt.hist(X[np.where(y==c)][:, i], label=c)
        plt.legend()
        plt.show()

def plot_histograms_same_scale(X, y):
    for c in np.unique(y):
        for i in range(X.shape[1]):
            plt.hist(X[np.where(y==c)][:, i], label=c)
        plt.legend()
        plt.show()

# Conclusion and observations

# What is the best feature to use for classification?



