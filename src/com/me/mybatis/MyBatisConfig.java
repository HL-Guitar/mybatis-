package com.me.mybatis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import com.me.bean.DataSource;

public class MyBatisConfig {
	private DataSource dataSource;  //数据源实体
	private List<String> mappers=new ArrayList<String>(); //保存mapper映射文件
	public MyBatisConfig(String config) {
		try {
			readXml(config);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 解析mybatis-config.xml
	 * @param config //文件路径
	 * @throws DocumentException
	 */
	private void readXml(String config) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(this.getClass().getClassLoader().getResourceAsStream(config));
		
		XPath xPath = doc.createXPath("//dataSource/property"); //解析mybatis-config.xml下的property所有节点
		List<Element> properties = xPath.selectNodes(doc);
		
		dataSource=new DataSource();//创建一个数据源实体
		String pname=null;
		for (Element e1:properties) { //获取所有的property节点信息，保存到数据源实体类中
			pname=e1.attributeValue("name");
			switch (pname) {
			case "driver":
				dataSource.setDriver(e1.attributeValue("value"));
				break;
			case "url":
				dataSource.setUrl(e1.attributeValue("value"));
				break;
			case "username":
				dataSource.setUsername(e1.attributeValue("value"));
				break;
			case "password":
				dataSource.setPassword(e1.attributeValue("value"));
				break;
			
			default:
				break;
			}
		}
		
		//System.out.println(dataSource);
		//开始获取mybatis-config.xml中的<mappers>节点，即映射文件
		xPath = doc.createXPath("//mappers/mapper");
		List<Element> list = xPath.selectNodes(doc);
		for (Element el:list) {
			mappers.add(el.attributeValue("resource")); //保存到一个集合中
		}
		//到这里我们已经解析完了mybatis-config.xml
		//System.out.println(mappers); 
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public List<String> getMappers() {
		return mappers;
	}
	
	public static void main(String[] args) {
		MyBatisConfig myBatisConfig = new MyBatisConfig("mybatis-config.xml");
	}
}
