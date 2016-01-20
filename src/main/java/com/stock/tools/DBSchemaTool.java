package com.stock.tools;

import java.util.Properties;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class DBSchemaTool {
	
	private static String PACKAGE_SCAN = "com";
	
	private static final Configuration	configuration = new Configuration();
	private static final Properties	prop = new Properties();
	private static final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	
	private static void init(){
		try {
			Resource jdbcRes = resolver.getResource("classpath:jdbc.properties");
			prop.load(jdbcRes.getInputStream());
			
			Reflections reflections = new Reflections(PACKAGE_SCAN, new FieldAnnotationsScanner(), new TypeAnnotationsScanner());
			Set<Class<?>> resourceRegistrarClasses = reflections.getTypesAnnotatedWith(Entity.class);
			
			for (Class<?> clazz : resourceRegistrarClasses) {
				configuration.addAnnotatedClass(clazz);
			}
			
			configuration.buildMappings();
			configuration.setProperty("hibernate.connection.username", prop.getProperty("jdbc.userName"));
			configuration.setProperty("hibernate.connection.password", prop.getProperty("jdbc.password"));
			configuration.setProperty("hibernate.connection.url", prop.getProperty("jdbc.url"));
			configuration.setProperty("hibernate.connection.driver_class", prop.getProperty("jdbc.driverClassName"));
			configuration.setProperty("hibernate.dialect", prop.getProperty("hibernate.dialect"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		init();
		SchemaExport schemaExport = new SchemaExport(configuration);

		System.out.println("Creating schema ...");
		schemaExport.create(true, true);
		System.out.println("Create schema finished.");
	}
}
