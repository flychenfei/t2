package com.britesnow.samplesocial.dao;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Runner;
import org.j8ql.RunnerProxy;
import org.j8ql.query.*;
import org.postgresql.ds.PGSimpleDataSource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.*;

@Singleton
public class DaoHelper {

	DB db;
	private HikariDataSource hds;

	ThreadLocal<SessionRunner> sessionRunnerHolder = new ThreadLocal<>();

	@Inject
	public DaoHelper(@Named("db.url") String url, @Named("db.user") String user, @Named("db.pwd") String pwd) {

		Properties p = new Properties(System.getProperties());
		System.setProperties(p);

		HikariConfig config = new HikariConfig();
		PGSimpleDataSource pg = new PGSimpleDataSource();
		try {
			pg.setUrl(url);
			pg.setUser(user);
			pg.setPassword(pwd);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		config.setDataSource(pg);
		config.setMaximumPoolSize(10);
		hds = new HikariDataSource(config);

		db = new DBBuilder().build(hds);
	}

	// --------- Runner Session Management --------- //
	/**
	 * Call this to create a Runner for the running thread so that all the DAO calls
	 * for this thread will use this runner.
	 * IMPORTANT: make sure to call the closeRunnerSession at the end of the thread activity
	 * 			(should be in a finally block)
	 */
	public void openRunnerSession(){
		sessionRunnerHolder.set(new SessionRunner(db.openRunner()));
	}

	public void closeRunnerSession(){
		SessionRunner sessionRunner = sessionRunnerHolder.get();
		sessionRunnerHolder.remove();
		if (sessionRunner != null){
			sessionRunner.closeSession();
		}
	}
	// --------- /Runner Session Management --------- //

	public Map getPoolInfo(){
		Map poolInfo = new HashMap();
		poolInfo.put("maxLifetime",hds.getMaxLifetime());
		poolInfo.put("minimumIdle",hds.getMinimumIdle());
		poolInfo.put("maximumPoolSize",hds.getMaximumPoolSize());
		return poolInfo;
	}

	public Runner openRunner(){
		// return the sessionRunner or open a new one if none in the session
		Runner runner = sessionRunnerHolder.get();
		return (runner != null)?runner:db.openRunner();
	}

	// --------- DML Builders --------- //
	public <T> T execute(InsertQuery<T> builder){
		try (Runner runner = openRunner()) {
			return runner.exec(builder);
		}
	}

	public <T> T execute(UpdateQuery<T> builder){
		try (Runner runner = openRunner()) {
			return runner.exec(builder);
		}
	}

	public <T> T execute(DeleteQuery<T> builder){
		try (Runner runner = openRunner()) {
			return runner.exec(builder);
		}
	}
	// --------- /DML Builders --------- //


	// --------- SelectQuery --------- //
	public <T> Optional<T> first(SelectQuery<T> builder){
		try (Runner runner = openRunner()) {
			return runner.first(builder);
		}
	}

	public long count(SelectQuery builder){
		try (Runner runner = openRunner()) {
			return runner.count(builder);
		}
	}

	public <T> List<T> list(SelectQuery<T> builder){
		try (Runner runner = openRunner()) {
			return runner.list(builder);
		}
	}
	// --------- /SelectQuery --------- //

	public int executeUpdate(String sql,Object... values) {
		try (Runner runner = openRunner()) {
			return runner.executeUpdate(sql, values);
		}
	}
}

class SessionRunner extends RunnerProxy {


	public SessionRunner(Runner runner) {
		super(runner);
	}

	@Override
	public void close() {
		// Do nothing, because it is a session runner.
	}

	public void closeSession(){
		runner.close();
	}
}