package com.ibm.atmn.waffle.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.ibm.atmn.waffle.core.RetryAnalyzer;


public class BaseRetryListener implements IAnnotationTransformer {

	@Override
	public void transform(ITestAnnotation annotation, Class c, Constructor constructor,
			Method method) {
			annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}
}
