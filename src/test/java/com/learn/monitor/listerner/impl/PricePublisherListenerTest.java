package com.learn.monitor.listerner.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class PricePublisherListenerTest implements ConstantManager {


	@Autowired
	private PricePublisherListener pricePublisherListener;

	@Autowired
	private PriceUpdateServiceMockImpl priceUpdateServiceMockImpl;
	
	@Autowired
	private AlertServiceMockImpl alertServiceMockImpl;

	@Test
	public void testNotNullPricePublisherListener() {
		assertNotNull(pricePublisherListener);
	}

	@Test
	public void testRegisterPricePublisherListener() {
		assertTrue(priceUpdateServiceMockImpl.containsCompanyListener(pricePublisherListener));
	}

	@Test
	public void testNotRegisterPricePublisherListener() {
		assertTrue(false == priceUpdateServiceMockImpl.containsBankListener(pricePublisherListener));
	}
	
	@Test
	public void testValuePricePublisherListener() {
	   	final String symbol[] = {"aaa","bbb","ccc"};
	   	final double price[] = {1.0,2.0,3.0};
	   	for(int i=0;i< symbol.length;i++){
	   		pricePublisherListener.priceUpdate(symbol[i], price[i]);
	   	}
	   	Map<String,Double> companyMapValue = pricePublisherListener.companypriceMap();
	   	assertTrue(companyMapValue.size() == symbol.length);
	   	for(int i = 0;i<symbol.length;i++){
	   		assertNotNull(companyMapValue.get(symbol[i]));
	   		assertTrue(companyMapValue.get(symbol[i]).equals(price[i]));
	   	}
	
	}
	
	@Test
	public void testDuplicateValue() {
		final String symbol[] = {"aaa","aaa","aaa","ddd","eee"};
	   	final double price[] = {1.0,1.0,1.0,1.0,2.0};
	    pricePublisherListener.priceUpdate(symbol[0], price[0]);
	   	assertTrue(alertServiceMockImpl.getAlertMsg() == null);
	    pricePublisherListener.priceUpdate(symbol[1], price[1]);
	   	assertTrue(alertServiceMockImpl.getAlertMsg().equals(DUPLICATE_VALUE));
	   	alertServiceMockImpl.setAlertMsg(null);
	   	assertTrue(alertServiceMockImpl.getAlertMsg() == null);
	    pricePublisherListener.priceUpdate(symbol[2], price[2]);
	   	assertTrue(alertServiceMockImpl.getAlertMsg().equals(DUPLICATE_VALUE));
	   	alertServiceMockImpl.setAlertMsg(null);
	   	assertTrue(alertServiceMockImpl.getAlertMsg() == null);
	    pricePublisherListener.priceUpdate(symbol[3], price[3]);
	   	assertTrue(alertServiceMockImpl.getAlertMsg() == null);
	    pricePublisherListener.priceUpdate(symbol[4], price[4]);
	   	assertTrue(alertServiceMockImpl.getAlertMsg() == null);

	   	
	}

}
