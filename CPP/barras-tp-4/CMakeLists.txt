cmake_minimum_required(VERSION 3.12)
project(TP4)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_BUILD_TYPE release)

file(GLOB_RECURSE TP4_SOURCES src/*.cpp src/*.cc)
file(GLOB_RECURSE TEST_SOURCES_TP4 tests/*.cpp tests/*.cc)

add_library(TP4_lib ${TP4_SOURCES})
add_executable(benchmark benchmark.cpp)
target_link_libraries(benchmark TP4_lib)

add_executable(unittests_tp4 ${TEST_SOURCES_TP4})
target_link_libraries(unittests_tp4 TP4_lib)

#add_executable(benchmarkTest benchmark_tests.cpp)
#target_link_libraries(benchmarkTest TP4_lib)
#target_link_libraries(benchmarkTest profiler)

add_compile_options(-Wall -Werror)

FIND_PACKAGE(OpenMP REQUIRED)
if(OPENMP_FOUND)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OpenMP_C_FLAGS}")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ${OpenMP_EXE_LINKER_FLAGS}")
else()
    message(WARNING "OpenMP could not be enabled")
endif()

