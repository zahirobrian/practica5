package com.ipn.practica5

import android.app.Application
import com.ipn.practica5.repository.MediaRepository

class App : Application() {
    val repository: MediaRepository by lazy { MediaRepository(this) }
}
