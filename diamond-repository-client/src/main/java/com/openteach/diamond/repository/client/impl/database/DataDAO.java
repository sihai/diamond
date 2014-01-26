/**
 * Copyright 2013 openteach
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package com.openteach.diamond.repository.client.impl.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.openteach.diamond.repository.client.Data;
import com.openteach.diamond.repository.client.Key;
import com.openteach.diamond.repository.client.exception.SequenceNotMatchException;


/**
 * 
 * @author sihai
 *
 */
public class DataDAO {
	
	private static final String SQL_QUERY_BY_KEY_AND_SUBKEY = "SELECT namespace, `key`, sub_key, value, sequence, create_time, last_modified_time FROM data WHERE namespace = '%s' AND `key` = '%s' AND sub_key = '%s'";
	private static final String SQL_QUERY_BY_KEY = "SELECT namespace, `key`, sub_key, value, sequence, create_time, last_modified_time FROM data WHERE namespace = '%s' AND `key` = '%s' AND sub_key <> '%s'";
	private static final String SQL_INSERT = "INSERT INTO data (namespace, `key`, sub_key, value, sequence, create_time, last_modified_time) VALUES ('%s', '%s', '%s', '%s', %d, now(), now())";
	private static final String SQL_UPDATE = "UPDATE data SET value = '%s', sequence = %d WHERE namespace = '%s' AND `key` = '%s' AND sub_key = '%s' AND sequence = %d";
	private static final String SQL_DELETE = "DELETE FROM data WHERE namespace = '%s' AND `key` = '%s' AND sub_key = '%s' AND sequence = %d";
	
	/**
	 * Datasource for dao
	 */
	private DataSource dataSource;
	
	public DataDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * 
	 * @param data
	 * @throws SequenceNotMatchException
	 */
	public void insert(Data data) throws SequenceNotMatchException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(String.format(SQL_QUERY_BY_KEY_AND_SUBKEY, new Object[]{data.getKey().getNamespace(), data.getKey().getKey(), data.getKey().getSubKey()}));
			boolean ret = false;
			if(resultSet.next()) {
				ret = _update_(connection, data);
			} else {
				ret = _insert_(connection, data);
			}
			if(!ret) {
				throw new SequenceNotMatchException(String.format("Sequence Not Match, key:%s", data.getKey()));
			}
		} catch (SQLException e) {
			throw new RuntimeException("OOPS, DAO", e);
		} finally {
			if(null != resultSet) {
				try {
					resultSet.close();
				} catch (SQLException e) {/*  */}
			}
			if(null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {/*  */}
			}
			if(null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {/*  */}
			}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Data query(Key key) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(String.format(SQL_QUERY_BY_KEY_AND_SUBKEY, new Object[]{key.getNamespace(), key.getKey(), Key.NONE_SUB_KEY}));
			if(resultSet.next()) {
				Key k = new Key();
				k.setNamespace(resultSet.getString("namespace"));
				k.setKey(resultSet.getString("key"));
				k.setSubKey(resultSet.getString("sub_key"));
				k.setSequence(resultSet.getLong("sequence"));
				Data data = new Data(key, resultSet.getString("value"));
				return data;
			}
			return null;
		} catch (SQLException e) {
			throw new RuntimeException("OOPS, DAO", e);
		} finally {
			if(null != resultSet) {
				try {
					resultSet.close();
				} catch (SQLException e) {/*  */}
			}
			if(null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {/*  */}
			}
			if(null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {/*  */}
			}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public List<Data> queryList(Key key) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(String.format(SQL_QUERY_BY_KEY, new Object[]{key.getNamespace(), key.getKey(), Key.NONE_SUB_KEY}));
			
			List<Data> dList = new ArrayList<Data>();
			while(resultSet.next()) {
				Key k = new Key();
				k.setNamespace(resultSet.getString("namespace"));
				k.setKey(resultSet.getString("key"));
				k.setSubKey(resultSet.getString("sub_key"));
				k.setSequence(resultSet.getLong("sequence"));
				Data data = new Data(k, resultSet.getString("value"));
				dList.add(data);
			}
			return dList;
		} catch (SQLException e) {
			throw new RuntimeException("OOPS, DAO", e);
		} finally {
			if(null != resultSet) {
				try {
					resultSet.close();
				} catch (SQLException e) {/*  */}
			}
			if(null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {/*  */}
			}
			if(null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {/*  */}
			}
		}
	}
	
	/**
	 * 
	 * @param connection
	 * @param data
	 * @return
	 */
	private boolean _insert_(Connection connection, Data data) {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(String.format(SQL_INSERT, new Object[]{data.getKey().getNamespace(), data.getKey().getKey(), data.getKey().getSubKey(), data.getValue(), data.getKey().getSequence()}));
			return true;
		} catch (SQLException e) {
			if(e instanceof MySQLIntegrityConstraintViolationException) {
				return false;
			}
			throw new RuntimeException("OOPS, DAO", e);
		} finally {
			if(null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {/*  */}
			}
		}
	}
	
	/**
	 * 
	 * @param connection
	 * @param data
	 * @return
	 */
	private boolean _update_(Connection connection, Data data) {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return 1 == statement.executeUpdate(String.format(SQL_UPDATE, new Object[]{data.getValue(), data.getKey().getSequence() + 1, data.getKey().getNamespace(), data.getKey().getKey(), data.getKey().getSubKey(), data.getKey().getSequence()}));
		} catch (SQLException e) {
			throw new RuntimeException("OOPS, DAO", e);
		} finally {
			if(null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {/*  */}
			}
		}
	}
	
	/**
	 * 
	 * @param fullKey
	 */
	public void delete(Key key) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.execute(String.format(SQL_DELETE, new Object[]{key.getNamespace(), key.getKey(), key.getSubKey(), key.getSequence()}));
		} catch (SQLException e) {
			throw new RuntimeException("OOPS, DAO", e);
		} finally {
			if(null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {/*  */}
			}
			if(null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {/*  */}
			}
		}
	}
}
