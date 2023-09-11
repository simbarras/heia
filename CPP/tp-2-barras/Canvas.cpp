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
 * @file Canvas.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Contains a list of shape ans draw them on the screen
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#include "Canvas.hpp"
#include <iostream>
#include <fstream>

Canvas::Canvas(int width, int height) : picture(), width(width), height(height), is_inverse(false) {}

Canvas::Canvas(const Canvas &other)
        : picture(), width(other.width), height(other.height), is_inverse(other.is_inverse) {}

Canvas &Canvas::operator=(Canvas other) {
    picture = other.picture;
    width = other.width;
    height = other.height;
    is_inverse = other.is_inverse;
    return *this;
}

void Canvas::clear() {
    picture.clear();
}

void Canvas::inverse(bool black) {
    is_inverse = black;
}

void Canvas::add(std::shared_ptr<Shape> shape) {
    picture.push_back(shape);
}

std::vector<std::vector<bool>> Canvas::draw() {
    std::vector<std::vector<bool>> result(height, std::vector<bool>(width, false));
    for (auto s: picture) {
        for (Point &p: s->get_points()) {
            if (p.get_x() < 0 || p.get_x() >= width || p.get_y() < 0 || p.get_y() >= height) continue;
            result[p.get_y()][p.get_x()] = p.is_set();
        }
    }
    return result;
}

void Canvas::print_console(std::vector<std::vector<bool>> canvas) {
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            std::cout << (canvas[i][j] ^ is_inverse ? "#" : " ");
        }
        std::cout << std::endl;
    }
}

void Canvas::print_file(std::vector<std::vector<bool>> canvas, std::string filename) {
    std::ofstream file;
    file.open(filename);
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            file << (canvas[i][j] ^ is_inverse ? "#" : " ");
        }
        file << std::endl;
    }
    file.close();
    std::cout << "File " << filename << " written." << std::endl;
}

void Canvas::print(std::string filename) {
    auto c = draw();
    print_console(c);
    print_file(c, "../result/" + filename);
}
