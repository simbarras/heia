cmake_minimum_required(VERSION 3.12)
project(CPPAlgo)

set(CMAKE_CXX_STANDARD 20)

set(CMAKE_BUILD_TYPE Debug)

file(GLOB_RECURSE TP6_SOURCES src/*.h src/*.cpp src/*.cc)

file(GLOB_RECURSE TEST_SOURCES_TP6 tests/*.h tests/*.cpp tests/*.cc)

add_executable(ClusterServer server.cpp)
add_library(TP6_lib ${TP6_SOURCES})
target_link_libraries(ClusterServer TP6_lib)
add_executable(unittests_tp6 ${TEST_SOURCES_TP6})
target_link_libraries(unittests_tp6 TP6_lib)

find_package( Threads REQUIRED )
target_link_libraries(ClusterServer Threads::Threads)

if (MSVC)
    # warning level 4 and all warnings as errors
    add_compile_options(/W4 /WX)
else()
    # lots of warnings and all warnings as errors
    add_compile_options(-Wall -Werror)
endif()

