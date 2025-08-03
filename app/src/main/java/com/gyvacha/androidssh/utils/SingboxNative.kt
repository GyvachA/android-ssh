package com.gyvacha.androidssh.utils

object SingboxNative {

    external fun start(config: String, tunFd: Int): Int
    external fun stop()
}