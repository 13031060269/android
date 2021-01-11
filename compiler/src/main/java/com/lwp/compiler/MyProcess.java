package com.lwp.compiler;

import com.google.auto.service.AutoService;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;

@AutoService(Processor.class)
public class MyProcess extends BaseProcess {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> result = new HashSet<>();
        result.add(Override.class.getCanonicalName());
        return result;
    }
}
