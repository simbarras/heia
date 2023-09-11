import pickle

import pandas as pd
from sklearn.model_selection import train_test_split


def train():
    df = pd.read_csv('./dataset/training_cleaned.csv')

    X, y = df.drop(columns=['solar_production']), df['solar_production']

    # Get 80% as training data and 20% as test data randomly
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    load_model(X_test, y_test)

    pass

    # Parameters
    # C: Penalty parameter C of the error term.
    # kernel: Specifies the kernel type to be used in the algorithm. It must be one of 'linear', 'poly', 'rbf', 'sigmoid', 'precomputed' or a callable.
    # degree: Degree of the polynomial kernel function (‘poly’). Ignored by all other kernels.
    # gamma: Kernel coefficient for ‘rbf’, ‘poly’ and ‘sigmoid’.

    param_grid = [
        {'C': [1, 10, 100, 1000], 'kernel': ['linear']},
        {'C': [1, 10, 100, 1000], 'kernel': ['rbf'], 'gamma': [0.001, 0.0001]},
        {'C': [1, 10, 100, 1000], 'kernel': ['poly'], 'degree': [2, 3, 4, 5], 'gamma': [0.001, 0.0001]},
        {'C': [1, 10, 100, 1000], 'kernel': ['sigmoid'], 'gamma': [0.001, 0.0001]}
    ]

    # GridSearchCV: Exhaustive search over specified parameter values for an estimator.
    # https://scikit-learn.org/stable/modules/generated/sklearn.model_selection.GridSearchCV.html
    from sklearn.model_selection import GridSearchCV
    from sklearn.svm import SVR

    # Create a classifier
    clf = GridSearchCV(SVR(), param_grid, cv=5, scoring='r2', verbose=2, n_jobs=2)

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
    pickle.dump(clf, open('./models/svm.pkl', 'wb'))


def load_model(X_test, y_test):
    # Load the model
    clf = pickle.load(open('./models/svm.pkl', 'rb'))

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

    # Print the median absolute error
    from sklearn.metrics import median_absolute_error
    print("Median absolute error: %.2f" % median_absolute_error(y_test, y_pred))

    # Print the max error
    from sklearn.metrics import max_error
    print("Max error: %.2f" % max_error(y_test, y_pred))

    # Plot predictions vs actual
    import matplotlib.pyplot as plt
    plt.scatter(y_test, y_pred)
    plt.xlabel('Actual')
    plt.ylabel('Predicted')
    plt.show()

    # Plot y_test on X-axis and absolute error on Y-axis
    import matplotlib.pyplot as plt

    plt.scatter(y_test, abs(y_test - y_pred))
    plt.xlabel('Actual')

    plt.ylabel('Absolute error')
    plt.show()

    # Use final dataset and predict values
    final = pd.read_csv('./dataset/test_students.csv')

    final = final.drop(columns=['Date', 'index'])

    final_pred = clf.predict(final)

    final['solar_production'] = final_pred

    final.to_csv('./dataset/test_students_predicted.csv', index=False)



train()
