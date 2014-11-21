package com.britesnow.samplesocial.feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.snow.web.hook.AppPhase;
import com.britesnow.snow.web.hook.annotation.WebApplicationHook;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class FeedGmailAnalyticsManager {
	// Pause duration between UserFeed scan
	static private final long SLEEP = 5000;
	
	private volatile List<String> users = new ArrayList<String>();
	/*
	Injector to create the FeedJob which use Guice annotations and needs to be created per job.
	 */
	@Inject
	private Injector injector;

	// when hasTask is set to true, there has new task
	private volatile boolean hasTask = false;
	
	// new user will start a task
	private volatile User user = null;
	
	// this is the thread for this FeedJobManager
	private Thread mainThread = new Thread(() -> run());
	
	// when on is set to false, the main thread stop iterating
	private volatile boolean on = true;
		
	// TODO: Will need to have a pool here (pool-size = 10).
	private ExecutorService feedJobPool = Executors.newCachedThreadPool();
	
	private List<CompletableFuture<HashMap<String, String>>> futures = new CopyOnWriteArrayList<>();
	
	/**
	 * Start the main thread that will look at the UserFeedInfo rows to perform eventual feedjob.
	 *
	 * Must be called only once, probably in a @WebApplicationHook AppPhase.Start
	 *
	 */
	@WebApplicationHook(phase = AppPhase.INIT)
	public void start(){
		mainThread.start();
	}
	
	public synchronized void addTask(User user){
		this.hasTask = true;
		this.user = user;
	}
	
	/**
	 * Stop the main thread and all the FeedJobs and wait until everything is completed and return.
	 */
	public void stop(){
		System.out.println("FeedJobManager.stop ");
		try {
			// tell this FeedJobManager to stop iterating
			on = false;
			System.out.println("FeedJobManager.stop  before mainThread.join()");
			// wait for the main thread to be done
			mainThread.join();
			System.out.println("FeedJobManager.stop  after mainThread.join()");

			System.out.println("FeedJobManager.stop  before awaitTermination");
			// wait for all the FeedJob to terminate
			feedJobPool.shutdown();
			feedJobPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			System.out.println("FeedJobManager.stop  after awaitTermination");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		// TODO: needs to stop all FeedJob and wait
	}

	/**
	 * No need to be a Runnable interface, as we are using lambda in the mainThread initialization.
	 */
	@SuppressWarnings("static-access")
	private void run() {
		System.out.println("FeedJobManager.run " + on);
		while (on) {
			if(hasTask){
				if(!users.contains(user.getUsername())){
					startNewFeedJob(user);
					users.add(user.getUsername());
				}
				resetTask();
			}
			// we sleep for a little while, before, looking for more work.
			try {
				Thread.currentThread().sleep(SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("FeedJobManager.run DONE");
	}
	
	private void startNewFeedJob(User user) {

		FeedGmailAnalyticsJob feedGmailAnalyticsJob = injector.getInstance(FeedGmailAnalyticsJob.class);
		feedGmailAnalyticsJob.init(user);

		// Note: Because we are using a ExecutorService feedJobPool, we can call this as much as we want, they will get queue
		CompletableFuture<HashMap<String, String>> future =  CompletableFuture.supplyAsync(() -> feedGmailAnalyticsJob.call(), feedJobPool);
		futures.add(future);
		future.thenAccept(result -> {if("true".equals(result.get("success"))){
										System.out.println("For the user "+result.get("name")+" , the gmailAnalytice job has succeeded!");
									}else{
										System.out.println("For the user "+result.get("name")+" , the gmailAnalytice job has failed!");
									}
							users.remove(result.get("name"));
						});
	}
	
	private void resetTask(){
		this.hasTask = false;
		this.user = null;
	}
}
