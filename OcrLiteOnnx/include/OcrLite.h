#ifndef __OCR_LITE_H__
#define __OCR_LITE_H__

#include "opencv2/core.hpp"
#include "onnx/onnxruntime_cxx_api.h"
#include "OcrStruct.h"
#include "DbNet.h"
#include "AngleNet.h"
#include "CrnnNet.h"

class OcrLite {
public:
    OcrLite(int numOfThread);

    ~OcrLite();

    void initLogger(bool isConsole, bool isPartImg, bool isResultImg);

    void enableResultTxt(const char *path, const char *imgName);

    void loadModels(const char *path);

    void Logger(const char *format, ...);

    OcrResult detect(const char *path, const char *imgName,
                     int padding, int imgResize,
                     float boxScoreThresh, float boxThresh, float minArea,
                     float unClipRatio, bool doAngle, bool mostAngle);

    OcrResult detect(char *input, int size, int padding, int imgResize,
                     float boxScoreThresh, float boxThresh, float minArea,
                     float unClipRatio, bool doAngle, bool mostAngle);

private:
    // 是否输出到控制台
    bool isOutputConsole = false;
    // 是否输出部分图片
    bool isOutputPartImg = false;
    // 是否输出文本
    bool isOutputResultTxt = false;
    // 是否输出图片
    bool isOutputResultImg = false;
    FILE *resultTxt;
    int numThread = 0;
    Ort::Env env = Ort::Env(ORT_LOGGING_LEVEL_ERROR, "OcrLite");
    Ort::SessionOptions sessionOptions;
    DbNet dbNet;
    AngleNet angleNet;
    CrnnNet crnnNet;

    std::vector <cv::Mat> getPartImages(cv::Mat &src, std::vector <TextBox> &textBoxes,
                                        const char *path, const char *imgName);

    OcrResult detect(const char *path, const char *imgName,
                     cv::Mat &src, cv::Rect &originRect, ScaleParam &scale,
                     float boxScoreThresh = 0.6f, float boxThresh = 0.3f, float minArea = 3.f,
                     float unClipRatio = 2.0f, bool doAngle = true, bool mostAngle = true);
};

#endif //__OCR_LITE_H__
