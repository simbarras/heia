import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.model_selection import train_test_split, RandomizedSearchCV

LIMIT_OF_ZERO = 0.00005
# index,Date,temp,humidity,precip,snow,snowdepth,windspeed,winddir,visibility,cloudcover,solarradiation,solarenergy,uvindex,conditions,solar_production
COLUMN_NAMES = {'index', 'Date', 'temp', 'humidity', 'precip', 'snow', 'snowdepth', 'windspeed', 'winddir',
                'visibility', 'cloudcover', 'solarradiation', 'solarenergy', 'uvindex', 'conditions'}
SELECTED_FEATURES = {'Date', 'cloudcover', 'solarradiation', 'solarenergy', 'uvindex', 'conditions', 'precip'}


def clean_normalize(df, finalDf):
    df['Date'] = pd.to_datetime(df['Date'])
    df = df.drop(columns=list(COLUMN_NAMES.difference(SELECTED_FEATURES)))

    # Combiner les deux dataset pour avoir les mêmes colonnes
    finalDf['Date'] = pd.to_datetime(finalDf['Date'], format='%Y-%d-%m %H:%M:%S')
    finalDf = finalDf.drop(columns=list(COLUMN_NAMES.difference(SELECTED_FEATURES)))

    # Ajouter des productions négatives pour le dataset final
    finalDf['solar_production'] = -100
    # Combine the two dataframes
    df = pd.concat([df, finalDf], ignore_index=True)
    # Rebuild index
    df = df.reset_index(drop=True)

    # Convert Date to hour and month colums
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

    # Keep production
    prod = df['solar_production']
    df = df.drop(columns=['solar_production'])

    # Normalize the data with minimax
    df = (df - df.min()) / (df.max() - df.min())

    # Add production back
    df['solar_production'] = prod

    # Separate df from finalDf

    finalDf = df[df['solar_production'] <= -100]
    finalDf = finalDf.drop(columns=['solar_production'])
    df = df[df['solar_production'] > -100]
    return df, finalDf


def train_random_forest():
    df = pd.read_csv('./dataset/training.csv')
    finalDf = pd.read_csv('./dataset/test_students.csv')

    df, finalDf = clean_normalize(df, finalDf)

    # Split the data into test and train set (80% train, 20% test)
    X, y = df.drop(columns=['solar_production']), df['solar_production']

    # Get 40% of row every where in the dataset as train set and delete them from the dataset
    X_test, X_train, y_test, y_train = train_test_split(X, y, test_size=0.8, shuffle=False)

    # Create a random forest classifier
    clf = RandomForestRegressor()

    params = {
        'max_depth': [int(x) for x in np.linspace(start=5, stop=1000, num=100)],
        'min_samples_leaf': [int(x) for x in range(1, 50)],
        'min_samples_split': [int(x) for x in range(1, 50)],
        'n_estimators': [int(x) for x in np.linspace(start=200, stop=5000, num=50)]
    }

    number_of_fits = params['max_depth'].__len__() * params['min_samples_leaf'].__len__() * params[
        'min_samples_split'].__len__() * params['n_estimators'].__len__()
    print('Number of fits: {}, with 5 folds, {}'.format(number_of_fits, str(number_of_fits * 5)))

    # Train the classifier
    random_search = RandomizedSearchCV(clf, param_distributions=params, n_iter=2000, cv=10, verbose=2, random_state=42,
                                       n_jobs=-1, scoring='r2')
    random_search.fit(X_train, y_train)

    # Print the best parameters
    print(random_search.best_params_)

    # Print the best score
    print(random_search.best_score_)

    # Save estimator
    import pickle
    pickle.dump(random_search.best_estimator_, open('./models/rf_rnd-notebook.pkl', 'wb'))

    # Test on test data
    y_pred = random_search.predict(X_test)

    # R2 score
    print("R2 score: ", r2_score(y_test, y_pred))

    # MSE
    print("MSE: ", mean_squared_error(y_test, y_pred))


def smmoth(y_pred):
    smoothed = pd.DataFrame(y_pred, columns=['solar_production'])
    smoothed['solar_production'] = smoothed['solar_production'].rolling(6).mean()
    smoothed['solar_production'] = smoothed['solar_production'].fillna(0)
    return smoothed['solar_production']

def compare_real_and_predicted(estimator):
    df = pd.read_csv('./dataset/training.csv')
    finalDf = pd.read_csv('./dataset/test_students.csv')

    df_cleaned, finalDf = clean_normalize(df, finalDf)



    unique_days = df['Date'].dt.floor('d').unique()

    # 10 randoms days
    random_days = np.random.choice(unique_days, 10, replace=False)
    unique_days = np.setdiff1d(unique_days, random_days)

    # Create a figure with 10 subplots
    fig, axs = plt.subplots(10, 1, figsize=(15, 30))
    index = 0
    for day in random_days:
        indexes = df['Date'].dt.floor('d') == day

        if df[indexes].shape[0] < 250:
            while df[indexes].shape[0] < 250:
                day = np.random.choice(unique_days, 1, replace=False)[0]
                unique_days = np.setdiff1d(unique_days, day)
                indexes = df['Date'].dt.floor('d') == day

        df_day = df_cleaned[indexes]
        X, y = df_day.drop(columns=['solar_production']), df_day['solar_production']

        y_pred = estimator.predict(X)

        r2 = r2_score(y, y_pred)
        mse = mean_squared_error(y, y_pred)

        axs[index].plot(X['hour'], y, label='Real')
        axs[index].plot(X['hour'], y_pred, label='Predicted')
        axs[index].set_title('R2: {}, MSE: {}'.format(r2, mse))
        axs[index].legend()
        index += 1

    plt.tight_layout()
    plt.show()




def use_estimator():
    # Load the model
    import pickle
    clf = pickle.load(open('./models/rf_rnd-notebook.pkl', 'rb'))

    df = pd.read_csv('./dataset/training.csv')

    finalDfBase = pd.read_csv('./dataset/test_students.csv')

    df, finalDf = clean_normalize(df, finalDfBase)

    compare_real_and_predicted(clf)

    y_pred = clf.predict(finalDf)

    y_pred[y_pred < LIMIT_OF_ZERO] = 0
    y_pred = smmoth(y_pred)

    # Save predictions
    finalDfBase['solar_production'] = y_pred
    finalDfBase.to_csv('./dataset/predictions.csv', index=False)

    final = pd.DataFrame()
    final['id'] = finalDfBase['index']
    final['predicted'] = y_pred
    final.to_csv('./dataset/cleaned_predictions.csv', index=False)


if __name__ == '__main__':
    #train_random_forest()
    use_estimator()
