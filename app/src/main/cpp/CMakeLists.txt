cmake_minimum_required(VERSION 3.10)
project(singbox_native)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include/${ANDROID_ABI})

add_library(native-lib SHARED singboxJNI.cpp)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(native-lib
        ${android-lib}
        ${log-lib}
        atomic
        m
)

