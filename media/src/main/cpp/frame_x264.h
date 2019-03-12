/**
 * @date : 2019/3/12 下午5:42
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description : 
 */
//
#ifndef SMARTIM_MEDIA_FRAME_X264_H
#define SMARTIM_MEDIA_FRAME_X264_H


#include <libavcodec/avcodec.h>
#include <x264.h>


namespace frame_x264{
    void setParams();
}



class Frame_X264 {

private:
    int in_width;
    int in_height;
    int out_width;
    int out_height;
    /* e.g. 25, 60, etc.. */
    int fps;
    int bitrate;
    int i_threads;
    int i_vbv_buffer_size;
    int i_slice_max_size;
    int b_frame_frq;

    /* x264 struct*/
    x264_picture_t pic_in;
    x264_picture_t pic_out;
    x264_param_t params;
    x264_nal_t* nals;
    x264_t* encoder;
    int num_nals;

    FILE *out1;
    FILE *out2;

public:
    Frame_X264();
    ~Frame_X264();  //作用：对象消亡时，自动被调用，用来释放对象占用的空间
    /* open for encoding */
    bool open();
    /* encode the given data */
    int encodeFrame(char* inBytes, int frameSize, int pts, char* outBytes, int *outFrameSize);
    /* close the encoder and file, frees all memory */
    bool close();
    /* validates if all params are set correctly, like width,height, etc.. */
    bool validateSettings();
    /* sets the x264 params */
    void setParams();
    int getFps() const;  //保证值不会更改
    void setFps(int fps);
    int getInHeight() const;
    void setInHeight(int inHeight);
    int getInWidth() const;
    void setInWidth(int inWidth);
    int getNumNals() const;
    void setNumNals(int numNals);
    int getOutHeight() const;
    void setOutHeight(int outHeight);
    int getOutWidth() const;
    void setOutWidth(int outWidth);
    int getBitrate() const;
    void setBitrate(int bitrate);
    int getSliceMaxSize() const;
    void setSliceMaxSize(int sliceMaxSize);
    int getVbvBufferSize() const;
    void setVbvBufferSize(int vbvBufferSize);
    int getIThreads() const;
    void setIThreads(int threads);
    int getBFrameFrq() const;
    void setBFrameFrq(int frameFrq);
};



#endif //SMARTIM_MEDIA_FRAME_X264_H
