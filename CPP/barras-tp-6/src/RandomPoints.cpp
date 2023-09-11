//
// Created by "Beat Wolf" on 04.06.2022.
//

#include "RandomPoints.h"
#define _USE_MATH_DEFINES
#include <cmath>

class RefPointHash{
public:
    std::size_t operator()(const Point& point) const{
        std::size_t h1 = std::hash<double>{}(point.getX());
        std::size_t h2 = std::hash<double>{}(point.getY());
        std::size_t h3 = std::hash<std::string>{}(point.getName());

        return (h1 ^ (h2 << 1)) ^ (h3 << 1);
    }
};

//Based on https://www.geeksforgeeks.org/random-number-generator-in-arbitrary-probability-distribution-fashion/
int findCeil(int arr[], int r, int l, int h)
{
    int mid;
    while (l < h)
    {
        mid = l + ((h - l) >> 1); // Same as mid = (l+h)/2
        (r > arr[mid]) ? (l = mid + 1) : (h = mid);
    }
    return (arr[l] >= r) ? l : -1;
}


//Based on https://www.geeksforgeeks.org/random-number-generator-in-arbitrary-probability-distribution-fashion/
// The main function that returns a random number
// from arr[] according to distribution array
// defined by freq[]. n is size of arrays.
int myRand(int arr[], int freq[], int n, std::mt19937 random_eng)
{
    // Create and fill prefix array
    int prefix[n], i;
    prefix[0] = freq[0];
    for (i = 1; i < n; ++i)
        prefix[i] = prefix[i - 1] + freq[i];

    // prefix[n-1] is sum of all frequencies.
    // Generate a random number with
    // value from 1 to this sum

    std::uniform_int_distribution<int> distribution(0, prefix[n - 1]);

    int r = distribution(random_eng) + 1;

    // Find index of ceiling of r in prefix array
    int index = findCeil(prefix, r, 0, n - 1);
    return arr[index];
}

RandomPoints::RandomPoints(double min, double max, int seed): min(min), max(max), seed(seed){};

std::vector<Point> RandomPoints::createRandomPoint(int count){
    size_t length = int(max - min);
    int possibleValues[length];
    std::iota(&possibleValues[0], &possibleValues[length - 1], int(min));
    int freq[int(max - min)];
    std::iota(&freq[0], &freq[length - 1], 0);
    for(int i = 0; i < length; i++){
        freq[i] = (1 + std::cos(M_PI + (freq[i]) / (length / 18.))) * 10 + 5;
    }

    std::mt19937 random_engine;

    if(seed == -1){
        std::random_device random_device;
        random_engine = std::mt19937(random_device());
    }else{
        random_engine = std::mt19937(seed);
    }
    std::uniform_real_distribution<double> distribution(-1, 1);


    std::unordered_set<Point, RefPointHash> point_set;

    while(point_set.size() < count){
        point_set.insert(Point(myRand(possibleValues, freq, length, random_engine) + distribution(random_engine),
                               myRand(possibleValues, freq, length, random_engine) + distribution(random_engine)));

    }

    return {point_set.begin(), point_set.end()};
}

