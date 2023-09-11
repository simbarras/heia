//
// Created by Beat Wolf on 23.07.2021.
//
#include <iostream>
#include "src/Point.h"
#include "src/RandomPoints.h"
#include "src/Clustering.h"
#include "includes/httplib/httplib.h"

static int getParam(const httplib::Request &req, const std::string &name, int default_value) {
    int value = default_value;
    if (req.has_param(name.c_str())) {
        value = std::stoi(req.get_param_value(name.c_str()));
    }

    return value;
}

int main() {
    std::cout << "Start simon's server" << std::endl;

    httplib::Server svr;

    std::vector<Point> points;

    svr.Get("/points", [&points](const httplib::Request &req, httplib::Response &res) {
        int point_count = getParam(req, "points", 500);
        int min = getParam(req, "min", 0);
        int max = getParam(req, "max", 1000);
        int seed = getParam(req, "seed", 42);

        RandomPoints rndPoints(min, max, seed);
        points = rndPoints.createRandomPoint(point_count);

        //Write header
        std::ostringstream stream;
        stream << "cluster,x,y" << std::endl;

        for (const Point &point: points) {
            stream << 0 << "," << point.getX() << "," << point.getY() << std::endl;
        }

        res.set_content(stream.str(), "text/csv");
    });

    std::cout << "/points created" << std::endl;

    svr.Get("/clusters", [&points](const httplib::Request &req, httplib::Response &res) {
        int seed = getParam(req, "seed", 42);
        int cluster_count = getParam(req, "clusters", 5);

        std::vector<Cluster> clusters;

        if (!req.has_param("algorithm") || req.get_param_value("algorithm") == "kmeans") {
            clusters = Clustering::kmeans(points, cluster_count, seed);
        } else {
            clusters = Clustering::hierarchical(points, cluster_count);
        }

        std::sort(clusters.begin(), clusters.end(), [](const Cluster &a, const Cluster &b) {
            if (a.getX() == b.getX()) {
                return a.getY() > b.getY();
            }
            return a.getX() > b.getX();
        });

        //Write header
        std::ostringstream stream;
        stream << "cluster,x,y" << std::endl;

        int clusterID = 0;
        for (const Cluster &cluster: clusters) {
            for (const Point &point: cluster.getPoints()) {
                stream << clusterID << "," << point.getX() << "," << point.getY() << std::endl;
            }
            clusterID++;
        }

        res.set_content(stream.str(), "text/csv");
    });

    std::cout << "/clusters created" << std::endl;

    svr.set_base_dir("./web");
    std::cout << "/ created" << std::endl;

    svr.Get("/author", [](const httplib::Request &req, httplib::Response &res) {
        res.set_content("Simon Barras", "text/plain");
    });

    std::cout << "/author created" << std::endl;

    std::cout << "Bind port 8080" << std::endl;

    svr.listen("0.0.0.0", 8080);

    std::cout << "Server started" << std::endl;

    return 0;
}