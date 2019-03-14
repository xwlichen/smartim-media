/**
 * @date : 2019/2/27 上午10:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */


#include <jni.h>
#include <string>

using namespace std;




extern "C" {
JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initVideoParam(JNIEnv *env,
                                                         jobject instance,
                                                         jint width,
                                                         jint height,
                                                         jint bitRate,
                                                         jint frameRate);

JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_intAudioParam(JNIEnv *env,
                                                        jobject instance,
                                                        jint sampleRate,
                                                        jint numChannels);

JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmpParam(JNIEnv *env,
                                                        jobject instance,
                                                        jstring url_);

JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env,
                                                        jobject instance,
                                                        jbyteArray data_);
}

JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initVideoParam(JNIEnv *env,
                                                         jobject instance,
                                                         jint width,
                                                         jint height,
                                                         jint bitRate,
                                                         jint frameRate) {

}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_intAudioParam(JNIEnv *env, jobject instance,
                                                        jint sampleRate, jint numChannels) {

    // TODO

}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_initRtmpParam(JNIEnv *env, jobject instance,
                                                        jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    // TODO

    env->ReleaseStringUTFChars(url_, url);
}


JNIEXPORT void JNICALL
Java_com_smart_im_media_bridge_LiveBridge_pushVideoData(JNIEnv *env, jobject instance,
                                                        jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO

    env->ReleaseByteArrayElements(data_, data, 0);
}


