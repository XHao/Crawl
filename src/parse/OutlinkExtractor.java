package parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.Link;

public abstract class OutlinkExtractor {
	
	private static final Logger LOG = LoggerFactory.getLogger(OutlinkExtractor.class);
	
	 /**
	  * Regex pattern to get URLs within a plain text.
	  * 
	  * @see <a
	  *      href="http://www.truerwords.net/articles/ut/urlactivation.html">
	  *      http://www.truerwords.net/articles/ut/urlactivation.html
	  *      </a>
	  */
	  private static final String URL_PATTERN = 
	    "([A-Za-z][A-Za-z0-9+.-]{1,120}:[A-Za-z0-9/](([A-Za-z0-9$_.+!*,;/?:@&~=-])|%[A-Fa-f0-9]{2}){1,333}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*,;/?:@&~=%-]{0,1000}))?)";
	  
	  /**
	   * Extracts <code>Outlink</code> from given plain text and adds anchor
	   * to the extracted <code>Outlink</code>s
	   * 
	   * @param plainText the plain text from wich URLs should be extracted.
	   * 
	   * @return Array of Outlinks within found in plainText
	   */
	  
	  public static Link[] getOutlinks(final String plainText) {
		    long start = System.currentTimeMillis();
		    final List<Link> outlinks = new ArrayList<Link>();

		    try {
		      final Pattern pattern = Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		      final Matcher matcher = pattern.matcher(plainText);

		      MatchResult result;
		      String url;

		      while (matcher.find()) {
		        // if this is taking too long, stop matching
		        if (System.currentTimeMillis() - start >= 60000L) {
		          if (LOG.isWarnEnabled()) {
		            LOG.warn("Time limit exceeded for getOutLinks");
		          }
		          break;
		        }
		        result = matcher.toMatchResult();
		        url = result.group(0);
		        outlinks.add(new Link(url));
		      }
		    } catch (Exception ex) {
		      // if the matcher fails (perhaps a malformed URL) we just log it and move on
		      if (LOG.isErrorEnabled()) { LOG.error("getOutlinks", ex); }
		    }

		    final Link[] retval;

		    //create array of the Outlinks
		    if (outlinks != null && outlinks.size() > 0) {
		      retval = outlinks.toArray(new Link[0]);
		    } else {
		      retval = new Link[0];
		    }

		    return retval;
		  }
}
