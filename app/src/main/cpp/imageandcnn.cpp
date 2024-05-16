#include <jni.h>
#include <string>
#include <algorithm>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_tutorials_applications_imageandcnn_MainActivityKt_capitalizeClassificationResult(
        JNIEnv *env, jclass clazz, jstring input) {
    const char *inputChars = env->GetStringUTFChars(input, 0);
    std::string inputStr(inputChars);
    std::transform(inputStr.begin(), inputStr.end(), inputStr.begin(), ::toupper);
    env->ReleaseStringUTFChars(input, inputChars);
    return env->NewStringUTF(inputStr.c_str());
}