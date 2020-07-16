package com.mrh.knife_compiler;

import com.google.auto.service.AutoService;
import com.mrh.knife_annotation.BindView;
import com.mrh.knife_api.SimpleKnifeBinder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by haoxinlei on 2020/6/9.
 */
@AutoService(Processor.class)
public class SimpleKnifeProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Messager mMessage;
    private Filer mFiler;
    private Map<String, ProxyInfo> mProxyInfoMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnv.getElementUtils();
        mMessage = processingEnv.getMessager();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(BindView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessage.printMessage(Diagnostic.Kind.NOTE, "simplebutterknife start process annotation");
        mProxyInfoMap.clear();
        searchAnnotation(roundEnvironment);
        generateJavaFile(roundEnvironment);
        return true;
    }

    private void searchAnnotation(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elementsAnnotatedWith) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            PackageElement packageElement = mElementUtils.getPackageOf(typeElement);
            String packageName = packageElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = mProxyInfoMap.get(packageName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(packageName, typeElement.getQualifiedName().toString(), ClassName.get(typeElement.asType()), variableElement);
                mProxyInfoMap.put(packageName, proxyInfo);
            } else {
                proxyInfo.variableElements.add(variableElement);
            }
        }
    }

    private void generateJavaFile(RoundEnvironment roundEnvironment) {
        if (mProxyInfoMap.size() == 0) {
            return;
        }
        for (String packageName : mProxyInfoMap.keySet()) {
            ProxyInfo proxyInfo = mProxyInfoMap.get(packageName);

            MethodSpec methodSpec = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(Object.class, "host")
                    .addParameter(Object.class, "source")
                    .addStatement(proxyInfo.generateCode())
                    .build();
            TypeSpec typeSpec = TypeSpec.classBuilder(proxyInfo.parseBinderClassName())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpec)
                    .addSuperinterface(SimpleKnifeBinder.class)
                    .build();

            JavaFile file = JavaFile.builder(proxyInfo.packageName, typeSpec)
                    .build();
            try {
                file.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
