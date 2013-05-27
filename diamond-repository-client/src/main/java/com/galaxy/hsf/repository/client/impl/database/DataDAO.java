/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.impl.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.galaxy.hsf.repository.client.Data;
import com.galaxy.hsf.repository.client.Key;


/**
 * 
 * @author sihai
 *
 */
public class DataDAO {
	
	private static final String SQL_QUERY_BY_KEY = "SELECT `key`, value, create_time, last_modified_time FROM data WHERE `key` = '%s'";
	private static final String SQL_INSERT = "INSERT INTO data (`key`, value, create_time, last_modified_time) VALUES ('%s', '%s', now(), now())";
	private static final String SQL_DELETE = "DELETE FROM data WHERE `key` = '%s'";
	
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
	 */
	public void insert(Data data) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.execute(String.format(SQL_INSERT, new Object[]{data.getKey().getFullKey(), data.getValue()}));
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
				Data data = new Data(key, resultSet.getString("value"));
				return data;
			}
			return null;
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
	
	/**
	 * 
	 * @param fullKey
	 */
	public void delete(String fullKey) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			statement.execute(String.format(SQL_DELETE, new Object[]{fullKey}));
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
