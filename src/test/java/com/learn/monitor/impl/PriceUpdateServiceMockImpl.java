package com.learn.monitor.impl;

import java.util.HashSet;
import java.util.Set;

import com.learn.monitor.PriceListener;
import com.learn.monitor.PriceUpdateService;

public class PriceUpdateServiceMockImpl implements PriceUpdateService {

	private final Set<PriceListener> bankPriceListeners = new HashSet<>();
	
	private final Set<PriceListener> companyPriceListeners = new HashSet<>();
	
	@Override
	public void subscribeToBankPriceUpdates(PriceListener priceListener) {
		bankPriceListeners.add(priceListener);

	}

	@Override
	public void subscribeToCompanyPriceUpdates(PriceListener priceListener) {
		companyPriceListeners.add(priceListener);

	}
	
	
	public boolean containsCompanyListener(PriceListener priceListener) {
		return (companyPriceListeners.contains(priceListener));
	}
	
	public boolean containsBankListener(PriceListener priceListener) {
		return (bankPriceListeners.contains(priceListener));
	}
	
	
	public void reset() {
		bankPriceListeners.clear();
		companyPriceListeners.clear();
	}

}
