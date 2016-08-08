package com.learn.monitor.listerner.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.learn.monitor.config.AppConfig;
import com.learn.monitor.impl.AlertServiceMockImpl;
import com.learn.monitor.impl.PriceUpdateServiceMockImpl;
import com.learn.monitor.listener.util.ConstantManager;
import com.learn.monitor.listerner.pojo.PricePojo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class BankPriceListenerTest implements ConstantManager {


	@Autowired
	private BankPriceListener bankPriceListener;

	@Autowired
	private PriceUpdateServiceMockImpl priceUpdateServiceMockImpl;
	
	@Autowired
	private PricePublisherListener pricePublisherListener;
	
	@Autowired
	private AlertServiceMockImpl alertServiceMockImpl;

	@Test
	public void testNotNullPricePublisherListener() {
		assertNotNull(bankPriceListener);
	}

	@Test
	public void testNotRegisterPricePublisherListener() {
		assertTrue(false == priceUpdateServiceMockImpl.containsCompanyListener(bankPriceListener));
	}

	@Test
	public void testRegisterPricePublisherListener() {
		assertTrue(true == priceUpdateServiceMockImpl.containsBankListener(bankPriceListener));
	}

	@Test
	public void testValuePricePublisherListener() {
		final String symbol[] = {"aaa","bbb","ccc"};
		final double price[] = {1.0,2.0,3.0};
		for(int i=0;i< symbol.length;i++){
			bankPriceListener.priceUpdate(symbol[i], price[i]);
		}
		Map<String,PricePojo> bankMapValue = bankPriceListener.getBankpriceMap();
		assertTrue(bankMapValue.size() == symbol.length);
		for(int i = 0;i<symbol.length;i++){
			assertNotNull(bankMapValue.get(symbol[i]));
			assertTrue(bankMapValue.get(symbol[i]).getPrice()==price[i]);
		}

	}
	
	@Test
	public void testCompareBankCompanyPrice() {
		final String symbol[] = {"aaa","bbb","ccc"};
		final double price[] = {1.0,2.0,3.0};
		for(int i=0;i< symbol.length;i++){
			bankPriceListener.priceUpdate(symbol[i], price[i]);
			pricePublisherListener.priceUpdate(symbol[i], price[i]);
		}
		DelayQueue<PricePojo> delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
		assertTrue(null != delayQueue && symbol.length == delayQueue.size());
		System.out.print("Waiting ...");
		try{
			TimeUnit.SECONDS.sleep(PERIOD_IN_SECS *3);
		}
		catch(InterruptedException ie) {
			System.err.println("Thread interrupted !!");
		}
		delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
		assertTrue(null != delayQueue && 0 == delayQueue.size());
		assertTrue(null == alertServiceMockImpl.getAlertMsg());
		String symbol1[] = {"eee","fff","ggg"};
		double price1[] = {1.0,2.0,3.0};
		for(int i=0;i< symbol1.length;i++){
			bankPriceListener.priceUpdate(symbol1[i], price1[i]);
		}
		delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
		assertTrue(null != delayQueue && symbol1.length == delayQueue.size());
		System.out.print(".... ...");
		try{
			TimeUnit.SECONDS.sleep(PERIOD_IN_SECS *3);
		}
		catch(InterruptedException ie) {
			System.err.println("Thread interrupted !!");
		}
		delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
		assertTrue(null != delayQueue && 0 == delayQueue.size());
		assertTrue(null != alertServiceMockImpl.getAlertMsg() && INCORRECT_VALUE.equals(alertServiceMockImpl.getAlertMsg()));
		alertServiceMockImpl.setAlertMsg(null);
		assertTrue(null == alertServiceMockImpl.getAlertMsg());
		String symbol2[] = {"eee","fff","ggg"};
		double price2[] = {2.0,3.0,4.0};
		for(int i=0;i< symbol2.length;i++){
			bankPriceListener.priceUpdate(symbol2[i], price2[i]);
		}
		try{
			TimeUnit.SECONDS.sleep(PERIOD_IN_SECS);
		}
		catch(InterruptedException ie) {
			System.err.println("Thread interrupted !!");
		}
		String symbol3[] = {"eee","fff","ggg"};
		double price3[] = {4.0,6.0,6.0};
		for(int i=0;i< symbol3.length;i++){
			bankPriceListener.priceUpdate(symbol3[i], price3[i]);
		}
		delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
		while(delayQueue.size() == (symbol2.length + symbol3.length)) {
			try {
				System.out.print(".... ...");
				Thread.sleep(1000);
				delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println("Thread interrupted !!");
			}
		}
		assertTrue(null != delayQueue && symbol3.length == delayQueue.size());
		assertTrue(null == alertServiceMockImpl.getAlertMsg());
		delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
		while(delayQueue.size() != 0) {
			try {
				System.out.print(".... ...");
				Thread.sleep(1000);
				delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println("Thread interrupted !!");
			}
		}
		assertTrue(null != delayQueue && 0 == delayQueue.size());
		assertTrue(null != alertServiceMockImpl.getAlertMsg() && INCORRECT_VALUE.equals(alertServiceMockImpl.getAlertMsg()));
		alertServiceMockImpl.setAlertMsg(null);
		assertTrue(null == alertServiceMockImpl.getAlertMsg());
		String symbol4[] = {"eee","fff","ggg"};
		double price4[] = {4.0,6.0,6.0};
		for(int i=0;i< symbol4.length;i++){
			bankPriceListener.priceUpdate(symbol4[i], price4[i]);
		}
		double price5[] = {5.0,7.0,8.0};
		for(int i=0;i< symbol4.length;i++){
			pricePublisherListener.priceUpdate(symbol4[i], price5[i]);
		}
		delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
		while(delayQueue.size() != 0) {
			try {
				System.out.print(".... ...");
				Thread.sleep(1000);
				delayQueue = bankPriceListener.getDelayPriceMonitorQueue();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println("Thread interrupted !!");
			}
		}
		assertTrue(null != delayQueue && 0 == delayQueue.size());
		assertTrue(null != alertServiceMockImpl.getAlertMsg() && INCORRECT_VALUE.equals(alertServiceMockImpl.getAlertMsg()));

	}

}
