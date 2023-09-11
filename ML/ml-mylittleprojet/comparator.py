import pandas as pd
from matplotlib import pyplot as plt
import matplotlib.dates as mdates


def compare_datasets():
    folder = "dataset/"
    data = "test_students.csv"
    first = "third_submit.csv"
    second = "cleaned_predictions.csv"

    first_df = pd.read_csv(folder + first)
    second_df = pd.read_csv(folder + second)
    data_df = pd.read_csv(folder + data)

    total_df = pd.DataFrame()
    total_df['id'] = first_df['id']
    total_df['Date'] = data_df['Date']
    total_df['conditions'] = data_df['conditions']
    total_df['first'] = first_df['predicted']
    total_df['second'] = second_df['predicted']


    print(total_df.head())


def plot_compare():

    original = pd.read_csv("dataset/test_students.csv")
    original['Date'] = pd.to_datetime(original['Date'], format='%Y-%d-%m %H:%M:%S')

    first = pd.read_csv("predicted-t.csv")
    second = pd.read_csv("dataset/fourth_submit.csv")
    third = pd.read_csv("dataset/fourth_submit.csv")
    current = pd.read_csv("dataset/fourth_submit.csv")

    mean = pd.DataFrame()
    mean['Date'] = original['Date']
    mean['predicted'] = (second['predicted'] + third['predicted'] + current['predicted']) / 3
    mean['Date'] = original['Date']

    # Append date to every prediction
    first['Date'] = original['Date']
    second['Date'] = original['Date']
    third['Date'] = original['Date']
    current['Date'] = original['Date']

    uniqueDayMonth = original['Date'].dt.strftime('%d-%m').unique()

    for dm in uniqueDayMonth:
        dayMonthDf = original[original['Date'].dt.strftime('%d-%m') == dm]

        if first['predicted'].max() < 0.05 \
                and second['predicted'].max() < 0.05 \
                and third['predicted'].max() < 0.05 \
                and current['predicted'].max() < 0.05:
            continue

        # Create figure 20 x 15
        fig, ax = plt.subplots(figsize=(20, 10))
        # Plot the data of 4 different predictions
        ax.plot(dayMonthDf['Date'], first[first['Date'].dt.strftime('%d-%m') == dm]['predicted'], label='first')
        ax.plot(dayMonthDf['Date'], second[second['Date'].dt.strftime('%d-%m') == dm]['predicted'], label='second')
        ax.plot(dayMonthDf['Date'], third[third['Date'].dt.strftime('%d-%m') == dm]['predicted'], label='third')
        ax.plot(dayMonthDf['Date'], current[current['Date'].dt.strftime('%d-%m') == dm]['predicted'], label='current')

        # Add mean in red bold dashed line
        ax.plot(dayMonthDf['Date'], mean[mean['Date'].dt.strftime('%d-%m') == dm]['predicted'], label='mean', color='red', linestyle='dashed', linewidth=3)

        # Label the axes
        ax.set(xlabel="Date", ylabel="Solar production", title="Solar production for day " + str(dm))
        # Add legend
        ax.legend()

        # Set X Major ticks DayLocator
        ax.xaxis.set_major_locator(mdates.HourLocator())
        # Set X Major ticks format
        ax.xaxis.set_major_formatter(mdates.DateFormatter('%H:%M'))

        # Set Y Axis limit = 3
        ax.set_ylim(0, 3)

        # Set X major ticks rotation
        plt.setp(ax.get_xticklabels(), rotation=90, ha="center")
        # Set X minor ticks rotation
        plt.setp(ax.get_xticklabels(minor=True), rotation=90, ha="center")

        # Show figure
        plt.show()

        # Save png
#         fig.savefig('./plots/' + str(dm) + '.png')

#compare_datasets()

plot_compare()