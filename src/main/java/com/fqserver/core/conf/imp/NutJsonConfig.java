package com.fqserver.core.conf.imp;

import java.io.File;
import java.util.Map;

import com.fqserver.core.conf.BaseConfig;
import com.fqserver.core.conf.NutConfFast;


public class NutJsonConfig extends BaseConfig {

    public static NutJsonConfig newConfig(Map<String, Object> map) {
        NutJsonConfig conf = new NutJsonConfig();
        conf.setStore(map);
        return conf;
    }

    public NutJsonConfig() {

    }

    public NutJsonConfig(String fileName, String nodeName) {
        this();

        String pathOuter = System.getProperty("user.dir") + "/../" + fileName;
        String path = System.getProperty("user.dir") + "/" + fileName;

        File f = new File(pathOuter);
        if (f.exists()) {
            NutConfFast.load(pathOuter);
        }

        f = new File(path);
        if (f.exists()) {
            NutConfFast.load(path);
        }
        Object obj = nodeName == null ? NutConfFast.getAllConf() : NutConfFast.get(nodeName);
        if (obj != null) {
            this.setStore((Map<String, Object>) obj);
            return;
        }
        throw new RuntimeException("init " + fileName + " file and " + nodeName + " node failed");
    }
}