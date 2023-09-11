import pandas as pd
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA


def main(df):

    # Code conditions to numeric values
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

    # Keep only the last 100 lines of the frame
    df = df.tail(1000)

    # Show a 3d plot of solar production per date and per conditions
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    ax.scatter(df['cloudcover'], df['conditions'], df['solar_production'])
    plt.show()

def pca(df):
    # Use the PCA of scikit learn to reduce the number of features to 2
    # Plot the 1 feature in a 2d plot
    pca = PCA(n_components=6)
    pca.fit(df)
    pca_df = pd.DataFrame(pca.transform(df))
    # Show 6 2d graphics of the 6 features
    fig, axs = plt.subplots(2, 3)
    axs[0, 0].scatter(pca_df[0], pca_df['solar_production'])
    axs[0, 1].scatter(pca_df[1], pca_df['solar_production'])
    axs[0, 2].scatter(pca_df[2], pca_df['solar_production'])
    axs[1, 0].scatter(pca_df[3], pca_df['solar_production'])
    axs[1, 1].scatter(pca_df[4], pca_df['solar_production'])
    axs[1, 2].scatter(pca_df[5], pca_df['solar_production'])
    plt.show()

def preprocess():
    df = pd.read_csv('./dataset/training.csv')

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

    # Keep only the last 1000 lines of the frame
    df = df.tail(10000)

    return df


if __name__ == '__main__':
    #main(df)
    pca(preprocess())
