import pandas as pd
from matplotlib import pyplot as plt
from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler


# Use PCA to reduce the dimensionality of the data and find which features are the most important to determine "solar_production"
def plot_pca():
    df = pd.read_csv("dataset/training.csv")
    df['Hour'] = pd.to_datetime(df['Date']).dt.hour
    df = df.drop(columns=['index', 'Date'])
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

    # Find every NaN value
    df = df.fillna(0)
    std_slc = StandardScaler()
    df_std = std_slc.fit_transform(df)
    pca = PCA(n_components=5) # 5 features in PCA.csv
    pca.fit(df_std)
    df_pca = pca.transform(df_std)
    df_pca = pd.DataFrame(df_pca)

    # Print 5 best features from PCA
    print(pca.explained_variance_ratio_)
    print(pca.components_)
    print(pca.singular_values_)

    # Plot the 5 best features
    plt.scatter(df_pca[0], df_pca[1], c=df['solar_production'])
    plt.show()









plot_pca()
