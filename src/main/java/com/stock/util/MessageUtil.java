package com.stock.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {
	
	private static ReloadableResourceBundleMessageSource messageResource;
	
	@Autowired
	private void setMessageResource(ReloadableResourceBundleMessageSource messageResource) {
		MessageUtil.messageResource = messageResource;
	}
	
	public static String getMessage(String msgCode, Object...params) {
		return messageResource.getMessage(msgCode, params, LocaleContextHolder.getLocale());
	}

}
