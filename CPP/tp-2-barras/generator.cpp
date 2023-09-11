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
 * @file generator.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Main entry for generating a random picture
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/


#include <iostream>
#include "Canvas.hpp"
#include "shape/rectangle/Rectangle.hpp"
#include "shape/rectangle/square/Square.hpp"
#include "shape/ellipse/Ellipse.hpp"
#include "shape/ellipse/circle/Circle.hpp"
#include <time.h>

constexpr int SHAPES = 10;
constexpr int MAX_SIZE = 29;

int main(int argc, char **argv) {
    std::cout << "Start simon's generator" << std::endl;

    if (argc != 2) {
        std::cout << "Specify the output file" << std::endl;
        return 1;
    }

    std::cout << "Create canvas" << std::endl;
    int width = 119;
    int height = 119;
    srand(time(NULL));
    Canvas c(width, height);

    for (int i = 0; i < SHAPES; i++) {
        switch (rand() % 4) {
            case 0: {
                std::cout << "Create rectangle" << std::endl;
                auto r = std::make_shared<Rectangle>(rand() % width, rand() % height, rand() % MAX_SIZE + 1,
                                                     rand() % MAX_SIZE + 1);
                c.add(r);
                break;
            }
            case 1: {
                std::cout << "Create square" << std::endl;
                auto s = std::make_shared<Square>(rand() % width, rand() % height, rand() % MAX_SIZE + 1);
                c.add(s);
                break;
            }
            case 2: {
                std::cout << "Create ellipse" << std::endl;
                auto e = std::make_shared<Ellipse>(rand() % width, rand() % height, rand() % MAX_SIZE + 1,
                                                   rand() % MAX_SIZE + 1);
                c.add(e);
                break;
            }
            case 3: {
                std::cout << "Create circle" << std::endl;
                auto ci = std::make_shared<Circle>(rand() % width, rand() % height, rand() % MAX_SIZE + 1);
                c.add(ci);
                break;
            }
        }
    }

    std::cout << "Print to console" << std::endl;
    c.print(argv[1]);

    /*std::cout << "Print in black" << std::endl;
    c.inverse(true);
    c.print("black.txt");*/

    std::cout << "End simon's generator" << std::endl;
    return 0;
}
