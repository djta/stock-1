package com.stock.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

	private static ReloadableResourceBundleMessageSource messageResource;

	@Autowired
	private void setMessageResource(
			ReloadableResourceBundleMessageSource messageResource) {
		MessageUtil.messageResource = messageResource;
	}

	public static String getMessage(String msgCode, Object... params) {
		return messageResource.getMessage(msgCode, params,
				LocaleContextHolder.getLocale());
	}

	public static String unicode2String(String unicode) {
		if (StringUtils.isBlank(unicode))
			return null;
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;

		while ((i = unicode.indexOf("\\u", pos)) != -1) {
			sb.append(unicode.substring(pos, i));
			if (i + 5 < unicode.length()) {
				pos = i + 6;
				sb.append((char) Integer.parseInt(
						unicode.substring(i + 2, i + 6), 16));
			}
		}

		return sb.toString();
	}

}
