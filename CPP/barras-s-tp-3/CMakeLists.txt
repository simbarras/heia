cmake_minimum_required(VERSION 3.16)
project(tp_3_barras)

set(CMAKE_CXX_STANDARD 17)

add_executable(tp_3_barras
        src/Point.cpp
        src/ClosestPair.cpp
        src/ClosestPair.h
        src/Point.h
        src/SimpleClosestPair.cpp
        src/SimpleClosestPair.h
        starbucks.cpp)

add_executable(unit_tests
        src/Point.cpp
        src/ClosestPair.cpp
        src/ClosestPair.h
        src/Point.h
        src/SimpleClosestPair.cpp
        src/SimpleClosestPair.h
        tests/AllTests.cpp
        tests/ClosestPairTest.cpp
        tests/PointTest.cpp)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Werror")
