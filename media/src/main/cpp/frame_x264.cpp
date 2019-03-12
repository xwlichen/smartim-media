/**
 * @date : 2019/3/12 下午6:00
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//

#include "frame_x264.h"
using  namespace frame_x264 ;

//供测试文件使用,测试的时候打开 （宏定义，这个定义了，下面的判断通过，就会执行相关方法）
#define ENCODE_OUT_FILE_1
//供测试文件使用
#define ENCODE_OUT_FILE_2

Frame_X264() : in_width(0), in_height(0), out_width(
        0), out_height(0), fps(0), encoder(NULL), num_nals(0) {

#ifdef ENCODE_OUT_FILE_1
    const char *outfile1 = "/sdcard/2222.h264";
    out1 = fopen(outfile1, "wb");
#endif

#ifdef ENCODE_OUT_FILE_2
    const char *outfile2 = "/sdcard/3333.h264";
    out2 = fopen(outfile2, "wb");
#endif
}


~Frame_X264() {
}

