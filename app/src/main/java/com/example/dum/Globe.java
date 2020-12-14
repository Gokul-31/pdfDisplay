package com.example.dum;

import android.app.Application;

public class Globe extends Application {
    public int data=20;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
