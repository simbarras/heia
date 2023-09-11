cmake_minimum_required(VERSION 3.16)
project(cpp_calculator)

set(CMAKE_CXX_STANDARD 17)

add_executable(cpp_calculator main.cpp Calculator.cpp Calculator.hpp)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Werror")
