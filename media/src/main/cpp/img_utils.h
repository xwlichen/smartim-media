/**
 * @date : 2019/3/15 上午11:56
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//
#ifndef SMARTIM_MEDIA_IMG_UTILS_H
#define SMARTIM_MEDIA_IMG_UTILS_H


#include <libyuv.h>
#include <jni.h>


class ImgUtils {

public:
    ImgUtils();

    ~ImgUtils();

    void nav21ToI420(jbyte *src_n21_data, jbyte *dst_i420_data, int width, int height);


};

#endif //SMARTIM_MEDIA_IMG_UTILS_H
