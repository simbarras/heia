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
 * @file Canvas.hpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Contains a list of shape ans draw them on the screen
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#ifndef TP_2_BARRAS_CANVAS_HPP
#define TP_2_BARRAS_CANVAS_HPP


#include <vector>
#include <string>
#include <memory>
#include "Point.hpp"
#include "shape/Shape.hpp"

constexpr int CANVAS_WIDTH = 1000;
constexpr int CANVAS_HEIGHT = 1000;

class Canvas {
public:
    Canvas(int width = CANVAS_WIDTH, int height = CANVAS_HEIGHT);

    Canvas(const Canvas &other);

    Canvas &operator=(Canvas other);

    void clear();

    void inverse(bool black);

    void add(std::shared_ptr<Shape> shape);

    void print(std::string filename);


private:
    void print_console(std::vector<std::vector<bool>> canvas);

    void print_file(std::vector<std::vector<bool>> canvas, std::string filename);

    std::vector<std::vector<bool>> draw();

private:
    std::vector<std::shared_ptr<Shape>> picture;
    int width;
    int height;
    int is_inverse;
};


#endif //TP_2_BARRAS_CANVAS_HPP
