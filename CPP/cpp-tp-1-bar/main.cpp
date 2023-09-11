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
 * @file main.cpp
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief Entry point of the program. Simple hello world.
 *
 * @date 2022.03.01 to 2020.03.03
 * @version 0.0.1
 ***************************************************************************/

#include <iostream>
#include "Calculator.hpp"

int start_calculating(Calculator *calculator, int level = 0) {
    bool first = true;
    double firstNumberValue = 0;

    // Start calculator
    while (true) {
        try {
            // Print current level
            std::cout << "Current parentheses level: " << level << std::endl;

            //Read first element from the console
            if (first) {
                std::string firstNumber;
                std::cout << "Enter first element:";
                std::cin >> firstNumber;
                if (firstNumber == "(") {
                    firstNumberValue = start_calculating(calculator, level + 1);
                } else {
                    firstNumberValue = calculator->parseDouble(firstNumber);
                }
                first = false;
            }

            //Read the operator
            std::string operatorString;
            std::cout << "Enter operator:";
            std::cin >> operatorString;
            if (operatorString == ")") return firstNumberValue;
            Op operator_ = calculator->checkOperator(operatorString);

            //Read the second element
            std::string secondNumber;
            std::cout << "Enter second element:";
            std::cin >> secondNumber;
            double secondNumberValue;
            if (secondNumber == "(") {
                secondNumberValue = start_calculating(calculator, level + 1);
            } else {
                secondNumberValue = calculator->parseDouble(secondNumber);
            }
            //Calculate the result
            firstNumberValue = calculator->calculate(firstNumberValue, secondNumberValue, operator_);
            printf("Result: %f\n", firstNumberValue);

        } catch (std::invalid_argument &e) {
            std::cout << e.what() << std::endl;
            continue;
        }
    }
}

int main() {
    std::cout << "Start calculator" << std::endl;

    // Initialize values
    Calculator calculator;

    // Start calculator
    try {
        start_calculating(&calculator);
    } catch (std::domain_error &e) {
        std::cout << e.what() << std::endl;
    }
    return 0;
}