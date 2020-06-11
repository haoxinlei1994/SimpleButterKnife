package com.mrh.knife_compiler;

import com.mrh.knife_annotation.BindView;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.VariableElement;

/**
 * Created by haoxinlei on 2020/6/10.
 */
public class ProxyInfo {
    public String packageName;
    public String className;
    public TypeName typeName;
    public List<VariableElement> variableElements = new ArrayList<>();

    public ProxyInfo(String packageName, String className, TypeName typeName, VariableElement variableElement) {
        this.packageName = packageName;
        this.className = className;
        this.typeName = typeName;
        variableElements.add(variableElement);
    }

    public String parseBinderClassName() {
        return className.substring(packageName.length() + 1) + "$$Binder";
    }

    /**
     * 生成代码
     * @return
     */
    public String generateCode() {
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < variableElements.size(); i++) {
            VariableElement variableElement = variableElements.get(i);
            codeBuilder
                    .append("((")
                    .append(className)
                    .append(")host).")
                    .append(variableElement.getSimpleName())
                    .append(" = ((android.app.Activity)source).findViewById(")
                    .append(variableElement.getAnnotation(BindView.class).value())
                    .append(")");
            if (i < variableElements.size() - 1) {
                codeBuilder.append(";\n");
            }
        }
        return codeBuilder.toString();
    }
}
