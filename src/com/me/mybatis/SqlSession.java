package com.me.mybatis;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.me.bean.MapperInfo;

public class SqlSession {

	private SqlSessionFactory factory; //SqlSessionFactory
	private DBHelper db;

	public SqlSession(SqlSessionFactory factory){
		this.factory=factory;
		db=new DBHelper(factory.getConfig().getDataSource()); //用已经解析好的数据源实体类去初始化DBhelper
	}

	/**
	 * 查询
	 * @param sqlId mapper映射文件下的节点id
	 * @param params 参数
	 * @return
	 */
	public <T> List<T> selectList(String sqlId,Object ... params) {
		String sql;//要执行的sql语句
		Class C;  //反射的对象
		try {
			MapperInfo mapperInfo=factory.getMapperInfos().get(sqlId);//获取该sqlId节点下的所有信息
			if (mapperInfo==null) {
				return null;
			}

			sql=mapperInfo.getSql(); //获取sql语句
			if (mapperInfo.isUpdate()) {
				return null;
			}

			String className = mapperInfo.getResultType();//获取返回值类型
			C=Class.forName(className); //反射得到该返回值类型的class
			return db.findObject(sql, C,params); //调用dbhelper进行数据查询操作

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 更新情况（update，insert,delete）
	 * @param sqlId
	 * @param obj
	 * @return
	 */
	public int update(String sqlId,Object obj) {
		int result=0; //执行结果

		MapperInfo mapperInfo = factory.getMapperInfos().get(sqlId);
		if (mapperInfo==null) {
			return 0;
		}

		String sql=mapperInfo.getSql();
		String className = mapperInfo.getParameterType();

		Pattern pattern = Pattern.compile("[#][{]\\w+}"); //正则表达式 。将#{xxxx}变成#{?}
		Matcher matcher = pattern.matcher(sql);

		ArrayList<String> colNames = new ArrayList<String>();
		while (matcher.find()) {
			colNames.add(matcher.group().replaceAll("[#{}]*", ""));
		}
		sql=matcher.replaceAll("?");
		//如果参数类型为map
		if ("map".equals(className)) {
			Map<String, Object> map = (Map<String, Object>) obj;
			List<Object> params=new ArrayList<Object>(); //参数集合
			for (String colName:colNames) {
				params.add(map.get(colName));
			}
			return db.update(sql, params);//使用DBhelper查询
		}
		if ("String".equals(className)) {
			return -1;
		}

		//参数类型为对象时
		try {
			Class c =Class.forName(className); //反射得到class
			Method[] methods=c.getDeclaredMethods();  //获取该类中的声明方法
			List<Method> getMethods = new ArrayList<Method>();  //保存该对象的get方法
			for (Method md:methods) {
				if (md.getName().startsWith("get")) { 
					getMethods.add(md); //如果方法为get方法，保存
				}
			}
			List<Object> params=new ArrayList<Object>();//参数集合
			String mname;
			for (String colName:colNames) { 
				for(Method md:getMethods){
					mname="get"+colName.substring(0,1).toUpperCase()+colName.substring(1);//转换为getXxx的形式,例如getdeptno转换为getDeptno
					if (mname.equals(md.getName())) { //用get方法或的参数
						params.add(md.invoke(obj));  //反向执行
					}
				}
			}
			return db.update(sql, params); //返回执行结果
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
