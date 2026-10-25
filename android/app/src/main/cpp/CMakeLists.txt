cmake_minimum_required(VERSION 3.18.1)
project("gow2launcher")

# Configuracao de compilação C++20
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Adiciona biblioteca nativa do Launcher / Hardware Profile
add_library(
    gow2_core_bridge
    SHARED
    gow2_native_bridge.cpp
    gow2_hardware_profile.cpp
)

# Localiza biblioteca de logging do Android
find_library(log-lib log)

target_link_libraries(
    gow2_core_bridge
    ${log-lib}
)
