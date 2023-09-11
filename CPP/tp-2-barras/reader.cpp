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
 * @file reader.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Main entry for reading the picture and make stats
 *
 * @date 25.03.22
 * @version 0.1.0
 *************************/

#include <iostream>
#include <fstream>

struct stats {
    int black;
    int white;
    int width;
    int height;
    std::string filename;
};

stats statistics(std::string filename) {
    stats s;
    s.filename = filename;
    s.black = 0;
    s.white = 0;
    s.width = 0;
    s.height = 0;

    std::ifstream in;
    in.open(filename);

    if (!in.is_open()) {
        std::cout << "Could not open file " << filename << std::endl;
        return s;
    }

    std::string line;

    while (std::getline(in, line)) {
        s.height++;
        for (uint32_t i = 0; i < line.length(); i++) {
            if (line[i] == '#') {
                s.black++;
            } else {
                s.white++;
            }
            s.width = i + 1;
        }
    }

    return s;
}

std::string print_stats(stats s) {
    int total = s.black + s.white;
    double percent_black = (double) s.black / total * 100;
    double percent_white = (double) s.white / total * 100;
    std::string result = "# Statistics for " + s.filename + "\n\n" \
                         "- Pixels: " + std::to_string(total) + "\n" \
                         "- Black:  " + std::to_string(s.black) + " (" + std::to_string(percent_black) + "%)\n" \
                         "- White:  " + std::to_string(s.white) + " (" + std::to_string(percent_white) + "%)\n" \
                         "- Width:  " + std::to_string(s.width) + "\n" \
                         "- Height: " + std::to_string(s.height) + "\n\n" \
                         "> Statics edited by _Simon Barras_";
    return result;
}

int main(int argc, char **argv) {
    std::cout << "Start simon's reader" << std::endl;

    if (argc != 3) {
        std::cout << "Specify the input and output file" << std::endl;
        return 1;
    }

    stats s = statistics(argv[1]);
    auto stats_str = print_stats(s);
    std::cout << stats_str << std::endl;

    std::ofstream out;
    out.open(argv[2]);
    out << stats_str << std::endl;
    out.close();

    return 0;
}
