package com.me.mybatis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import com.me.bean.MapperInfo;

public class SqlSessionFactory {

	private MyBatisConfig config;  //mybatis-config.xml的解析结果
	private Map<String, MapperInfo> mapperInfos =new HashMap<String, MapperInfo>();//mapper映射文件的解析结果

	public  SqlSessionFactory(String configXml) {
		config=new MyBatisConfig(configXml);  //解析mybatis-config.xml
		try {
			parseXml(); //解析mapper映射文件
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析mapper映射文件
	 * @throws DocumentException
	 */
	private void parseXml() throws DocumentException {
		List<String> mappers = config.getMappers(); //获取映射文件路径
		if (mappers!=null && !mappers.isEmpty()) {
			SAXReader reader = new SAXReader();
			Document doc= null;
			XPath xPath = null;
			List<Element> ops=null;

			String opname=null;
			MapperInfo mapperInfo=null;

			//循环解析所有映射文件
			for (String mapper:mappers) {
				doc = reader.read(this.getClass().getClassLoader().getResourceAsStream(mapper));
				xPath=doc.createXPath("//mapper/*"); //获取DeptMapper.xml下mapper下的所有节点
				ops=xPath.selectNodes(doc);

				for (Element el:ops) {
					mapperInfo=new MapperInfo(); //创建一个mapper实体类
					opname=el.getName();
					//如果是select节点，说明不是更新语句
					if (opname.equals("select")) { 
						mapperInfo.setUpdate(false);
					}
					//将各项信息填入新创建的mapper实体类
					mapperInfo.setParameterType(el.attributeValue("parameterType"));
					mapperInfo.setResultType(el.attributeValue("resultType"));
					mapperInfo.setSql(el.getTextTrim());

					//保存结果到一个集合中
					mapperInfos.put(el.attributeValue("id"), mapperInfo);
				}
			}
		}
	}

	public MyBatisConfig getConfig() {
		return config;
	}

	public Map<String, MapperInfo> getMapperInfos() {
		return mapperInfos;
	}
}
