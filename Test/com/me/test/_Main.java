package com.me.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.me.bean.DeptBean;
import com.me.mybatis.SqlSession;
import com.me.mybatis.SqlSessionFactory;

public class _Main {

	@Test
	public void test() {
		SqlSessionFactory sqFactory = new SqlSessionFactory("mybatis-config.xml");
		//System.out.println(sqFactory.getMapperInfos());
		SqlSession session = new SqlSession(sqFactory);
		List<Object> dept = session.selectList("findAll");
		System.out.println(dept);
	}
	@Test
	public void test2() {
		SqlSessionFactory sqFactory = new SqlSessionFactory("mybatis-config.xml");
		SqlSession session = new SqlSession(sqFactory);
		int result=session.update("addDept", new DeptBean(88,"hah","666"));
		System.out.println(result);
		
	}
	
	@Test
	public void test3() {
		SqlSessionFactory sqFactory = new SqlSessionFactory("mybatis-config.xml");
		SqlSession session = new SqlSession(sqFactory);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deptno", 99);
		map.put("dname", "呵呵");
		map.put("loc", "888");
		int result=session.update("addDept1", map);
		System.out.println(result);
	}
}
