package crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.Link;
import data.LinkConstant;
import index.SolrService;

/**
 * It will start as a master, and can create many CrawlSlave, the slave do the CrawlTask. 
 * {@link #dispatch(CrawlTask, CrawlTaskConf)} and {@link #accept(CrawlResult)} connect the master and slaves.
 * @author i072208 shako
 *
 */
public class CrawlImpl implements Crawl{

	private static final Logger LOG = LoggerFactory.getLogger(CrawlImpl.class);
	
	private boolean init;
	private CrawlConfig config;
	private CrawlStatus status;
	/* decide to replace the pool with CrawlSlaveHeap */
	private ExecutorService pool;
	private LinkSort linkSort;
	
	public CrawlImpl(){
		init = false;
	}
		
	/******************** public methods ********************/
	
	/**
	 * @see Crawl#setConfig(CrawlConfig)
	 */
	@Override
	public void setConfig(CrawlConfig crawlConfig) {
		if(init == false){
			LOG.info("This crawl begins to initialize config.");
			this.config = crawlConfig;
			init = true;
		} else{
			LOG.info("This crawl tries to init more than once.");
			return;
		}
	}
	
	/**
	 * @see Crawl#dispatch(CrawlTask, CrawlTaskConf)
	 */
	@Override
	public void dispatch(CrawlTask task, CrawlTaskConf conf) {
		addTask();
		task.setConf(conf);
		pool.submit(task);
	}

	/**
	 * thread safe method
	 * @see Crawl#accept(CrawlResult)
	 */
	@Override
	public void accept(CrawlResult result) {
		Link[] links = null;
		
		synchronized(this){
			links = linkSort.addResult(result);
		}
		
		if(links == null)
			return;
		
		CrawlSlave task = new CrawlSlave();
		CrawlTaskConf conf = new CrawlTaskConf();
		conf.setProtocol(result.getProtocol());
		conf.setCrawl(this);
		conf.setLinks(links);
		dispatch(task, conf);
	}


	public void addTask(){
	}
	
	public void reduceTask(){
	}
	
	@Override
	public void run() {
		if(init == false){
			LOG.warn("Crawl need to set config.");
			System.exit(-1);
		}
		LOG.info("Crawl start.");
		status = CrawlStatus.NEW;
		linkSort = new LinkSort();
		pool = Executors.newFixedThreadPool(config.getThreads());
		boolean guard = true;
		while(guard)
		{
			switch(status)
			{
				case NEW:{
					Link[] root_links = readSeeds(config.getSeedPath());
					for(Link link : root_links){
						String p = link.getProtocol();
						if(p.equals(LinkConstant.PROTOCOL_FILE)){
							new Thread(new FileNotify(link.getPath())).start();
						}
						linkSort.addLink(link);
						CrawlSlave task = new CrawlSlave();
						CrawlTaskConf conf = new CrawlTaskConf();
						conf.setProtocol(p);
						conf.setCrawl(this);
						conf.setLinks(new Link[]{link});
						dispatch(task, conf);
					}
					status = CrawlStatus.HOLD;
				}					
					break;
				case UPDATE:{
					linkSort.clear();
					status = CrawlStatus.NEW;
				}
					break;
				case CLEAN:{
					clean();
					status = CrawlStatus.CLOSE;
				}
					break;
				case HOLD:{
					boolean flag = true;
					while(flag)
					{
						
					}
				}
					break;
				case CLOSE:{
					close();
				}
					break;
			}
		}
	}
	
	/**
	 * @see Crawl#close()
	 */
	@Override
	public void close() {
		pool.shutdown();
		LOG.info("Crawl stop working.");
		System.exit(0);
	}
	
	@Override
	public CrawlStatus getStatus() {		
		return status;
	}
	
	/**
	 * the main function
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("log4j.configuration", "log4j.properties");	
		CrawlConfig crawlConfig = new CrawlConfig();
		crawlConfig.init();
		
		if(!crawlConfig.isInitSucceed())
		{
			LOG.warn("Crawl init config failed, please check the \"config.xml\".");
			System.exit(-1);
		}
		else
		{
			CrawlImpl c = new CrawlImpl();
			c.setConfig(crawlConfig);
			c.run();
		}
	}

	/******************** private methods ********************/
	
	/**
	 * 
	 * @param seedPath
	 * @return
	 */
	private Link[] readSeeds(String seedPath) {
		File file = new File(seedPath);
		BufferedReader reader = null;
		HashSet<Link> seeds = new HashSet<Link>();
        try {
			reader = new BufferedReader(new FileReader(file));
			String temp;
			while((temp = reader.readLine()) != null)
			{
				Link link = new Link(temp.trim());
				seeds.add(link);
			}
		} catch (FileNotFoundException e) {
			LOG.warn("Seed file doesn't exist, please check the \"config.xml\".");
		} catch (IOException e) {
			LOG.warn(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage());
				}
			}
		}
        return seeds.toArray(new Link[0]);
	}
	
	private void clean() {
		linkSort.clear();
		SolrService service = new SolrService();
		service.deleteAll();
	}
	
	/*private void writeNewResults(String dir) {
		if(dir == null)
			return;
		try {
			File file = new File(dir);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(file, linkSort);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/*private void readLastResault(String dir) {
		if(dir == null)
			return;
		File file = new File(dir);
		ObjectMapper objectMapper = new ObjectMapper();
		if(file.exists() && file.isFile())
		{
			try {
				Link[] temp = objectMapper.readValue(file, Link[].class);
				file.delete();
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	class LinkSort{
		
		private HashSet<Link> links;
		
		public LinkSort(){
			links = new HashSet<Link>(16,(float) 0.95);
		}
		
		private void clear() {
			links.clear();
		}

		private void addLink(Link link) {
			links.add(link);
		}

		private Link[] addResult(CrawlResult result) {
			List<Link> in = Arrays.asList(result.getLinks());
			
			if(result.needResolveDuplicate() == false){
				this.links.addAll(in);
			} else {
				for(Link link : in){
					if(!this.links.add(link)){
						in.remove(link);
					}
				}
			}
			return in.toArray(new Link[0]);	
		}
	}
	
	class FileNotify implements Runnable{

		private WatchService watcher;
		private String path;
		
		FileNotify(String path){
			this.path = path;
		}
		
		@Override
		public void run() {
			try {
				Path dir = Paths.get(path);
				watcher = FileSystems.getDefault().newWatchService();
				dir.register(watcher, 
					StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_DELETE, 
					StandardWatchEventKinds.ENTRY_MODIFY);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {  
                WatchKey key;  
                try {  
                    key = watcher.take();  
                    for (WatchEvent<?> event : key.pollEvents()) {  
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) {  
                            continue;  
                        } else{
                        	// TODO 
                        }
                    }                
                    key.reset(); 
                } catch (InterruptedException e) {  
                     
                }  
            }  
		}
		
	}
}
