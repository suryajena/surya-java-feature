package com.java.reactive.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.java.reactive.stackoverflow.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import java.util.concurrent.*;

public class AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractFuturesTest.class);

	protected final ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory("Custom"));

	@Rule
	public TestName testName = new TestName();

	protected ThreadFactory threadFactory(String nameFormat) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat + "-%d").build();
	}

	protected final StackOverflowClient client = new FallbackStubClient(
			new InjectErrorsWrapper(
					new LoggingWrapper(
							new ArtificialSleepWrapper(
									new HttpStackOverflowClient()
							)
					), "php"
			)
	);

	
	@BeforeClass
	public static void start() {
		

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root. setLevel(Level.INFO);
		System.setProperty("org.slf4j.simpleLogger.defaultLogLeveldebug", "debug");
	} 
	@Before
	public void logTestStart() {
		log.debug("Starting: {}", testName.getMethodName());
	}

	@After
	public void stopPool() throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
	}

	protected CompletableFuture<String> questions(String tag) {
		return CompletableFuture.supplyAsync(() ->
				client.mostRecentQuestionAbout(tag),
				executorService);
	}

}
