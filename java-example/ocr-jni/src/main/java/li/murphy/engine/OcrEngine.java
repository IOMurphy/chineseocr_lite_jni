package li.murphy.engine;


import li.murphy.pojo.OcrResult;
import java.nio.ByteBuffer;

public class OcrEngine {

    private OcrEngine() {
    }

    private static final String LIB_NAME = "OcrLiteOnnx";

    static {
        try {
//            log.info("开始加载动态库{}", LIB_NAME);
            System.loadLibrary(LIB_NAME);
        } catch (UnsatisfiedLinkError e) {
//            log.error("初始化动态库失败，ocr将无法正常运行");
            e.printStackTrace();
        }
    }


    private static volatile OcrEngine instance;

    public static final String MODEL_PATH = "../../../models";
    public static final int NUM_THREADS = Runtime.getRuntime().availableProcessors() * 3 / 4 + 1;

    public static OcrEngine getInstance() {
        return getInstance(NUM_THREADS, MODEL_PATH);
    }

    public static OcrEngine getInstance(int numThreads, String modelPath) {
        if (instance == null) {
            synchronized (OcrEngine.class) {
                if (instance == null) {
                    if (numThreads <= 0) {
                        numThreads = NUM_THREADS;
                    }
                    try {
                        instance = new OcrEngine();
//                        log.info("初始化线程及模型，线程数{}，模型路径为{}", new File(modelPath).getAbsolutePath());
                        instance.initThreads(numThreads);
                        instance.loadModels(modelPath);
//                        log.info("完成模型及线程初始化");
                    } catch (UnsatisfiedLinkError e) {
//                        log.error("初始化动态库失败，ocr将无法正常运行");
                        instance = null;
                    }
                }
            }
        }
        return instance;
    }


    private static final int PADDING = 0;
    private static final float BOX_SCORE_THRESH = 0.6f;
    private static final float BOX_THRESH = 0.3f;
    private static final float MINI_AREA = 3f;
    private static final float UN_CLIP_RATIO = 2.0f;
    private static final boolean DO_ANGLE = true;
    private static final boolean MOST_ANGLE = true;
    private static final int RESIZE = 0;

    /**
     * 对外暴露的文本检测接口
     *
     * @param data 图片的byte数组
     * @return
     */
    public OcrResult detect(byte[] data) {
        return detect(data, data.length, PADDING, RESIZE, BOX_SCORE_THRESH, BOX_THRESH, MINI_AREA,
                UN_CLIP_RATIO, DO_ANGLE, MOST_ANGLE);
    }

    public OcrResult detect(byte[] data, int length, int padding, int resize, float boxScoreThresh, float boxThresh, float miniArea, float unClipRation, boolean doAngle, boolean mostAngle) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length).put(data);
        return detectByDirectByteBuffer(byteBuffer, length, padding, resize, boxScoreThresh, boxThresh, miniArea,
                unClipRation, doAngle, mostAngle);
    }

    /**
     * 通过文件名ocr
     *
     * @param input
     * @param padding
     * @param resize
     * @param boxScoreThresh
     * @param boxThresh
     * @param miniArea
     * @param unClipRation
     * @param doAngle
     * @param mostAngle
     * @return
     */
    @Deprecated
    native OcrResult detectByFilePath(String input, int padding, int resize, float boxScoreThresh, float boxThresh, float miniArea, float unClipRation, boolean doAngle, boolean mostAngle);


    /**
     * 通过对外内存ocr
     *
     * @param input
     * @param length
     * @param padding
     * @param resize
     * @param boxScoreThresh
     * @param boxThresh
     * @param miniArea
     * @param unClipRation
     * @param doAngle
     * @param mostAngle
     * @return
     */
    native OcrResult detectByDirectByteBuffer(ByteBuffer input, int length, int padding, int resize, float boxScoreThresh, float boxThresh, float miniArea, float unClipRation, boolean doAngle, boolean mostAngle);


    /**
     * 加载模型
     *
     * @param modelsDir
     * @return
     */
    native boolean loadModels(String modelsDir);

    /**
     * 查看ocr版本
     *
     * @return
     */
    native String getVersion();

    /**
     * 开启结果输出到文件中
     *
     * @param imagePath
     */
    native void enableResultText(String imagePath);

    /**
     * 初始化处理线程
     *
     * @param numThreads
     * @return
     */
    native boolean initThreads(int numThreads);

    /**
     * 初始化日志记录器
     *
     * @param isConsole
     * @param isPartImg
     * @param isResultImg
     */
    native void initLogger(boolean isConsole, boolean isPartImg, boolean isResultImg);
}