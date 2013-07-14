/**
 * Copyright 2013 Qiangqiang RAO
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
package com.galaxy.diamond.repository.client.impl.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.galaxy.diamond.repository.client.Data;
import com.galaxy.diamond.repository.client.Key;
import com.galaxy.diamond.repository.client.exception.SequenceNotMatchException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;


/**
 * 
 * @author sihai
 *
 */
public class DataDAO {
	
	private static final String SQL_QUERY_BY_KEY = "SELECT `key`, value, sequence, create_time, last_modified_time FROM data WHERE `key` = '%s'";
	private static final String SQL_INSERT = "INSERT INTO data (`key`, value, sequence, create_time, last_modified_time) VALUES ('%s', '%s', %d, now(), now())";
	private static final String SQL_UPDATE = "UPDATE data SET value = '%s', sequence = %d WHERE `key` = '%s' AND sequence = %d";
	private static final String SQL_DELETE = "DELETE FROM data WHERE `key` = '%s' AND sequence = %d";
	
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
			resultSet = statement.executeQuery(String.format(SQL_QUERY_BY_KEY, new Object[]{data.getKey().getFullKey()}));
			boolean ret = false;
			if(resultSet.next()) {
				ret = _update_(connection, data);
			} else {
				ret = _insert_(connection, data);
			}
			if(!ret) {
				throw new SequenceNotMatchException(String.format("Sequence Not Match, key:%s, sequence:%d", data.getKey().getFullKey(), data.getKey().getSequence()));
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
	 * Full key
	 * @param fullKey
	 * @return
	 */
	public Data query(String fullKey) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(String.format(SQL_QUERY_BY_KEY, new Object[]{fullKey}));
			if(resultSet.next()) {
				Key key = new Key();
				key.setFullKey(resultSet.getString("key"));
				key.setSequence(resultSet.getLong("sequence"));
				Data data = new Data(key, resultSet.getString("value"), resultSet.getLong("sequence"));
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
	 * @param connection
	 * @param data
	 * @return
	 */
	private boolean _insert_(Connection connection, Data data) {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(String.format(SQL_INSERT, new Object[]{data.getKey().getFullKey(), data.getValue(), data.getKey().getSequence()}));
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
			return 1 == statement.executeUpdate(String.format(SQL_UPDATE, new Object[]{data.getValue(), data.getKey().getSequence() + 1, data.getKey().getFullKey(), data.getKey().getSequence()}));
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
			statement.execute(String.format(SQL_DELETE, new Object[]{key.getFullKey(), key.getSequence()}));
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
