package com.distocraft.dc5000.etl.xml3GPP32435DYN;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynamicParserCache {

	public static final String PROP_FILE_PATH = "/eniq/sw/conf/";
	public static final String COUNTER_LIST_FILE = "3GPPDYN_CounterList.properties";
	public static final String COUNTER_MAPPING_FILE = "3gppdynmdcmapping.properties";

	
	private static TreeSet<String> counterList;
	private static Map<String, String> counterMap;

	private static volatile DynamicParserCache instance;

	private static final Object INSTANCE_LOCK = new Object();
	private static final Lock CACHE_LOCK = new ReentrantLock();
	private static final Condition CACHE_INITIALIZED = CACHE_LOCK.newCondition();

	private static volatile boolean isCacheInitialized = false;
	private static Logger log;
	
	public static final long WAIT_TIMEOUT = 60L;

	private DynamicParserCache() {

	}

	public TreeSet<String> getCounterList() throws InterruptedException {
		if (!isCacheInitialized) {
			try {
				CACHE_LOCK.lock();
				CACHE_INITIALIZED.await(WAIT_TIMEOUT, TimeUnit.MINUTES);
			} finally {
				CACHE_LOCK.unlock();
			}
		}
		return new TreeSet<>(counterList);
	}

	public Map<String, String> getCounterMap() throws InterruptedException {
		if (!isCacheInitialized) {
			try {
				CACHE_LOCK.lock();
				CACHE_INITIALIZED.await(WAIT_TIMEOUT, TimeUnit.MINUTES);
			} finally {
				CACHE_LOCK.unlock();
			}
		}
		return new HashMap<>(counterMap);
	}

	public static DynamicParserCache getInstance(Logger log) throws FileNotFoundException, IOException {
		DynamicParserCache tempCache = instance;
		if (tempCache == null) {
			synchronized (INSTANCE_LOCK) {
				if (tempCache == null) {
					instance = new DynamicParserCache();
					DynamicParserCache.log = log;
					instance.loadCounterList();
					instance.loadCounterMap();
					isCacheInitialized = true;
					log.log(Level.INFO, "DynamicParserCache Initialized");
					try {
						CACHE_LOCK.lock();
						CACHE_INITIALIZED.signalAll();
					} finally {
						CACHE_LOCK.unlock();
					}
					tempCache = instance;
				}
			}
		}
		return tempCache;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadCounterMap() throws FileNotFoundException, IOException {
		try (InputStream fis = new FileInputStream(PROP_FILE_PATH + COUNTER_MAPPING_FILE)) {
			Properties prop = new Properties();
			prop.load(fis);
			counterMap = new HashMap<>((Map)prop);
			log.log(Level.INFO, "DynamicParserCache Loaded counter Map : " + counterMap);
		}
	}

	private void loadCounterList() throws FileNotFoundException, IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(PROP_FILE_PATH + COUNTER_LIST_FILE))) {
			String counter = null;
			counterList = new TreeSet<>((arg0, arg1) -> {
				if (arg0.length() < arg1.length()) {
					return 1;
				}
				return -1;
			});
			while ((counter = reader.readLine()) != null) {
				counterList.add(counter);
			}
			log.log(Level.INFO, "DynamicParserCache Loaded counter list : " + counterList);
		}
	}

}
