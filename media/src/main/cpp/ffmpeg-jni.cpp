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
JNIEXPORT jstring JNICALL
Java_com_smart_im_media_FFmpegBridge_test(JNIEnv *env,
                                          jclass type);
}

JNIEXPORT jstring JNICALL
Java_com_smart_im_media_FFmpegBridge_test(JNIEnv *env,
                                          jclass type) {
    const char *s = "sdfdsfsdfdsfds";

    return env->NewStringUTF(s);

}
