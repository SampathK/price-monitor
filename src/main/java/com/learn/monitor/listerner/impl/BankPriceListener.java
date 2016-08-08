package com.learn.monitor.listerner.impl;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.learn.monitor.AlertService;
import com.learn.monitor.PriceListener;
import com.learn.monitor.PriceUpdateService;
import com.learn.monitor.listener.util.ConstantManager;
import com.learn.monitor.listener.util.LockUtil;
import com.learn.monitor.listerner.PriceProducer;
import com.learn.monitor.listerner.pojo.PricePojo;

public class BankPriceListener implements PriceListener,Runnable,ConstantManager {

	
	private final PriceUpdateService priceUpdateService;
	private final PriceProducer priceProducer;
	private final LockUtil lockUtil;
	private final AlertService alertService;

	private final Map<String,PricePojo> bankpriceMap;

	private final DelayQueue<PricePojo> delayPriceMonitorQueue = new DelayQueue<>();;


	public BankPriceListener(final Map<String,PricePojo> bankPriceMap,
			final PriceUpdateService priceUpdateService,final PriceProducer priceProducer,final LockUtil lockUtil,final AlertService alertService) {
		this.bankpriceMap = bankPriceMap;
		this.alertService = alertService;
		this.priceUpdateService = priceUpdateService;
		this.priceProducer = priceProducer;
		this.lockUtil = lockUtil;
		registerThis();
		initScheduler();
	}


	private void initScheduler() {
		final ScheduledExecutorService scheduler =
				Executors.newScheduledThreadPool(MONITOR_THREAD_POOL_SIZE);
		scheduler.scheduleAtFixedRate(this, PERIOD_IN_SECS, PERIOD_IN_SECS, SECONDS);

	}


	private void registerThis() {
		priceUpdateService.subscribeToBankPriceUpdates(this);

	}


	public void priceUpdate(final String symbol, final double price) {
		final String lock = lockUtil.getLock(symbol);
		final PricePojo pricePojo = new PricePojo(symbol,price, System.currentTimeMillis());
		synchronized (lock) {
			bankpriceMap.put(symbol, pricePojo);	
		}
		delayPriceMonitorQueue.put(pricePojo);
	}


	public Map<String, PricePojo> getBankpriceMap() {
		return new HashMap<>(bankpriceMap);
	}


	@Override
	public void run() {
		final List<PricePojo> pricePojos = new ArrayList<>();
		final int drainedEl= delayPriceMonitorQueue.drainTo(pricePojos);
		for(int i=0; i < drainedEl; i++) {
			PricePojo pricePojo = pricePojos.get(i);
			final String symbol = pricePojo.getSymbol();
			final String lock = lockUtil.getLock(symbol);
			PricePojo curPricePojo = null;
			Double producerPrice = null;
			synchronized (lock) {
				curPricePojo = bankpriceMap.get(symbol);
				producerPrice = priceProducer.getPrice(symbol);

			}
			if(curPricePojo.equals(pricePojo)){
				double curPrice = pricePojo.getPrice();
				if(null == producerPrice || 0 != Double.compare(producerPrice, curPrice)){
					alertService.alert(INCORRECT_VALUE);
				}
			}
		}
	}


	public DelayQueue<PricePojo> getDelayPriceMonitorQueue() {
		return new DelayQueue<>(delayPriceMonitorQueue);
	}
	
	
	


}
