package com.joinflatshare.interfaces

import java.io.File

interface OnFileFetched {
    fun onFetched(file: File)
}