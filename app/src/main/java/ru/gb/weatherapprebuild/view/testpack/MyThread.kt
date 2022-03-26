package ru.gb.weatherapprebuild.view.testpack

import android.os.Looper

class MyThread : Thread() {
    override fun run() {
        Looper.prepare()
        Looper.loop()
    }
}
