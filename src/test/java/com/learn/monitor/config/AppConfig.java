package com.learn.monitor.config;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.learn.monitor.impl.AlertServiceMockImpl;
import com.learn.monitor.impl.PriceUpdateServiceMockImpl;
import com.learn.monitor.listener.util.LockUtil;
import com.learn.monitor.listerner.impl.BankPriceListener;
import com.learn.monitor.listerner.impl.PricePublisherListener;
import com.learn.monitor.listerner.pojo.PricePojo;


@Configuration
public class AppConfig {

	private AlertServiceMockImpl alertServiceMockImpl;
	private PriceUpdateServiceMockImpl priceUpdateServiceMockImpl;
	private LockUtil lockUtil;
	private PricePublisherListener pricePublisherListener;
	private BankPriceListener bankPriceListener;

	@Bean()
	public AlertServiceMockImpl alertServiceMockImpl() {
		if(null == alertServiceMockImpl){
			alertServiceMockImpl = new AlertServiceMockImpl();
		}
		return alertServiceMockImpl;
	}

	@Bean()
	public PriceUpdateServiceMockImpl priceUpdateServiceMockImpl() {
		if(null == priceUpdateServiceMockImpl) {
			priceUpdateServiceMockImpl = new PriceUpdateServiceMockImpl();
		}
		return priceUpdateServiceMockImpl;
	}

	@Bean()
	public LockUtil lockUtil() {
		if(null == lockUtil){
			lockUtil = new LockUtil(new ConcurrentHashMap<String,String>());
		}
		return lockUtil;
	}

	@Bean()
	public PricePublisherListener pricePublisherListener () {
		if(null == pricePublisherListener) {
		 pricePublisherListener = new PricePublisherListener(new HashMap<String,Double>(),priceUpdateServiceMockImpl(),lockUtil(),alertServiceMockImpl());
		}
		return pricePublisherListener;
	}
    
	@Bean()
	public BankPriceListener bankPriceListener () {
		if(null == bankPriceListener) {
	  	bankPriceListener = new BankPriceListener(new HashMap<String,PricePojo>(),priceUpdateServiceMockImpl(),pricePublisherListener(),lockUtil(),alertServiceMockImpl());
		}
		return bankPriceListener;
	}   
}
