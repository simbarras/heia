//
// Created by beatw on 3/3/2022.
//


#include "../includes/Catch2/catch.hpp"
#include "../src/Point.h"

TEST_CASE("Point: Construction") {
    Point p(10, 12);

    REQUIRE(10 == Approx(p.getX()));
    REQUIRE(12 == Approx(p.getY()));
    REQUIRE("" == p.getName());

    p = Point(-123, 234, "test");
    REQUIRE(-123 == Approx(p.getX()));
    REQUIRE(234 == Approx(p.getY()));
    REQUIRE("test" == p.getName());
}

TEST_CASE("Point: Construction 2") {
    Point p(10, 12);

    Point p2 = p;
    REQUIRE(p.getX() == Approx(p2.getX()));
    REQUIRE(p.getY() == Approx(p2.getY()));
    REQUIRE(p.getName() == p2.getName());
}


TEST_CASE("Point: Construction 3") {
    Point p(10, 12, "Name");

    Point p2 = p;
    REQUIRE(p.getX() == Approx(p2.getX()));
    REQUIRE(p.getY() == Approx(p2.getY()));
    REQUIRE(p.getName() == p2.getName());
}

TEST_CASE("Point: Chaining") {
    Point p(10, 12, "Name");
    Point p1(10, 12, "Name2");
    Point pTemp1(20, 10, "temp");
    Point pTemp2(5, 3, "temp");

    (((p *= 10) /= 2) += {20, 10}) -= {5, 3};
    p1 = p1 * 10;
    p1 = p1 / 2;
    p1 = p1 + pTemp1;
    p1 = p1 - pTemp2;

    REQUIRE(p.getX() == Approx(65));
    REQUIRE(p.getY() == Approx(67));
    REQUIRE(p1.getX() == Approx(p1.getX()));
    REQUIRE(p1.getY() == Approx(p.getY()));

}

TEST_CASE("Point: failed") {
    Point p(10, 12, "Name");

    CHECK_THROWS(p /= 0);
    CHECK_THROWS(p / 0);
}

TEST_CASE("Point: setters") {
    Point p(10, 12, "Name");

    p.setX(20);
    p.setY(30);

    REQUIRE(20 == Approx(p.getX()));
    REQUIRE(30 == Approx(p.getY()));
    REQUIRE("Name" == p.getName());
}

