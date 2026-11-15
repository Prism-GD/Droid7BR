cmake_minimum_required(VERSION 3.22.1)

project(droid7br_native LANGUAGES CXX)

add_library(
    droid7br_native
    SHARED
    native_bridge.cpp
)

find_library(log-lib log)

target_compile_features(droid7br_native PRIVATE cxx_std_17)
target_link_libraries(droid7br_native PRIVATE ${log-lib})
