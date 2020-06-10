package com.mrh.knife_api;

import com.mrh.knife_annotation.SimpleKnifeBinder;

public class SimpleButterKnife {
    public static void bind(Object host, Object source) {
        String className = host.getClass().getName() + "$$Binder";
        try {
            SimpleKnifeBinder binder = (SimpleKnifeBinder) Class.forName(className).newInstance();
            binder.bind(host, source);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
