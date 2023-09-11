import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split


def train():
    df = pd.read_csv('./dataset/training_cleaned.csv')

    X, y = df.drop(columns=['solar_production']), df['solar_production']

    # Get 80% as training data and 20% as test data randomly
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Parameters of random forest
    # n_estimators: The number of trees in the forest.
    # max_depth: The maximum depth of the tree.
    # min_samples_split: The minimum number of samples required to split an internal node.
    # min_samples_leaf: The minimum number of samples required to be at a leaf node.
    # max_features: The number of features to consider when looking for the best split.


    param_grid = {
        'n_estimators': [100, 200, 300, 400, 500],
        'max_depth': [5, 10, 15, 20, 25],
        'min_samples_split': [2, 4, 6, 8, 10],
        'min_samples_leaf': [1, 2, 4, 6, 8],
        'max_features': ['sqrt', 'log2']
    }

    # GridSearchCV: Exhaustive search over specified parameter values for an estimator.
    # https://scikit-learn.org/stable/modules/generated/sklearn.model_selection.GridSearchCV.html
    from sklearn.model_selection import GridSearchCV
    from sklearn.ensemble import RandomForestRegressor

    # Create a classifier
    clf = GridSearchCV(RandomForestRegressor(), param_grid, cv=5, scoring='r2', verbose=2, n_jobs=4)

    # Train the classifier
    clf.fit(X_train, y_train)

    # Print the best parameters
    print(clf.best_params_)

    # Print the best score
    print(clf.best_score_)

    # Print the best estimator
    print(clf.best_estimator_)

    # Print the score on the test data
    print(clf.score(X_test, y_test))

    # Save the model
    import pickle
    pickle.dump(clf, open('./models/svm.pkl', 'wb'))

    # Load the model
    # clf = pickle.load(open('./models/svm.pkl', 'rb'))

    # Predict the test data
    y_pred = clf.predict(X_test)

    # Plot the predictions
    import matplotlib.pyplot as plt
    plt.plot(y_test, label='Actual')
    plt.plot(y_pred, label='Predicted')
    plt.legend()
    plt.show()

    # Print the mean squared error
    from sklearn.metrics import mean_squared_error
    print("Mean squared error: %.2f" % mean_squared_error(y_test, y_pred))

    # Print the mean absolute error
    from sklearn.metrics import mean_absolute_error
    print("Mean absolute error: %.2f" % mean_absolute_error(y_test, y_pred))

    # Print the r2 score
    from sklearn.metrics import r2_score
    print("R2 score: %.2f" % r2_score(y_test, y_pred))

    # Print the explained variance score
    from sklearn.metrics import explained_variance_score
    print("Explained variance score: %.2f" % explained_variance_score(y_test, y_pred))

    # Print the median absolute error
    from sklearn.metrics import median_absolute_error
    print("Median absolute error: %.2f" % median_absolute_error(y_test, y_pred))

    # Print the max error
    from sklearn.metrics import max_error
    print("Max error: %.2f" % max_error(y_test, y_pred))

    # Print the mean squared logarithmic error
    from sklearn.metrics import mean_squared_log_error
    print("Mean squared logarithmic error: %.2f" % mean_squared_log_error(y_test, y_pred))

    # Print the mean poisson deviance
    from sklearn.metrics import mean_poisson_deviance
    print("Mean poisson deviance: %.2f" % mean_poisson_deviance(y_test, y_pred))

    # Print the mean gamma deviance
    from sklearn.metrics import mean_gamma_deviance
    print("Mean gamma deviance: %.2f" % mean_gamma_deviance(y_test, y_pred))

def test():
    df = pd.read_csv('./dataset/training_cleaned.csv')

    X, y = df.drop(columns=['solar_production']), df['solar_production']

    # Get 80% as training data and 20% as test data randomly
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    params = {'max_depth': 25, 'max_features': 'log2', 'min_samples_leaf': 1, 'min_samples_split': 4, 'n_estimators': 200}
    clf = RandomForestRegressor(**params)

    # Train the classifier
    clf.fit(X_train, y_train)

    # Print the score on the test data
    print(clf.score(X_test, y_test))

    y_pred = clf.predict(X_test)

    # Print the mean squared error
    from sklearn.metrics import mean_squared_error
    print("Mean squared error: %.2f" % mean_squared_error(y_test, y_pred))

    # Print the mean absolute error
    from sklearn.metrics import mean_absolute_error
    print("Mean absolute error: %.2f" % mean_absolute_error(y_test, y_pred))

    # Print the r2 score
    from sklearn.metrics import r2_score
    print("R2 score: %.2f" % r2_score(y_test, y_pred))

    # Print the explained variance score
    from sklearn.metrics import explained_variance_score
    print("Explained variance score: %.2f" % explained_variance_score(y_test, y_pred))

    # Print the median absolute error
    from sklearn.metrics import median_absolute_error
    print("Median absolute error: %.2f" % median_absolute_error(y_test, y_pred))

    # Print the max error
    from sklearn.metrics import max_error
    print("Max error: %.2f" % max_error(y_test, y_pred))

    # Print the mean squared logarithmic error
    from sklearn.metrics import mean_squared_log_error
    print("Mean squared logarithmic error: %.2f" % mean_squared_log_error(y_test, y_pred))

    # Print the mean poisson deviance
    from sklearn.metrics import mean_poisson_deviance
    print("Mean poisson deviance: %.2f" % mean_poisson_deviance(y_test, y_pred))

    # Print the mean gamma deviance
    from sklearn.metrics import mean_gamma_deviance
    print("Mean gamma deviance: %.2f" % mean_gamma_deviance(y_test, y_pred))

