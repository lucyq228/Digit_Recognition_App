package com.example.cheng.camera_app.models;

/**
 * Created by cheng on 3/7/2018.
 */

public class Classification {

    //conf is the output
    private float conf;
    //input label
    private String label;

    Classification() {
        this.conf = -1.0F;
        this.label = null;
    }

    void update(float conf, String label) {
        this.conf = conf;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public float getConf() {
        return conf;
    }
}
