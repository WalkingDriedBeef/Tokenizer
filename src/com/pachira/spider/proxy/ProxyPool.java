package com.pachira.spider.proxy;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * Pooled Proxy Object
 *
 * @author yxssfxwzy@sina.com <br>
 * @see Proxy
 * @since 0.5.1
 */
public class ProxyPool {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private BlockingQueue<Proxy> proxyQueue = new DelayQueue<Proxy>();
    private Map<String, Proxy> allProxy = new ConcurrentHashMap<String, Proxy>();
    private int reuseInterval = 1500;// ms
	private int reviveTime = 2 * 60 * 60 * 1000;// ms
	private boolean isEnable = false;

    public ProxyPool(List<String[]> httpProxyList, boolean isValidateWhenInit) {
    	addProxy(isValidateWhenInit, httpProxyList.toArray(new String[httpProxyList.size()][]));
    }

	public void addProxy(boolean validateWhenInit, String[]... httpProxyList) {
		isEnable = true;
		for (String[] s : httpProxyList) {
			try {
				if (allProxy.containsKey(s[0])) continue;
				HttpHost item = new HttpHost(InetAddress.getByName(s[0]), Integer.valueOf(s[1]));
				if (!validateWhenInit || ProxyUtils.validateProxy(item)) {
					Proxy p = new Proxy(item, reuseInterval);
					proxyQueue.add(p);
					allProxy.put(s[0], p);
				}
			} catch (UnknownHostException e) {
				logger.error("HttpHost init error:", e);
			}
		}
	}

    public synchronized HttpHost getProxy() {
        Proxy proxy = null;
        try{
        	Long time = System.currentTimeMillis();
        	logger.info("proxy pool size: [ " + proxyQueue.size() + "]");
//    		proxy = proxyQueue.take();
        	//choke this
    		proxy = proxyQueue.poll();
    		double costTime = (System.currentTimeMillis() - time) / 1000.0;
    		if (costTime > reuseInterval) {
    		    logger.info("get proxy time >>>> " + costTime);
    		}
    		proxy = allProxy.get(proxy.getHttpHost().getAddress().getHostAddress());
    		proxy.setLastBorrowTime(System.currentTimeMillis());
    		proxy.borrowNumIncrement(1);
        }catch (Exception e) {
        	logger.error("get proxy error, so use the local ip.");
        	return null;
		}
        return proxy.getHttpHost();
    }

    public void returnProxy(HttpHost host, int statusCode) {
    	if(host == null) return;
        Proxy p = allProxy.get(host.getAddress().getHostAddress());
        if (p == null) {
            return;
        }
        switch (statusCode) {
            case Proxy.SUCCESS:
                p.setReuseTimeInterval(reuseInterval);
                p.setFailedNum(0);
                p.setFailedErrorType(new ArrayList<Integer>());
                p.recordResponse();
                p.successNumIncrement(1);
                logger.info(p.toString());
                break;
            case Proxy.ERROR_403:
                // banned,try longer interval
                p.fail(Proxy.ERROR_403);
                p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
                logger.info(host + " >>>> reuseTimeInterval is >>>> " + p.getReuseTimeInterval() / 1000.0);
                break;
            case Proxy.ERROR_BANNED:
                p.fail(Proxy.ERROR_BANNED);
                p.setReuseTimeInterval(10 * 60 * 1000 * p.getFailedNum());
                logger.warn("this proxy is banned >>>> " + p.getHttpHost());
                logger.info(host + " >>>> reuseTimeInterval is >>>> " + p.getReuseTimeInterval() / 1000.0);
                break;
            case Proxy.ERROR_404:
                // p.fail(Proxy.ERROR_404);
                // p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
                break;
            default:
                p.fail(statusCode);
                break;
        }
        if (p.getFailedNum() > 20) {
            p.setReuseTimeInterval(reviveTime);
            logger.error("remove proxy >>>> " + host + ">>>>" + p.getFailedType() + " >>>> remain proxy >>>> " + proxyQueue.size());
            return;
        }
        if (p.getFailedNum() > 0 && p.getFailedNum() % 5 == 0) {
            if (!ProxyUtils.validateProxy(host)) {
                p.setReuseTimeInterval(reviveTime);
                logger.error("remove proxy >>>> " + host + ">>>>" + p.getFailedType() + " >>>> remain proxy >>>> " + proxyQueue.size());
                return;
            }
        }
        try {
            proxyQueue.put(p);
        } catch (InterruptedException e) {
            logger.warn("proxyQueue return proxy error", e);
        }
    }

    public int getIdleNum() {
        return proxyQueue.size();
    }

    public void enable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public boolean isEnable() {
        return isEnable;
    }
    public int getReuseInterval() {
		return reuseInterval;
	}

	public void setReuseInterval(int reuseInterval) {
		this.reuseInterval = reuseInterval;
	}
	
	public int getReviveTime() {
		return reviveTime;
	}

	public void setReviveTime(int reviveTime) {
		this.reviveTime = reviveTime;
	}

}
