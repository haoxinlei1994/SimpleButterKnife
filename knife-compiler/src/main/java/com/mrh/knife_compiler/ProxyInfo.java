package com.mrh.knife_compiler;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.VariableElement;

/**
 * Created by haoxinlei on 2020/6/10.
 */
public class ProxyInfo {
    public String packageName;
    public String className;
    public List<VariableElement> mVariableElements = new ArrayList<>();

    public ProxyInfo(String packageName, String className, VariableElement variableElement) {
        this.packageName = packageName;
        this.className = className;
        mVariableElements.add(variableElement);
    }

    public String parseBinderClassName() {
        return className.substring(packageName.length() + 1) + "$$Binder";
    }
}
