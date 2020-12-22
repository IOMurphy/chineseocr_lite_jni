# 简介

代码主要来自[chineseocr_lite](https://hub.fastgit.org/ouyanghuiyu/chineseocr_lite)，因为个人只在Linux下JNI调用该动态库，故打算维护linux下使用jni调用该动态库的仓库，其他可参考原仓库[chineseocr_lite](https://hub.fastgit.org/ouyanghuiyu/chineseocr_lite)。

# 目录结构

        ├── LICENSE  // LICENSE
        ├── LICENSE-origin
        ├── OcrLiteOnnx   // 动态库文件夹
        │   ├── CMakeLists.txt 
        │   ├── build.sh
        │   ├── clean.sh
        │   ├── cmake-build-debug
        │   ├── include  // 动态库头文件
        │   ├── onnx  // onnx依赖
        │   │   └── ONNXConfig.cmake
        │   ├── run-test.sh
        │   └── src  // c++源码
        ├── README.md
        ├── java-example // java示例
        │   ├── example // 调用ocr-jni的demo
        │   │   ├── example.iml
        │   │   ├── pom.xml
        │   │   ├── src
        │   │   └── target
        │   ├── ocr-jni // jni调用的java代码
        │   │   ├── pom.xml
        │   │   ├── src
        │   │   └── target
        │   └── pom.xml
        ├── libs // 编译好可直接使用的动态库文件
        └── models // 模型文件
# 只使用动态库

只使用动态库不编译cpp代码。参考java程序以及libs文件夹，将libs文件夹按照下方《将动态库添加到ld可链接的地方》即可正常使用该动态库。

# 编译动态库
## 准备工作

### 安装必要软件

```shell
sudo apt install openjdk-8-jdk cmake gcc g++ maven unzip maven -y
```

### 编译opencv

下载opencv源码

```
wget https://github.com/opencv/opencv/archive/3.4.12.zip
```

编译opencv

```shell
unzip 3.4.12.zip
cd opencv-3.4.12
mkdir build && pushd build
cmake -D CMAKE_INSTALL_PREFIX=/usr/local -D CMAKE_BUILD_TYPE=Release ..
make -j8
popd
```

### 下载onnxruntime

下载

```shell
wget https://github.com/microsoft/onnxruntime/releases/download/v1.6.0/onnxruntime-linux-x64-1.6.0.tgz
```

移动到项目中

```shell
 tar xvf onnxruntime-linux-x64-1.6.0.tgz
 # 处理头文件
 mkdir -p chineseocr_lite_jni/OcrLiteOnnx/include/onnx
 cp onnxruntime-linux-x64-1.6.0/include/* chineseocr_lite_jni/OcrLiteOnnx/include/onnx/
 # 处理动态库
 mkdir -p chineseocr_lite_jni/OcrLiteOnnx/onnx/linux/
 cp onnxruntime-linux-x64-1.6.0/lib/* chineseocr_lite_jni/OcrLiteOnnx/onnx/linux/
```



## 编译

修改OcrLiteOnnx中的CMakeFiles.txt

```shell
将set(OpenCV_DIR "/mnt/d/Code/IOMurphy/opencv-3.4.12/build")修改为opencv的build的路径。
```

编译

```shell
echo "2"|bash build.sh
```

将动态库添加到ld可链接的地方

```shell
# 方法1
# 将文件夹添加到~/.bashrc的环境变量中
vim ~/.bashrc
# 末尾添加如下代码
export LD_LIBRARY_PATH='/mnt/d/Code/IOMurphy/chineseocr_lite_jni/OcrLiteOnnx/build-lib':$LD_LIBRARY_PATH
# 应用环境变量
source ~/.bashrc
# 通过env命令可查看
env


# 方法2
# 将文件移动到系统的动态库中，不推荐
mv build-lib/libOcrLiteOnnx.so /lib64

# 方法3
vim /etc/ld.so.conf
# 行末添加chineseocr_lite_jni/OcrLiteOnnx/build-lib
# 通过ldconfig刷新环境变量
ldconfig
# 通过ldconfig -v可查询
ldconfig -v|grep libOcrLiteOnnx.so
```

编译java

```
cd java-example
mvn clean install
```

# 运行

```
cd chineseocr_lite_jni/java-exmple/example/target
java -jar example-0.0.1-SNAPSHOT.jar
```

# 问题排查

````
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x00007f603f4951a2, pid=21815, tid=0x00007f6011cf0700
#
# JRE version: OpenJDK Runtime Environment (8.0_275-b01) (build 1.8.0_275-8u275-b01-0ubuntu1~20.04-b01)
# Java VM: OpenJDK 64-Bit Server VM (25.275-b01 mixed mode linux-amd64 compressed oops)
# Problematic frame:
# V  [libjvm.so+0x7151a2]
#
# Failed to write core dump. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# An error report file with more information is saved as:
# /mnt/d/Code/IOMurphy/chineseocr_lite_jni/java-example/example/target/hs_err_pid21815.log
#
# If you would like to submit a bug report, please visit:
#   http://bugreport.java.com/bugreport/crash.jsp
#
Aborted (core dumped)
````

方法签名不对，或者包名不对。

```
Caused by: java.lang.UnsatisfiedLinkError: li.murphy.engine.OcrEngine.initThreads(I)Z
        at li.murphy.engine.OcrEngine.initThreads(Native Method) ~[ocr-jni-0.0.1-SNAPSHOT.jar!/:na]
        at li.murphy.engine.OcrEngine.getInstance(OcrEngine.java:43) ~[ocr-jni-0.0.1-SNAPSHOT.jar!/:na]
        at li.murphy.engine.OcrEngine.getInstance(OcrEngine.java:32) ~[ocr-jni-0.0.1-SNAPSHOT.jar!/:na]
        at li.murphy.MyController.<init>(MyController.java:22) ~[classes!/:0.0.1-SNAPSHOT]
        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method) ~[na:1.8.0_275]
        at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62) ~[na:1.8.0_275]
        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45) ~[na:1.8.0_275]
        at java.lang.reflect.Constructor.newInstance(Constructor.java:423) ~[na:1.8.0_275]
        at org.springframework.beans.BeanUtils.instantiateClass(BeanUtils.java:212) ~[spring-beans-5.3.2.jar!/:5.3.2]
        ... 28 common frames omitted
```

环境变量没有设置正确，参考上方《将动态库添加到ld可链接的地方》。