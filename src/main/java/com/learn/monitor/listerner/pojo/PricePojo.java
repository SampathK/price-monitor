package com.learn.monitor.listerner.pojo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

import com.learn.monitor.listener.util.ConstantManager;

public class PricePojo implements Delayed {

	private final String symbol;
	private final double price;
	private final long origin;

	public PricePojo(final String symbol,final double price,final long origin) {
		this.symbol = symbol;
		this.price = price;
		this.origin = origin;
	}

	public String getSymbol() {
		return symbol;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public long getDelay( TimeUnit unit ) {
		return unit.convert((MILLISECONDS.convert(ConstantManager.PERIOD_IN_SECS, SECONDS)) - ( System.currentTimeMillis() - origin ), 
				MILLISECONDS );
	}

	@Override
	public int compareTo( Delayed delayed ) {
		if( delayed == this ) {
			return 0;
		}
		long d = ( getDelay( MILLISECONDS ) - delayed.getDelay( MILLISECONDS ) );
		return ( ( d == 0 ) ? 0 : ( ( d < 0 ) ? -1 : 1 ) );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PricePojo other = (PricePojo) obj;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}



}
