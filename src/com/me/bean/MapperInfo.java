package com.me.bean;

public class MapperInfo {
 
	private String parameterType;  //参数类型
	private String resultType;    //返回值类型
	private String sql;			//sql语句
	private boolean isUpdate=true; //是否为更新语句
	@Override
	public String toString() {
		return "MapperInfo [parameterType=" + parameterType + ", resultType="
				+ resultType + ", sql=" + sql + ", isUpdate=" + isUpdate + "]";
	}
	public String getParameterType() {
		return parameterType;
	}
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public boolean isUpdate() {
		return isUpdate;
	}
	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isUpdate ? 1231 : 1237);
		result = prime * result
				+ ((parameterType == null) ? 0 : parameterType.hashCode());
		result = prime * result
				+ ((resultType == null) ? 0 : resultType.hashCode());
		result = prime * result + ((sql == null) ? 0 : sql.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapperInfo other = (MapperInfo) obj;
		if (isUpdate != other.isUpdate)
			return false;
		if (parameterType == null) {
			if (other.parameterType != null)
				return false;
		} else if (!parameterType.equals(other.parameterType))
			return false;
		if (resultType == null) {
			if (other.resultType != null)
				return false;
		} else if (!resultType.equals(other.resultType))
			return false;
		if (sql == null) {
			if (other.sql != null)
				return false;
		} else if (!sql.equals(other.sql))
			return false;
		return true;
	}
	public MapperInfo(String parameterType, String resultType, String sql,
			boolean isUpdate) {
		super();
		this.parameterType = parameterType;
		this.resultType = resultType;
		this.sql = sql;
		this.isUpdate = isUpdate;
	}
	public MapperInfo() {
		super();
	}

	

}
