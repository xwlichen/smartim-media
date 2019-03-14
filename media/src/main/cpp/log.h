/**
 * @date : 2019/3/14 上午10:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//
#ifndef SMARTIM_MEDIA_LOG_H
#define SMARTIM_MEDIA_LOG_H


#include <android/log.h>

extern int JNI_DEBUG;  //声明，不是定义，在全局变量，如果变量没有extern修饰且没有显式的初始化，同样成为变量的定义，因此此时必须加extern

#define LOGE(debug, format, ...) if(debug){__android_log_print(ANDROID_LOG_ERROR, "smart_jni", format, ##__VA_ARGS__);}
#define LOGI(debug, format, ...) if(debug){__android_log_print(ANDROID_LOG_INFO, "smart_jni", format, ##__VA_ARGS__);}


#endif //SMARTIM_MEDIA_LOG_H
