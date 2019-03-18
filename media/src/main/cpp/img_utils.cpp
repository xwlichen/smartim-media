/**
 * @date : 2019/3/15 上午11:56
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//
#include "img_utils.h"


ImgUtils::ImgUtils() {}

ImgUtils::~ImgUtils() {}


void ImgUtils::nav21ToI420(char *src_n21_data, char *dst_i420_data, int width, int height) {
    //Y通道数据大小
    int src_y_size = width * height;
    //U通道数据大小
    int src_u_size = (width >> 1) * (height >> 1);

    //NV21中Y通道数据
    char *src_nv21_y_data = src_n21_data;
    //由于是连续存储的Y通道数据后即为VU数据，它们的存储方式是交叉存储的
    char *src_nv21_vu_data = src_n21_data + src_y_size;  //指针位移

    //YUV420P中Y通道数据
    char *src_i420_y_data = dst_i420_data;
    //YUV420P中U通道数据
    char *src_i420_u_data = dst_i420_data + src_y_size;
    //YUV420P中V通道数据
    char *src_i420_v_data = dst_i420_data + src_y_size + src_u_size;

    //直接调用libyuv中接口，把NV21数据转化为YUV420P标准数据，此时，它们的存储大小是不变的
    libyuv::NV21ToI420((const uint8 *) src_nv21_y_data, width,
                       (const uint8 *) src_nv21_vu_data, width,
                       (uint8 *) src_i420_y_data, width,
                       (uint8 *) src_i420_u_data, width >> 1,
                       (uint8 *) src_i420_v_data, width >> 1,
                       width, height);
}



