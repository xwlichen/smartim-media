/**
 * @date : 2019/2/27 上午10:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */


#include <jni.h>
#include <string>
#include "log.h"
#include "frame_x264.h"
#include "rtmp_utils.h"
#include "img_utils.h"


extern "C" {


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initVideoConfig(JNIEnv *env,
                                                          jobject instance,
                                                          jint width,
                                                          jint height,
                                                          jint bitRate,
                                                          jint frameRate);
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env,
                                                          jobject instance,
                                                          jint sampleRate,
                                                          jint numChannels);
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env,
                                                   jobject instance,
                                                   jstring url_);
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env,
                                                        jobject instance,
                                                        jbyteArray data_);
}

//------------------------------------------------------------------------------------------------------------------------


//void nav21ToI420(char *src_n21_data, char *dst_i420_data, int width, int height) {
//    //Y通道数据大小
//    int src_y_size = width * height;
//    //U通道数据大小
//    int src_u_size = (width >> 1) * (height >> 1);
//
//    //NV21中Y通道数据
//    char *src_nv21_y_data = src_n21_data;
//    //由于是连续存储的Y通道数据后即为VU数据，它们的存储方式是交叉存储的
//    char *src_nv21_vu_data = src_n21_data + src_y_size;  //指针位移
//
//    //YUV420P中Y通道数据
//    char *src_i420_y_data = dst_i420_data;
//    //YUV420P中U通道数据
//    char *src_i420_u_data = dst_i420_data + src_y_size;
//    //YUV420P中V通道数据
//    char *src_i420_v_data = dst_i420_data + src_y_size + src_u_size;
//
//    //直接调用libyuv中接口，把NV21数据转化为YUV420P标准数据，此时，它们的存储大小是不变的
//    libyuv::NV21ToI420((const uint8 *) src_nv21_y_data, width,
//                       (const uint8 *) src_nv21_vu_data, width,
//                       (uint8 *) src_i420_y_data, width,
//                       (uint8 *) src_i420_u_data, width >> 1,
//                       (uint8 *) src_i420_v_data, width >> 1,
//                       width, height);
//}




Frame_X264 *frame_x264;
RtmpUtils *rtmpUtils;
ImgUtils *imgUtils;


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initVideoConfig(JNIEnv *env,
                                                          jobject instance,
                                                          jint width,
                                                          jint height,
                                                          jint bitRate,
                                                          jint frameRate) {

    frame_x264 = new Frame_X264();
    frame_x264->setInWidth(width);
    frame_x264->setInHeight(height);
    frame_x264->setBitrate(bitRate);
    frame_x264->setFps(frameRate);
    frame_x264->open_x264_Encode();


}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env, jobject instance,
                                                          jint sampleRate, jint numChannels) {


}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env, jobject instance,
                                                   jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    rtmpUtils = new RtmpUtils();
    rtmpUtils->init((unsigned char *) url);
    rtmpUtils->init_thread();

    imgUtils = new ImgUtils();

    // TODO

    env->ReleaseStringUTFChars(url_, url);
}


int fts = 0;
//char *dst_i420_data = (char *) malloc(
//        sizeof(char) * 640 * 480 * 3 / 2);


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env, jobject instance,
                                                        jbyteArray data_) {

    jbyte *data = env->GetByteArrayElements(data_, NULL);
    char *dst_i420_data = (char *) malloc(
            sizeof(char) * frame_x264->getInWidth() * frame_x264->getInHeight() * 3 / 2);
    imgUtils->nav21ToI420((char *)data, dst_i420_data, frame_x264->getInWidth(), frame_x264->getInHeight());



    fts++;
    frame_x264->set_x264_nal_t(NULL);
    int nal_num = frame_x264->encode_frame(dst_i420_data, fts);

    rtmpUtils->add_x264_data(frame_x264->get_x264_nal_t(), nal_num);


    free(dst_i420_data);
    dst_i420_data = NULL;
    env->ReleaseByteArrayElements(data_, data, 0);

}


