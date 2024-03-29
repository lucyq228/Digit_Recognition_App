package com.example.cheng.camera_app.models;

/**
 * Created by cheng on 3/7/2018.
 */

//public interface for the classifer
//exposes its name and the recognize function
//which given some drawn pixels as input
//classifies what it sees as an MNIST image
public interface Classifier {
    String name();

    Classification recognize(final float[] pixels);
}

