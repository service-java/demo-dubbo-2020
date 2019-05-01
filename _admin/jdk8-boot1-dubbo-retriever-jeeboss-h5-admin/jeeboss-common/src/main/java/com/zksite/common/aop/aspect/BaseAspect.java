package com.zksite.common.aop.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class BaseAspect {

	/**
	 * 获取切入目标方法
	 * 
	 * @return
	 */
	protected Method getTargetMethod(final ProceedingJoinPoint pjp) {
		return MethodSignature.class.cast(pjp.getSignature()).getMethod();
	}

	/**
	 * 根据指定注解类获取方法中的注解信息
	 * 
	 * @param pjp
	 * @param annotationClass
	 * @return
	 */
	protected <T extends Annotation> T getAnnotation(final ProceedingJoinPoint pjp, Class<T> annotationClass) {
		Method targetMethod = getTargetMethod(pjp);
		T annotation = targetMethod.getAnnotation(annotationClass);
		if (annotation == null) {
			annotation = targetMethod.getDeclaringClass().getAnnotation(annotationClass);
		}
		return annotation;
	}

	protected <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
		return method.getAnnotation(annotationClass);
	}

	/**
	 * 根据指定注解类获取拦截方法中的参数值
	 * 
	 * @param pjp
	 * @param annotationClass
	 * @return
	 */
	protected List<Object> getMethodParametersByAnnotation(final ProceedingJoinPoint pjp, Class<?> annotationClass) {
		return getMethodParametersByAnnotation(pjp, getTargetMethod(pjp), annotationClass);
	}

	/**
	 * 根据指定注解方法获取拦截方法中的参数值
	 * 
	 * @param pjp
	 * @param annotationClass
	 * @return
	 */
	protected List<Object> getMethodParametersByAnnotation(final ProceedingJoinPoint pjp, Method method,
			Class<?> annotationClass) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			return Collections.emptyList();
		}

		List<Object> result = new ArrayList<Object>();
		int i = 0;
		for (Annotation[] annotations : parameterAnnotations) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(annotationClass)) {
					result.add(pjp.getArgs()[i]);
					continue;
				}
			}
			i++;
		}

		return result;
	}

	/**
	 * 根据指定注解方法获取拦截方法中的注解及参数值
	 * @param pjp
	 * @param method
	 * @param annotationClass
	 * @return List<[annotation, value]>
	 */
	protected List<Object[]> getMethodAnnotationAndParametersByAnnotation(final ProceedingJoinPoint pjp, Method method,
			Class<?> annotationClass) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			return Collections.emptyList();
		}

		List<Object[]> result = new ArrayList<Object[]>();
		int i = 0;
		for (Annotation[] annotations : parameterAnnotations) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(annotationClass)) {
					result.add(new Object[] { annotation, pjp.getArgs()[i] });
					continue;
				}
			}
			i++;
		}

		return result;
	}
	
}
