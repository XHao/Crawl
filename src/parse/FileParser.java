package parse;


import index.SolrService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawl.CrawlTask;

import data.Link;
import data.SolrData;

public class FileParser implements ParseTask {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileParser.class);
	private CrawlTask node = null;
	private UrlFilter filter;

	@Override
	public void setCrawlNode(CrawlTask node) {
		this.node = node;
	}

	@Override
	public void parse(Link[] links) {
		if(node == null)
		{
			if(LOG.isErrorEnabled())
				LOG.error("Parser work without crawlnode, links update will fail, so program skip parse.");
		}
		else
		{
			/*final ArrayList<String> ids = new ArrayList<String>();
			final ArrayList<SolrData> solrDataList = new ArrayList<SolrData>(); 
			*/
			final ArrayList<Link> indexFiles = new ArrayList<Link>();
			final ArrayList<Link> indexFolders = new ArrayList<Link>();
			ArrayList<Link> outlinks = new ArrayList<Link>();
			filter = new FileFilter();
			for(Link link : links)
			{
				if(filter.isValidate(link.getUrl()))
				{
					/*SolrData tempdata = TikaExtractor.extract(link);
					if(tempdata == null)
					{
						tempdata = new SolrData();
						tempdata.setUrl(link.getUrl());
						tempdata.setTitle(link.getTitle());
						tempdata.setLocation(link.getLocation());
					}
					solrDataList.add(tempdata);
					ids.add(link.getUrl());*/
					indexFiles.add(link);
				}
				else
				{
					File file = new File(link.getPath());
					if(file.exists() && file.isDirectory() && file.list() != null)
					{
						indexFolders.add(link);
						for(String sub : file.list())
						{
							Link outlink = new Link(link.getUrl() + "/" + sub);
							outlinks.add(outlink);				
						}
					}
				}
			}
			node.dispatchResultLinks(outlinks.toArray(new Link[0]));
			
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					SolrService service = new SolrService();
					service.addFiles(indexFiles.toArray(new Link[0]));
					service.addFolders(indexFolders.toArray(new Link[0]));
				}
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*private static class TikaExtractor implements Extractor
	{
		public static SolrData extract(Link link)
		{
			try {
				Tika tika = new Tika();
				//disable the maximum length of strings returned by the parseToString methods
				tika.setMaxStringLength(-1);
				Metadata meta = new Metadata();
				SolrData data = new SolrData();
				File file = new File(link.getPath());
				InputStream in = new FileInputStream(file);
				String content = tika.parseToString(in, meta);
				data.setContent(content);
				data.setUrl(link.getUrl());
				data.setAuthor(meta.get(meta.CREATOR));
				data.setContent_type(meta.get(meta.CONTENT_TYPE));
				data.setLast_modified(meta.get(meta.LAST_MODIFIED));
				data.setTitle(link.getTitle());
				data.setLocation(link.getLocation());
				return data;				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;			
		}
	}*/
}
