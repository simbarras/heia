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

/**************************
 * @file Ellipse.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Class to draw an ellipse
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#include "Ellipse.hpp"
#include <stdexcept>

Ellipse::Ellipse(int x, int y, int width, int height) : Shape() {
    if (x < 0 || y < 0 || width <= 0 || height <= 0) {
        throw std::invalid_argument("Ellipse: negative values are not allowed");
    }
    Ellipse::draw(x, y, width, height);
}

Ellipse::Ellipse(const Ellipse &other) {
    points = other.points;
}

Ellipse &Ellipse::operator=(const Ellipse &other) {
    points = other.points;
    return *this;
}

void Ellipse::draw(int x, int y, int width, int height) {
    double r1 = (width - 1) / 2.0;
    double r2 = (height - 1) / 2.0;
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            // calculate the distance from the center of the ellipse
            double delta_i = (i - r1);
            double delta_j = (j - r2);
            double delta_square_i = delta_i * delta_i;
            double delta_square_j = delta_j * delta_j;
            double r1_square = r1 * r1;
            double r2_square = r2 * r2;
            double elt_i = delta_square_i / r1_square;
            double elt_j = delta_square_j / r2_square;
            double elt = elt_i + elt_j;
            bool isInEllipse = elt <= 1.1;
            if (isInEllipse) {
                points.emplace_back(x + i, y + j, true);
            }
        }
    }
}

Ellipse::~Ellipse() {}