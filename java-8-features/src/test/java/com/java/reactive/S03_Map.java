package com.java.reactive;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.reactive.util.AbstractFuturesTest;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class S03_Map extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S03_Map.class);

	@Test
	public void oldSchool() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() -> sleep(3),
								//client.mostRecentQuestionsAbout("java"),
						executorService
				);

		final Document document = java.get();       //blocks
		final Element element = document.
				select("a.question-hyperlink").get(0);
		final String title = element.text();
		final int length = title.length();
		log.info("Length: {}", length);
	}

	/**
	 * Callback hell, doesn't compose
	 */
	@Test
	public void callbacksCallbacksEverywhere() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() ->sleep(3),
								//client.mostRecentQuestionsAbout("java"),
						executorService
				);

		java.thenAccept(document ->log(document, 0));
				//log.debug("Downloaded: {}", document));
	}

	@Test
	public void thenApply() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() ->sleep(3),
								//client.mostRecentQuestionsAbout("java"),
						executorService
				);

		final CompletableFuture<Element> titleElement =
				java.thenApply((Document doc) ->
						doc.select("a.question-hyperlink").get(0));

		final CompletableFuture<String> titleText =
				titleElement.thenApply(Element::text);

		final CompletableFuture<Integer> length =
				titleText.thenApply(String::length);

		log.info("Length: {}", length.get());
	}

	@Test
	public void thenApplyChained() throws Exception {
		final CompletableFuture<Document> java =
				CompletableFuture.supplyAsync(() ->sleep(3),
								//client.mostRecentQuestionsAbout("java"),
						executorService
				);

		final CompletableFuture<Integer> length = java.
				thenApply(doc -> doc.select("a.question-hyperlink").get(0)).
				thenApply(Element::text).
				thenApply(String::length);

		log.info("Length: {}", length.get());
	}
	
	public Document sleep(int a){
		try {
			System.out.println(Thread.currentThread().getName()+" sleeping");
			TimeUnit.SECONDS.sleep(a);
			System.out.println(Thread.currentThread().getName()+" woke up");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return client.mostRecentQuestionsAbout("java");
	}
	
	public void log(Object o,int a){
		try {
			System.out.println(Thread.currentThread().getName()+" object:"+Objects.isNull(o));
			System.out.println(Thread.currentThread().getName()+" 2sleeping");
			TimeUnit.SECONDS.sleep(a);
			System.out.println(Thread.currentThread().getName()+" 2woke up");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

