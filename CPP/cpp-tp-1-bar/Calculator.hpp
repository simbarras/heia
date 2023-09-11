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
 * @file Calculator.hpp
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief Contains all declaration for the calculator.
 *
 * @date 2022.03.01 to 2020.03.03
 * @version 0.0.1
 ***************************************************************************/

#ifndef CPP_CALCULATOR_CALCULATOR_HPP
#define CPP_CALCULATOR_CALCULATOR_HPP

#include <string>

enum struct Op : char {
    ADD = '+',
    SUB = '-',
    MUL = '*',
    DIV = '/',
    POW = '^',
    MOD = '%',
};

class Calculator {
public:
    Calculator();

    double parseDouble(std::string input);

    Op checkOperator(std::string input);

    double calculate(double first, double second, Op operator_);

private:
    void closProgram(std::string input);
};

#endif //CPP_CALCULATOR_CALCULATOR_HPP
