// Copyright 2022 Haute école d'ingénierie et d'architecture de Fribourg
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/****************************************************************************
 * @file PointTest.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Test the Point class
 *
 * @date 2022-05-10
 * @version 0.1.0
 ***************************************************************************/


#include "../includes/Catch2/catch.hpp"
#include "../../barras-tp-4/src/Point.h"

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

    (((p *= 10) /= 2) += {20, 10}) -= {5, 3};

    REQUIRE(p.getX() == Approx(65));
    REQUIRE(p.getY() == Approx(67));
}

TEST_CASE("Point: Chaining step by step"){
    Point p1(10, 12, "Name");
    Point p2(10, 12, "Name");

    p1 = p1 * 10;
    p2.setX(p2.getX() * 10);
    p2.setY(p2.getY() * 10);
    REQUIRE(p1.getX() == Approx(p2.getX()));
    REQUIRE(p1.getY() == Approx(p2.getY()));

    p1 = p1 / 2;
    p2.setX(p2.getX() / 2);
    p2.setY(p2.getY() / 2);
    REQUIRE(p1.getX() == Approx(p2.getX()));
    REQUIRE(p1.getY() == Approx(p2.getY()));

    Point p3(20, 10, "Name");
    p1 = p1 + p3;
    p2.setX(p2.getX() + 20);
    p2.setY(p2.getY() + 10);
    REQUIRE(p1.getX() == Approx(p2.getX()));
    REQUIRE(p1.getY() == Approx(p2.getY()));

    p3 = {5, 3};
    p1 = p1 - p3;
    p2.setX(p2.getX() - 5);
    p2.setY(p2.getY() - 3);
    REQUIRE(p1.getX() == Approx(p2.getX()));
    REQUIRE(p1.getY() == Approx(p2.getY()));

    REQUIRE(p1.getX() == Approx(65));
    REQUIRE(p1.getY() == Approx(67));
}

TEST_CASE("Point: failed") {
    Point p(10, 12, "Name");

    CHECK_THROWS(p /= 0);
    CHECK_THROWS(p / 0);
}