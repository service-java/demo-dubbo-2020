package com.zksite.common.utils;

import java.util.List;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMap;

public abstract class AbstractBeanMapper {
	protected static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

	protected abstract List<ClassMap<?, ?>> getRegisterClassMaps();
	
	protected MapperFacade mapperFacade;

	protected AbstractBeanMapper() {
		for (ClassMap<?, ?> classMap : getRegisterClassMaps()) {
			mapperFactory.registerClassMap(classMap);
		}
		
		mapperFacade = mapperFactory.getMapperFacade();
	}

	public <A, B> BoundMapperFacade<A, B> getMapperFacade(Class<A> aClass, Class<B> bClass) {
		return mapperFactory.getMapperFacade(aClass, bClass);
	}

	public MapperFacade getMapperFacade() {
		return mapperFacade;
	}
}
