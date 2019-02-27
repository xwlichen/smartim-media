# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
#判断编译器类型,如果是gcc编译器,则在编译选项中加入c++11支持
if(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS "-std=c++11 ${CMAKE_CXX_FLAGS}")
    message(STATUS "optional:-std=c++11")
endif(CMAKE_COMPILER_IS_GNUCXX)

#需要引入我们头文件,以这个配置的目录为基准
include_directories(src/main/jniLibs/include)
include_directories(src/main/jniLibs/other)

#FFMpeg配置
#FFmpeg配置目录
set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../../src/main/jniLibs)



add_library( # Sets the name of the library.
        ffmpeg-jni

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/ffmpeg-jni.cpp)

# 编解码(最重要的库)
add_library(
        avcodec
        SHARED
        IMPORTED)
set_target_properties(
        avcodec
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavcodec-56.so)


# 设备信息
add_library(
        avdevice
        SHARED
        IMPORTED)
set_target_properties(
        avdevice
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavdevice-56.so)


# 滤镜特效处理库
add_library(
        avfilter
        SHARED
        IMPORTED)
set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavfilter-5.so)

# 封装格式处理库
add_library(
        avformat
        SHARED
        IMPORTED)
set_target_properties(
        avformat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavformat-56.so)

# 工具库(大部分库都需要这个库的支持)
add_library(
        avutil
        SHARED
        IMPORTED)
set_target_properties(
        avutil
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavutil-54.so)


# 后期处理
add_library(
        postproc
        SHARED
        IMPORTED)
set_target_properties(
        postproc
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libpostproc-53.so)


# 音频采样数据格式转换库
add_library(
        swresample
        SHARED
        IMPORTED)
set_target_properties(
        swresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswresample-1.so)

# 视频像素数据格式转换
add_library(
        swscale
        SHARED
        IMPORTED)
set_target_properties(
        swscale
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswscale-3.so)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


target_link_libraries( # Specifies the target library.
        ffmpeg-jni

        avcodec
        avdevice
        avfilter
        avformat
        avutil
        postproc
        swresample
        swscale
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})