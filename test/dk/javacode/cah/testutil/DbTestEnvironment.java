package dk.javacode.cah.testutil;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import com.mchange.v2.c3p0.PooledDataSource;

import dk.javacode.cah.database.connection.DbConnectionHandler;
import dk.javacode.cah.database.connection.IDbConnectionHandler;


public class DbTestEnvironment implements IDbConnectionHandler {

	private IDatabaseTester databaseTester;

	private FileUtil fileUtil;

	private String dbName = ".h2/dbtest";

	private String schemaDir = "test/sql/";
	private String updateDir = "test/sql/updates";
	private List<String> schemaFiles;
	private List<String> updateFiles;

	private String dataFile;

	private Connection connection;

	public DbTestEnvironment(String dataFile) {
		this.schemaFiles = getSqlFiles(schemaDir);
		this.dataFile = dataFile;		
		
		this.updateFiles = getSqlFiles(updateDir);
		
		System.out.println(schemaFiles);
	}
	
	private List<String> getSqlFiles(String dir) {
		String[] tmp = new File(dir).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sql");
			}
		});
		List<String> res = new ArrayList<String>(tmp.length);
		for (String f : tmp) {
			res.add(f);
		}
		Collections.sort(res);
		return res;
	}

	public void setUp() throws Exception {
		DbConnectionHandler.instance = this;
		fileUtil = new FileUtil();
		setupH2Db();

		setupDbUnit();

		connection = DriverManager.getConnection(getDbUrl());
	}

	public void tearDown() throws Exception {
		databaseTester.onTearDown();
		connection.close();
	}

	private void setupDbUnit() throws ClassNotFoundException, MalformedURLException, DataSetException, Exception {
		// Create JdbcDatabaseTest using h2
		databaseTester = new JdbcDatabaseTester("org.h2.Driver", getDbUrl());

		// Load the dataset which will be inserted before the test.
		IDataSet dataSet = null;
		FlatXmlDataSetBuilder xml = new FlatXmlDataSetBuilder();
		dataSet = xml.build(new File("test-data/" + dataFile));
		databaseTester.setDataSet(dataSet);

		// will call default setUpOperation (clean insert of dataset)
		databaseTester.onSetup();
	}

	private void setupH2Db() throws SQLException {
		// Load Table definitions from file
		String sql = "DROP ALL OBJECTS";

		// Execute table definitions against db
		Connection connection = DriverManager.getConnection(getDbUrl());
		Statement stmt = connection.createStatement();
		stmt.execute(sql);
		
		for (String sqlFile : schemaFiles) {
			System.out.println("Applying sql: " + sqlFile);
			String createSql = fileUtil.readFile(schemaDir + "/" + sqlFile);
			createSql = createSql.replace("\\'", "''");
			createSql = createSql.replace("ENGINE=InnoDB DEFAULT CHARSET=latin1", "");
			System.out.println(createSql);
			stmt = connection.createStatement();
			stmt.execute(createSql);
		}
		for (String sqlFile : updateFiles) {
			System.out.println("Applying update sql: " + sqlFile);
			String updateSql = fileUtil.readFile(updateDir + "/" + sqlFile);
			updateSql = updateSql.replace("\\'", "''");
			updateSql = updateSql.replace("ON UPDATE CURRENT_TIMESTAMP", "");
			updateSql = updateSql.replace("DEFAULT CURRENT_TIMESTAMP", "");
			updateSql = updateSql.replaceAll(".*drop foreign key.*\n", "");
			System.out.println(updateSql);
			stmt = connection.createStatement();
			stmt.execute(updateSql);
		}
	}

	public String getDbUrl() {
		return "jdbc:h2:" + dbName + ";MODE=MYSQL";
		// return "jdbc:mysql://" + dbHost + "/" + dbName + "?user=" + dbUser +
		// "&password=" + dbPass;
	}

	@Override
	public Connection getConnection() throws SQLException, PropertyVetoException {
		return DriverManager.getConnection(getDbUrl());
	}

	@Override
	public PooledDataSource getDataSource() throws PropertyVetoException {
		return null;
	}	
	
	
}
