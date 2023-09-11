#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Copyright 2023, School of Engineering and Architecture of Fribourg
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


"""
Find the fusion point of an ionic liquid.
"""

__author__ = "Simon Barras"
__date__ = "2023-01-31"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"

# IMPORTS
import pandas as pd
import pandas_profiling as pp
import argparse
from sklearn import svm
from sklearn.decomposition import PCA
from sklearn.ensemble import GradientBoostingRegressor, RandomForestRegressor
from sklearn.feature_selection import SelectKBest, f_regression
from sklearn.model_selection import GridSearchCV, RandomizedSearchCV
from sklearn.preprocessing import MinMaxScaler, StandardScaler
from sklearn.metrics import mean_squared_error, max_error, mean_absolute_error, r2_score
import matplotlib.pyplot as plt

# CONSTANTS
MAE_TARGET = 5
NEW_FILE = 'resources/PS5_data_new.xlsx'
OLD_FILE = 'resources/SVM_data.xlsx'
TARGET = 'exp (K)'

# ------------------------------------#
# Read the data
# ------------------------------------#
# Old data
def read_old_dataset(sheet_name):
    """
    Read the dataset from the old file
    :param sheet_name:
    :return: features and target
    """
    df = pd.read_excel(OLD_FILE, sheet_name=f"{sheet_name} set")
    df = df.dropna()
    y = df[[TARGET]]
    X = df.drop([TARGET, 'pre (K)', 'AARD'], axis=1)
    return X, y


def clean_old_dataset(X, y):
    """
    Remove non-numeric columns
    :param X: features
    :param y: target
    :return: features and target
    """
    # Remove non numeric columns
    X = X.drop(['anion'], axis=1)
    X = X.drop(['cation'], axis=1)
    return X, y


def build_old_dataset(sheet_name):
    """
    Build the dataset from the old file
    :param sheet_name:
    :return: features and target cleaned
    """
    X, y = read_old_dataset(sheet_name)
    X, y = clean_old_dataset(X, y)
    return X, y


# New data
def read_structure():
    """
    Read the structure of the molecules
    :return: whole informations
    """
    df = pd.read_excel(NEW_FILE, sheet_name='IL structure')
    return df


def read_set(sheet_name):
    """
    Read the training or testing set
    :param sheet_name: Training or Testing
    :return: SMILES, cation, anion and target
    """
    df = pd.read_excel(NEW_FILE, sheet_name=f"{sheet_name} set")
    return df


def read_old_data():
    """
    Read the old data from the new file
    :return: SMILES, cation, anion and data
    """
    df = pd.read_excel(NEW_FILE, sheet_name="Old data")
    return df


def read_new_data():
    """
    Read the new data from the new file
    :return: SMILES, cation, anion and data
    """
    df = pd.read_excel(NEW_FILE, sheet_name="New data")
    return df


def build_dataset(sheet_name, old_data=False):
    """
    Build the dataset from the new file
    :param sheet_name: Training or Testing
    :param old_data: True if the old data should be used
    :return: features and target cleaned with data
    """
    df = read_set(sheet_name)
    if old_data:
        data = read_old_data()
    else:
        data = read_new_data()

    # Create a new dataset with the data
    dataset = pd.DataFrame()
    y = df[[TARGET]]
    for i in range(len(df)):
        anion = df.iloc[i]['anion']
        cation = df.iloc[i]['cation']
        # Get the data of the couple
        data_couple = data[(data['anion'] == anion) & (data['cation'] == cation)]
        if len(data_couple) == 0:
            # Drop the row
            y = y.drop(i)
        # Add the data to the dataset
        dataset = pd.concat([dataset, data_couple], ignore_index=True)
    return dataset.drop(['SMILES', 'cation', 'anion'], axis=1), y


# ------------------------------------#
# Preprocess the data
# ------------------------------------#
def normalize_dataset(method, X_train, X_test, y_train, y_test, both=False):
    """
    Normalize the dataset
    :param method: whick normalization method to use
    :param X_train:
    :param X_test:
    :param y_train:
    :param y_test:
    :param both: if the target should be normalized
    :return: new dataset normalized and y_scaler
    """
    # Normalize the data
    if method == 'minmax':
        X_scaler = MinMaxScaler()
    elif method == 'standard':
        X_scaler = StandardScaler()

    X_train_norm = X_scaler.fit_transform(X_train)
    X_test_norm = X_scaler.transform(X_test)
    y_scaler = None
    if both:
        if method == 'minmax':
            y_scaler = MinMaxScaler()
        elif method == 'standard':
            y_scaler = StandardScaler()
        y_train_norm = y_scaler.fit_transform(y_train)
        y_test_norm = y_scaler.transform(y_test)
    else:
        y_train_norm = y_train
        y_test_norm = y_test
    return X_train_norm, X_test_norm, y_train_norm, y_test_norm, X_scaler, y_scaler

def preprocess_data( method, X_train, X_test, y_train, y_test, k):
    """
    Preprocess the data
    :param method: pca or kbest
    :param X_train:
    :param X_test:
    :param y_train:
    :param y_test:
    :param k: number of features to keep
    :return: modified dataset
    """
    if method == 'pca':
        # Use pandas PCA
        pca = PCA(n_components=k)
        X_train = pca.fit_transform(X_train)
        X_test = pca.transform(X_test)
    elif method == 'kbest':
        # Use kbest
        kbest = SelectKBest(f_regression, k=k)
        X_train = kbest.fit_transform(X_train, y_train)
        X_test = kbest.transform(X_test)

    return X_train, X_test


# ------------------------------------#
# Train the model
# ------------------------------------#
def search(method, model, X, y):
    """
    Search the best parameters for the model
    :param method: grid or random
    :param model: to search
    :param X: features
    :param y: target
    :return: best model
    """
    print(f"Searching best parameters with {method} search")
    param_grid = {
        'n_estimators': [100, 200, 300, 400, 500],
    }
    if method == 'grid':
        # Grid search
        search_model = GridSearchCV(model, param_grid, cv=5, scoring='neg_mean_squared_error', verbose=2)
    elif method == 'random':
        # Random search
        search_model = RandomizedSearchCV(model, param_grid, cv=5, scoring='neg_mean_squared_error', verbose=2)
    search_model.fit(X, y.ravel())
    return search_model


def train_nusvr(X_train, y_train, search_method, reproduction=False):
    """
    Train the NuSVR model
    :param X_train:
    :param y_train:
    :param search_method: if the model should be searched
    :param reproduction: to use Mme Yerly parameters
    :return: trained model
    """
    model = svm.NuSVR()
    if reproduction:
        model.set_params(kernel='rbf', gamma=0.0272, C=65)
    # Transforma target to 1D array
    # y = y_train.ravel()
    if search_method:
        model = search(search_method, model, X_train, y_train)
    else:
        model.fit(X_train, y_train)
    return model


def train_svr(X_train, y_train, search_method):
    """
    Train the SVR model
    :param X_train:
    :param y_train:
    :param search_method: if the model should be searched
    :return: trained model
    """
    model = svm.SVR()
    if search_method:
        model = search(search_method, model, X_train, y_train)
    else:
        model.fit(X_train, y_train)
    return model


def train_gradient_boosting(X_train, y_train, search_method):
    """
    Train the Gradient Boosting model
    :param X_train:
    :param y_train:
    :param search_method: if the model should be searched
    :return: trained model
    """
    model = GradientBoostingRegressor()
    if search_method:
        model = search(search_method, model, X_train, y_train)
    else:
        model.fit(X_train, y_train)
    return model


def train_random_forest(X_train, y_train, search_method):
    """
    Train the Random Forest model
    :param X_train:
    :param y_train:
    :param search_method: if the model should be searched
    :return: trained model
    """
    model = RandomForestRegressor()
    if search_method:
        model = search(search_method, model, X_train, y_train)
    else:
        model.fit(X_train, y_train)
    return model


# ------------------------------------#
# Compute the score
# ------------------------------------#
def compute_score(model, X_test, y_test, X_scaler, y_scaler):
    """
    Compute the metrics and show a plot
    :param model: to evaluate
    :param X_test:
    :param y_test:
    :param X_scaler:
    :param y_scaler: if need to denormalize the data
    :return: the scores
    """
    # Predict the data
    y_test_pred = model.predict(X_test)
    # Denormalize the data
    if y_scaler:
        y_test_pred = y_scaler.inverse_transform(y_test_pred.reshape(-1, 1))
        y_test = y_scaler.inverse_transform(y_test.reshape(-1, 1))

    # Compute scores
    score = model.score(X_test, y_test)
    m_error = max_error(y_test, y_test_pred)
    mae = mean_absolute_error(y_test, y_test_pred)
    mse = mean_squared_error(y_test, y_test_pred)
    r_squared = r2_score(y_test, y_test_pred)

    # Print the scores
    print(f"Score: {score}")
    print(f"Max error: {m_error}")
    print(f"Mean absolute error: {mae}")
    print(f"Mean squared error: {mse}")
    print(f"R squared: {r_squared}")

    # Show a graph
    # Show the correct values
    plt.plot(y_test, y_test, 'r')
    # Draw a line above and below the correct values
    plt.plot(y_test, y_test + MAE_TARGET, 'g')
    plt.plot(y_test, y_test - MAE_TARGET, 'g')
    # Show the predicted values
    plt.plot(y_test, y_test + mae, 'b--')
    plt.plot(y_test, y_test - mae, 'b--')
    plt.scatter(y_test, y_test_pred, c='b', marker='o', s=10)
    # Add legend
    plt.legend(['Target', f"Target + target MAE ({MAE_TARGET})", f"Target - target MAE ({MAE_TARGET})",
                f"Target + MAE ({mae:.2f})", f"Target - MAE ({mae:.2f})", 'Predicted values'])
    # Add axis labels
    plt.xlabel('Ground truth (K)')
    plt.ylabel('Predicted values (K)')
    plt.title(f"Predicted values vs ground truth (MAE: {mae:.2f})")
    # Show the graph
    plt.show()

    return score, m_error, mae, mse, r_squared


# ------------------------------------#
# Analyse the data
# ------------------------------------#
def profile_data(df, report_name='profile.html'):
    """
    Display some information about the data
    :param df: data to analyse
    :param report_name: file name of the report
    :return: None
    """
    # Information from pandas
    print(df.head())
    print(df.describe())
    print(df.info())
    # Pandas profiling
    profile = pp.ProfileReport(df)
    profile.to_file(output_file=report_name)


# ------------------------------------#
# Main
# ------------------------------------#
def main(action, algorithme=None, search_method=None, preprocessing=None, k=0, normalize=None, both=False, old_data=False):
    """
    Main function
    """
    # Read the data
    print(f"Load the data (old_data: {old_data})")
    X_train, y_train = build_dataset('Training', old_data)
    X_test, y_test = build_dataset('Testing', old_data)
    print(
        f"Data loaded (X_train: {X_train.shape}, y_train: {y_train.shape}, X_test: {X_test.shape}, y_test: {y_test.shape})")

    if preprocessing:
        print("Preprocess the data with " + preprocessing)
        X_train, X_test = preprocess_data(preprocessing, X_train, X_test, y_train, y_test, k)

    # Perform the action
    if action == 'analyse':
        print("Analyse the data")
        analyse_data = X_train
        analyse_data['exp (K)'] = y_train
        profile_data(analyse_data)


    elif action == 'train':
        # Train the model
        print("Train the model")

        # Normalise the data
        X_train_norm = X_train
        y_train_norm = y_train
        X_test_norm = X_test
        y_test_norm = y_test
        X_scaler = None
        y_scaler = None
        if normalize:
            print("Normalize the data with " + normalize)
            X_train_norm, X_test_norm, y_train_norm, y_test_norm, X_scaler, y_scaler = normalize_dataset(normalize,
                                                                                                         X_train,
                                                                                                         X_test,
                                                                                                         y_train,
                                                                                                         y_test,
                                                                                                         both)

        # Train the model
        if algorithme == 'nusvr':
            print("Train the model with nusvr")
            model = train_nusvr(X_train_norm, y_train_norm, search_method, True)
        elif algorithme == 'svr':
            print("Train the model with svr")
            model = train_svr(X_train_norm, y_train_norm, search_method)
        elif algorithme == 'randomforest':
            print("Train the model with random forest")
            model = train_random_forest(X_train_norm, y_train_norm, search_method)
        elif algorithme == 'gradientboosting':
            print("Train the model with gradient boosting")
            model = train_gradient_boosting(X_train_norm, y_train_norm, search_method)

        # Compute the score
        print("Compute the score with the train data")
        _, _, _, _, _ = compute_score(model, X_train_norm, y_train_norm, X_scaler, y_scaler)
        print("Compute the score with the test data")
        score, m_error, mae, mse, r_squared = compute_score(model, X_test_norm, y_test_norm, X_scaler, y_scaler)


if __name__ == '__main__':
    # Parse the arguments
    parser = argparse.ArgumentParser(description='Find the fusion point of an ionic liquid.')
    parser.add_argument('--version', action='version', version=f'{__version__}')
    parser.add_argument('-o', '--old', default=False, action='store_true', help='Use the old data')
    parser.add_argument('-n', '--normalise', default=None, choices=['minmax', 'standard'], type=str,
                        help='Normalise the data')
    parser.add_argument('-b', '--both', default=False, action='store_true', help='Normalise feature and target')
    parser.add_argument('-a', '--algorithm', default=None, choices=['nusvr', 'svr', 'randomforest', 'gradientboosting'],
                        type=str, help='Algorithm to use')
    parser.add_argument('-p', '--preprocessing', default=None, choices=['pca', 'kbest'], type=str, help='Preprocessing to use')
    parser.add_argument('-k', '--kfeatures', default=10, type=int, help='Number of features to keep')
    parser.add_argument('-s', '--search', default=None, type=str, choices=['random', 'grid'], help='Search algorithm to use')
    parser.add_argument('action', choices=['analyse', 'train'], help='Action to perform')

    # Parse the arguments
    args = parser.parse_args()

    # Run the main function
    main(args.action, args.algorithm, args.search, args.preprocessing, args.kfeatures, args.normalise, args.both, args.old)
