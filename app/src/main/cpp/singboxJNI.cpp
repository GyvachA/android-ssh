#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>

#define LOG_TAG "SingboxJNI"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

using StartFunc = int (*)(char*, int);
using StopFunc = void (*)();

static void* singboxHandle = nullptr;
static StartFunc Start = nullptr;
static StopFunc Stop = nullptr;

__attribute__((constructor))
void load_singbox() {
    singboxHandle = dlopen("libsingbox.so", RTLD_NOW);
    if (!singboxHandle) {
        LOGE("Failed to load libsingbox.so: %s", dlerror());
        return;
    }

    Start = reinterpret_cast<StartFunc>(dlsym(singboxHandle, "Start"));
    if (!Start) {
        LOGE("Failed to load symbol Start: %s", dlerror());
    }

    Stop = reinterpret_cast<StopFunc>(dlsym(singboxHandle, "Stop"));
    if (!Stop) {
        LOGE("Failed to load symbol Stop: %s", dlerror());
    }

    LOGI("libsingbox.so loaded successfully");
}

__attribute__((destructor))
void unload_singbox() {
    if (singboxHandle) {
        dlclose(singboxHandle);
        singboxHandle = nullptr;
    }
}

extern "C" {

JNIEXPORT jint JNICALL
Java_com_gyvacha_androidssh_utils_SingboxNative_start(JNIEnv* env, jobject /*thiz*/, jstring config_json, jint tun_fd) {
    if (!Start) {
        LOGE("Start not loaded");
        return -1;
    }

    const char* config_cstr = env->GetStringUTFChars(config_json, nullptr);
    int ret = Start((char*)config_cstr, tun_fd);
    env->ReleaseStringUTFChars(config_json, config_cstr);
    return ret;
}

JNIEXPORT void JNICALL
Java_com_gyvacha_androidssh_utils_SingboxNative_stop(JNIEnv* env, jobject /*thiz*/) {
    if (!Stop) {
        LOGE("Stop not loaded");
        return;
    }
    Stop();
}

}
