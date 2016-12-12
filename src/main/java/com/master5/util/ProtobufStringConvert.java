package com.master5.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ProtobufStringConvert {
	
	private static String lineSplitRex="[\n\r]";
	
	private static String codeOpen="{";
	
	private static String codeClose="}";
	
	private static String codeStringContentRex="\"[\\s\\S]*?\"";
	
	private static String codeStringFlag="\"";
	
	private static String codeTrueFlag="true";
	
	private static String codeFalseFlag="false";
	
	private static String codeDoubleFlag=".";
	
	
	
	/**
	 * 解析protobuf的Message对象使用toString方法生成的字符串到Hash对象
	 * @param sspRequestStr  Message对象使用toString方法生成的字符串
	 * @return hashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> coventStringToMap(String protobufString){
	
			String[] results = protobufString.split(lineSplitRex);
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
				compareStr = result.replaceAll(codeStringContentRex, "").trim();
				if (compareStr.startsWith(codeOpen) || compareStr.endsWith(codeOpen)) {//判断开始符号
					cache = new HashMap<String, Object>();
					stack.peek().put(result.replace(codeOpen, "").trim(), cache);
					stack.push(cache);
					continue;
				}

				if (compareStr.startsWith(codeClose) || compareStr.endsWith(codeClose)) {//判断结束符号
					stack.pop();
					continue;
				}

				resultsTmp = result.split(":");
				key = resultsTmp[0].trim();
				value = resultsTmp[1].trim();
				cache = stack.peek();

				if (value.equals(codeFalseFlag) || value.equals(codeTrueFlag)) {
					valueObject = Boolean.parseBoolean(value);
				} else if (!value.startsWith(codeStringFlag)) {//以引号开始是字符串
					if (value.contains(codeDoubleFlag)) {//浮点型
						valueObject = Double.parseDouble(value);
					} else {
						valueObject = Integer.parseInt(value);
					}
				} else {
					if (value.startsWith(codeStringFlag)) {
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
