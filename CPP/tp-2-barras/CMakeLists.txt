cmake_minimum_required(VERSION 3.16)
project(tp_2_barras)

set(CMAKE_CXX_STANDARD 17)

add_executable(generator generator.cpp Point.cpp Point.hpp Canvas.cpp Canvas.hpp ./shape/Shape.hpp ./shape/Shape.cpp ./shape/rectangle/Rectangle.cpp ./shape/rectangle/Rectangle.hpp ./shape/rectangle/square/Square.cpp ./shape/rectangle/square/Square.hpp ./shape/ellipse/Ellipse.cpp ./shape/ellipse/Ellipse.hpp ./shape/ellipse/circle/Circle.cpp ./shape/ellipse/circle/Circle.hpp)
add_executable(reader reader.cpp )
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Werror")
