package util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import data.Link;

public class BrowserHelper{
	private Shell shell;
	private Browser browser;
	private Display display;
	
	private Link[] links;
	private Webpage page;
	private int index = 0;
	private BrowserHelperListener listener;
	private static Pattern p = Pattern.compile("(http:|https:)(?!.*((.doc)|(.jpg)|(.pdf)|(.zip)|(.xls)|(.msg)|(.ppt))).*", Pattern.CASE_INSENSITIVE);
	
	public BrowserHelper(Link[] links)
	{
		this.links = links;
	}
	
	public void run() 
	{
		display = new Display();	
		shell = new Shell(display); 
		browser = new Browser(shell, SWT.NONE);
		browser.addProgressListener(
		new ProgressListener()
		{
			@Override
			public void changed(ProgressEvent event) {
			}

			@Override
			public void completed(ProgressEvent event) {
				String html = browser.getText();
				if(!html.isEmpty())
				{
					page = new Webpage(browser.getUrl(), html);
					notifyListener();
				}
				setUrl();
			}
		});
		
		setUrl();
		
		long time = System.currentTimeMillis();
		long timeout = 30000 * links.length;
		
		while (!shell.isDisposed()) {
			if((System.currentTimeMillis() - time) > timeout)
			{
				shell.dispose();
			}
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	private void setUrl()
	{
		String url = nextUrl();
		if(url != null)
		{
			if(p.matcher(url).matches())
				browser.setUrl(url);
		}
		else
		{
			shell.dispose();
		}
	}

	private boolean hasNextUrl()
	{
		if(index < links.length)
			return true;
		return false;
	}
	
	private String nextUrl()
	{
		if(hasNextUrl())
		{
			return links[index++].getUrl();
		}
		
		return null;
	}
	
	private void notifyListener() {
		listener.callback(page);
	}
	
	public void addBrowserHelperListener(BrowserHelperListener listener)
	{
		this.listener= listener; 
	}
	
	public void removeBrowserHelperListener(BrowserHelperListener listener)
	{
		this.listener= null; 
	}
}

/*public class BrowserHelper{
	
	private Shell shell;
	private Browser browser;
	private CountDownLatch latch;
	private Display display;
	
	private Link[] links;
	private Webpage page;
	private int index = 0;
	private BrowserHelperListener listener;
	private long timeout = 30000;
	private static Pattern p = Pattern.compile("(http:|https:)(?!.*((.doc)|(.jpg)|(.pdf)|(.zip)|(.xls)|(.msg)|(.ppt))).*", Pattern.CASE_INSENSITIVE);
	
	public BrowserHelper(Link[] links)
	{
		this.links = links;
		final CountDownLatch initLatch = new CountDownLatch(1);
		new Thread(){
			public void run()
			{
				display = new Display();	
				shell = new Shell(display); 
				browser = new Browser(shell, SWT.NONE);
				browser.addProgressListener(
				new ProgressListener()
				{
					@Override
					public void changed(ProgressEvent event) {
					}
	
					@Override
					public void completed(ProgressEvent event) {
						String html = browser.getText();
						if(!html.isEmpty())
						{
							page = new Webpage(browser.getUrl(), html);
							notifyListener();
						}
						latch.countDown();
					}
				});
				initLatch.countDown();
				
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				display.dispose();
			}
		}.start();	
		try {    
            initLatch.await();  
		} catch (InterruptedException e) {  
            Thread.interrupted();  
		}
	}
	
	public void setUrl(final String url)
	{
		latch = new CountDownLatch(1);  
        display.syncExec(new Runnable() {  
                public void run() {  
                        browser.setUrl(url);  
                }  
        });  
        waitLoad();  
	}

	private void waitLoad() {
		try {
			boolean isTimeout;  
			isTimeout = !latch.await(timeout,TimeUnit.MILLISECONDS);
		 
			if (isTimeout) {  
                display.syncExec(new Runnable() {  
                        public void run() {  
                                browser.stop();  
                        }  
                });  
                latch.await(timeout,TimeUnit.MILLISECONDS);  
        	}
        } catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	public void run() 
	{
		try{
			while(hasNextUrl())
			{
				final String url = nextUrl();
				if(url != null && p.matcher(url).matches())
				{
					setUrl(url);
				}
			}
			shell.dispose();
		}catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch(SWTException  e)
		{
			e.printStackTrace();
		}
	}

	private boolean hasNextUrl()
	{
		if(index < links.length)
			return true;
		return false;
	}
	
	private String nextUrl()
	{
		if(hasNextUrl())
		{
			return links[index++].getUrl();
		}
		
		return null;
	}
	
	private void notifyListener() {
		listener.callback(page);
	}
	
	public void addBrowserHelperListener(BrowserHelperListener listener)
	{
		this.listener= listener; 
	}
	
	public void removeBrowserHelperListener(BrowserHelperListener listener)
	{
		this.listener= null; 
	}
}*/



