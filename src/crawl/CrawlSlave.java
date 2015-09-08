package crawl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import data.Link;
import data.LinkConstant;
import parse.BrowserParser;
import parse.FileParser;
import parse.ParseTask;

public class CrawlSlave implements CrawlTask{

	private ParseTask parse;
	private CrawlTaskConf conf;

	@Override
	public void setConf(CrawlTaskConf conf) {
		this.conf = conf;
	}
	
	@Override
	public void run() {
		parse();
		index();
	}
	
	private void parse()
	{
		String val = conf.getProtocol();
		if(val.equals(LinkConstant.PROTOCOL_HTTP))
		{
			parse = new BrowserParser();
		}
		else if(val.equals(LinkConstant.PROTOCOL_FILE))
		{
			parse = new FileParser();
		} else
		{
			parse = null;
		}
		
		if(parse != null)
		{
			parse.setCrawlNode(this);
			parse.parse(conf.getLinks());
		}
	}
	
	private void index()
	{
		
	}
	
	@Override
	public void dispatchResultLinks(Link[] links){
		HashMap<String, HashSet<Link>> map = new HashMap<String, HashSet<Link>>(2);
		for(Link link : links){
			String p = link.getProtocol();
			if(map.containsKey(p)){
				map.get(p).add(link);
			} else{
				HashSet<Link> ls = new HashSet<Link>();
				ls.add(link);
				map.put(p, ls);
			}
		}
		for(Entry<String, HashSet<Link>> entry : map.entrySet())
		{
			String protocol = entry.getKey();
			boolean flag = protocol.equals(LinkConstant.PROTOCOL_FILE) ? false : true;
			Link[] out = entry.getValue().toArray(new Link[0]);
			CrawlResult ret = new CrawlResult(out, protocol, flag);
			conf.getCrawl().accept(ret);
		}
		//one parse task over
		if(conf.getProtocol().equals(LinkConstant.PROTOCOL_FILE)){
			conf.getCrawl().reduceTask();
		}
	}

	/*
	 * now can't figure out if the browser can do the right parsing, only check the file parse end.
	 * @see crawl.CrawlTask#end()
	 */
	@Override
	public void end() {
		
	}
}
