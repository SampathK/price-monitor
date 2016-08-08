package com.learn.monitor.listerner.impl;

import java.util.HashMap;
import java.util.Map;

import com.learn.monitor.AlertService;
import com.learn.monitor.PriceListener;
import com.learn.monitor.PriceUpdateService;
import com.learn.monitor.listener.util.ConstantManager;
import com.learn.monitor.listener.util.LockUtil;
import com.learn.monitor.listerner.PriceProducer;

public class PricePublisherListener implements PriceListener,ConstantManager,PriceProducer {

	private final PriceUpdateService priceUpdateService;
	private final AlertService alertService;
	private final LockUtil lockUtil;
	private final Map<String,Double> companypriceMap;

	public PricePublisherListener(final Map<String, Double> companyPriceMap
			,final PriceUpdateService priceUpdateService,final LockUtil lockUtil,
			final AlertService alertService) {
		this.companypriceMap = companyPriceMap;
		this.priceUpdateService = priceUpdateService;
		this.alertService = alertService;
		this.lockUtil = lockUtil;
		registerThis();
	}


	private void registerThis() {
		priceUpdateService.subscribeToCompanyPriceUpdates(this);

	}

	@Override
	public void priceUpdate(final String symbol, final double price) {
		final String lock = lockUtil.getLock(symbol); 
		Double prevPrice = null;
		synchronized (lock) {
			prevPrice = companypriceMap.put(symbol, price);
		}
		if(null != prevPrice && true == prevPrice.equals(price)) {
			alertService.alert(DUPLICATE_VALUE);
		}

	}


	public Map<String, Double> companypriceMap() {
		return new HashMap<>(companypriceMap);
	}


	@Override
	public Double getPrice(String symbol) {
		// TODO Auto-generated method stub
		return companypriceMap.get(symbol);
	}
	
	

}
