package com.github.otbproject.otbproject.util;

/**
 * Created by Justin on 13/03/2015.
 */
public class VersionClass {

    public String getVersion(){
        return getClass().getPackage().getImplementationVersion();
    }
}
