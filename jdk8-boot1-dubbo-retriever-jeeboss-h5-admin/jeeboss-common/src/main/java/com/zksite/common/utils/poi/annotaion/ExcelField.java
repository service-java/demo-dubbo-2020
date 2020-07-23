package com.zksite.common.utils.poi.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {

    /**
     * 导出列名称
     * 
     * @return
     */
    String name();

    /**
     * 如果注解的字段是一个日期，可以指定导出的日期格式
     * 
     * @return
     */
    String dateFormat() default "";
    
    
}
