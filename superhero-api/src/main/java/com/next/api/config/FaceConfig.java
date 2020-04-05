package com.next.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "com.next")
@PropertySource("classpath:face.properties")
public class FaceConfig {
    private  String faceFileSpace;
    private  String url;

    public String getFaceFileSpace() {
        return faceFileSpace;
    }

    public void setFaceFileSpace(String faceFileSpace) {
        this.faceFileSpace = faceFileSpace;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FaceConfig{" +
                "faceFileSpace='" + faceFileSpace + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
