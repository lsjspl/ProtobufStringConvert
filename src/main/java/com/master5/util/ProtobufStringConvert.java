package com.master5.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ProtobufStringConvert {
	
	private static String LINE_SPLIT_REX="[\n\r]";
	
	private static String CODE_OPEN="{";
	
	private static String CODE_CLOSE="}";
	
	private static String CODE_STRING_CONTENT_REX="\"[\\s\\S]*?\"";
	
	private static String CODE_STRING_FLAG="\"";
	
	private static String CODE_TRUE_FLAG="true";
	
	private static String CODE_FALSE_FLAG="false";
	
	private static String CODE_DOUBLE_FLAG=".";
	
	
	
	/**
	 * 解析protobuf的Message对象使用toString方法生成的字符串到Hash对象
	 * @param sspRequestStr  Message对象使用toString方法生成的字符串
	 * @return hashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> coventStringToMap(String protobufString){
	
			String[] results = protobufString.split(LINE_SPLIT_REX);
			String[] resultsTmp;
			String compareStr;
			Stack<Map<String, Object>> stack = new Stack<>();
			stack.push(new HashMap<String,Object>());
			Map<String, Object> cache;
			List<Object> valueList;
			String key;
			String value;
			Object valueObject;

			for (String result : results) {
				compareStr = result.replaceAll(CODE_STRING_CONTENT_REX, "").trim();
				if (compareStr.startsWith(CODE_OPEN) || compareStr.endsWith(CODE_OPEN)) {//判断开始符号
					cache = new HashMap<String, Object>();
					stack.peek().put(result.replace(CODE_OPEN, "").trim(), cache);
					stack.push(cache);
					continue;
				}

				if (compareStr.startsWith(CODE_CLOSE) || compareStr.endsWith(CODE_CLOSE)) {//判断结束符号
					stack.pop();
					continue;
				}

				resultsTmp = result.split(":");
				key = resultsTmp[0].trim();
				value = resultsTmp[1].trim();
				cache = stack.peek();

				if (value.equals(CODE_FALSE_FLAG) || value.equals(CODE_TRUE_FLAG)) {
					valueObject = Boolean.parseBoolean(value);
				} else if (!value.startsWith(CODE_STRING_FLAG)) {//以引号开始是字符串
					if (value.contains(CODE_DOUBLE_FLAG)) {//浮点型
						valueObject = Double.parseDouble(value);
					} else {
						valueObject = Integer.parseInt(value);
					}
				} else {
					if (value.startsWith(CODE_STRING_FLAG)) {
						valueObject = value.substring(1, value.length() - 1);
					} else {
						valueObject = value;
					}
				}

				if (cache.containsKey(key)) {
					if (cache.get(key) instanceof List) {
						valueList = (List<Object>) cache.get(key);
					} else {
						valueList = new ArrayList<>();
						valueList.add(cache.get(key));
					}
					valueList.add(valueObject);
					cache.put(key, valueList);
				} else {
					cache.put(key, valueObject);
				}

			}

			return stack.pop();
		}
		
}
