package com.thinkgem.jeesite.common.service;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.thinkgem.jeesite.common.utils.CookieUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.Servlets;
/**
 *
 * @Company:
 * @Description:根据缓存中设置语言获取国际化资源信息
 * @fileName:LocaleService.java
 * @author:wen.zhang   <br>
 * @date 2016年3月27日 上午11:48:47
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 * @JDK version used:jdk1.7.0_02
 * @Update date： 2016年3月27日 上午11:48:47
 * @Update author：
 */
@Service
public class LocaleService extends BaseService {

	@Autowired
	CookieLocaleResolver resolver;

	@Autowired
	private MessageSource messageSource;

	/**
	 * 获取用户设置的语言版本
	 *
	 * @return
	 */
	public Locale getLocale() {
		Locale locale = null;
		//获取语言缓存值
		String cookieLanguage = CookieUtils.getCookie(Servlets.getRequest(), "language");
		//如果不为空
		if (StringUtils.isBlank(cookieLanguage)) {

			try {
				locale = new Locale(cookieLanguage);
			} catch (Exception ex) {
				logger.error("获取用户语言异常:{}", ex);
			}

		}
		if (null == locale) {
			locale = resolver.resolveLocale(Servlets.getRequest());
		}
		return locale;
	}


}
