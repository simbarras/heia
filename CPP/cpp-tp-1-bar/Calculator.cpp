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
 * @file Calculator.cpp
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief Contains all method to cast argument and compute the result.
 *
 * @date 2022.03.01 to 2020.03.03
 * @version 0.0.1
 ***************************************************************************/

#include <stdexcept>
#include "Calculator.hpp"
#include <math.h>

Calculator::Calculator() {
    printf("Calculator created\n");
}

double Calculator::parseDouble(std::string input) {
    // Try to parse string to double
    // If it fails, throw an exception
    closProgram(input);
    if (input == "pi") return M_PI;
    if (input == "e") return M_E;
    try {
        return std::stod(input);
    } catch (std::invalid_argument &e) {
        throw std::invalid_argument("Invalid input: " + input + " is not a valid double");
    }
}

Op Calculator::checkOperator(std::string input) {
    // Check if input is an operator
    closProgram(input);
    if (input.size() != 1) throw std::invalid_argument("Invalid input: " + input + " must be a single character");
    char c = input[0];
    if (c != char(Op::ADD) && c != char(Op::SUB) && c != char(Op::MUL) && c != char(Op::DIV) && c != char(Op::MOD) &&
        c != char(Op::POW))
        throw std::invalid_argument("Invalid input: " + input + " is not a valid operator");
    Op operator_ = Op(input[0]);
    return operator_;
}

double Calculator::calculate(double first, double second, Op operator_) {
    // Calculate the result of the operation
    // Choose the correct operator with a switch statement
    // Return the result
    double result = first;
    switch (operator_) {
        case Op::ADD:
            return first + second;
        case Op::SUB:
            return first - second;
        case Op::MUL:
            return first * second;
        case Op::DIV:
            if (second == 0) {
                throw std::invalid_argument("Division by zero");
            }
            return first / second;
        case Op::POW:
            if (second < 0) {
                throw std::invalid_argument("Negative exponent");
            }
            if (second == 0) {
                return 1;
            }
            for (int i = 1; i < second; i++) {
                result *= first;
            }
            return result;
        case Op::MOD:
            return first - (second * (int) (first / second));
        default:
            throw std::invalid_argument("Invalid operator");
    }
}

void Calculator::closProgram(std::string input) {
    if (input == "#") {
        throw std::domain_error("# will stop the program");
    }
}

