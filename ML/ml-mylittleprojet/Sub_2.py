import math

import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.model_selection import train_test_split, RandomizedSearchCV


def clean_normalize(df, finalDf=None):
    df = df.drop(columns=['index', 'snow', 'snowdepth', 'windspeed', 'winddir', 'temp'])

    # If there is a finalDf, combine it with the training data
    if finalDf is not None:
        finalDf = finalDf.drop(columns=['index', 'snow', 'snowdepth', 'windspeed', 'winddir', 'temp'])
        # Add solar_production with value -100 to finalDf
        finalDf['solar_production'] = -100
        # Combine the two dataframes
        df = pd.concat([df, finalDf], ignore_index=True)
        # Rebuild index
        df = df.reset_index(drop=True)

    # Convert Date to hour and month colums
    df['Date'] = pd.to_datetime(df['Date'])
    df['hour'] = df['Date'].dt.hour
    df['month'] = df['Date'].dt.month

    # Drop date column
    df = df.drop(columns=['Date'])

    # Fill conditions na with "UNKNOWN"
    df['conditions'] = df['conditions'].fillna('UNKNOWN')
    # Fill na with 0
    df = df.fillna(0)

    # Transform literals to numbers
    df['conditions'] = df['conditions'].map(
        {
            'UNKNOWN': 0,
            'Rain, Partially cloudy': 2,
            'Rain': 3,
            'Overcast': 4,
            'Rain, Overcast': 1,
            'Partially cloudy': 5,
            'Clear': 6,
        })


    # Drop test
    df = df.drop(columns=['humidity', 'conditions', 'visibility'])

    # Keep production
    prod = df['solar_production']
    df = df.drop(columns=['solar_production'])

    # Normalize the data with minimax
    df = (df - df.min()) / (df.max() - df.min())

    # Add production back
    df['solar_production'] = prod


    # Separate df from finalDf
    if finalDf is not None:
        finalDf = df[df['solar_production'] <= -100]
        finalDf = finalDf.drop(columns=['solar_production'])
        df = df[df['solar_production'] > -100]
        return df, finalDf
    else:
        return df, None


def train_random_forest():
    df = pd.read_csv('./dataset/training.csv')
    finalDf = pd.read_csv('./dataset/test_students.csv')

    df, finalDf = clean_normalize(df, finalDf)

    # Split the data into test and train set (80% train, 20% test)
    X, y = df.drop(columns=['solar_production']), df['solar_production']

    # Get 40% of row every where in the dataset as train set and delete them from the dataset
    number_of_row = math.floor(0.4 * len(X))
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=number_of_row, random_state=42)

    # Create a random forest classifier
    clf = RandomForestRegressor()

    params = {
        'max_depth': [5, 10, 25, 50, 100],
        'max_features': ['sqrt', 'log2'],
        'min_samples_leaf': [1, 2, 4],
        'min_samples_split': [2, 4, 8],
        'n_estimators': [100, 200, 400]
    }

    # Train the classifier
    random_search = RandomizedSearchCV(clf, param_distributions=params, n_iter=100, cv=5, verbose=2, random_state=42,
                                       n_jobs=-1, scoring='r2')
    random_search.fit(X_train, y_train)

    # Print the best parameters
    print(random_search.best_params_)

    # Print the best score
    print(random_search.best_score_)

    # Save estimator
    import pickle
    pickle.dump(random_search.best_estimator_, open('./models/rf_rnd.pkl-1', 'wb'))

    # Test on test data
    y_pred = random_search.predict(X_test)

    # R2 score
    print("R2 score: ", r2_score(y_test, y_pred))

    # MSE
    print("MSE: ", mean_squared_error(y_test, y_pred))


def use_estimator():
    # Load the model
    import pickle
    clf = pickle.load(open('./models/rf_rnd-1.pkl', 'rb'))

    df = pd.read_csv('./dataset/training.csv')
    finalDfBase = pd.read_csv('./dataset/test_students.csv')

    df, finalDf = clean_normalize(df, finalDfBase)

    y_pred = clf.predict(finalDf)

    # Save predictions
    finalDfBase['solar_production'] = y_pred
    finalDfBase.to_csv('./dataset/predictions.csv', index=False)

    final = pd.DataFrame()
    final['id'] = finalDfBase['index']
    final['predicted'] = y_pred
    final.to_csv('./dataset/cleaned_predictions.csv', index=False)


if __name__ == '__main__':
    # train_random_forest()
    use_estimator()
