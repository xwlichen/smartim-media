/**
 * @date : 2019/2/27 上午10:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */


#include <jni.h>
#include <string>
#include "frame_x264.h"
#include "img_utils.h"
using namespace imgt;


extern "C" {
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_initVideoConfig(JNIEnv *env,
//                                                         jobject instance,
//                                                         jint width,
//                                                         jint height,
//                                                         jint bitRate,
//                                                         jint frameRate);
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env,
//                                                        jobject instance,
//                                                        jint sampleRate,
//                                                        jint numChannels);
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env,
//                                                        jobject instance,
//                                                        jstring url_);
//JNIEXPORT void JNICALL
//Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env,
//                                                        jobject instance,
//                                                        jbyteArray data_);
}

//------------------------------------------------------------------------------------------------------------------------


Frame_X264 *frame_x264;




JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initVideoConfig(JNIEnv *env,
                                                         jobject instance,
                                                         jint width,
                                                         jint height,
                                                         jint bitRate,
                                                         jint frameRate) {

    frame_x264=new Frame_X264();
    frame_x264->setInWidth(width);
    frame_x264->setInHeight(height);
    frame_x264->setBitrate(bitRate);
    frame_x264->setFps(frameRate);
    frame_x264->openX264Encode();


}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initAudioConfig(JNIEnv *env, jobject instance,
                                                        jint sampleRate, jint numChannels) {


}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmp(JNIEnv *env, jobject instance,
                                                        jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    // TODO

    env->ReleaseStringUTFChars(url_, url);
}

char *dst_i420_data;
int fts=0;
char *dst_h264_data;

JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env, jobject instance,
                                                        jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);


    nav21ToI420((char*)data,dst_i420_data,frame_x264->getInWidth(),frame_x264->getInHeight());
    fts++;
    frame_x264->encodeFrame(dst_i420_data,fts,dst_h264_data);

    env->ReleaseByteArrayElements(data_, data, 0);
}


