package eschool.sites.model;

import java.util.Map;
import java.util.LinkedHashMap;

import javax.jdo.annotations.*;

@PersistenceCapable
public class Page {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
	private long id;
	private Site parentSite;
	private Page parentPage;
	private String ident;
	private String name;
	private String content;
	@Join
	@Key(types=java.lang.String.class)
	@Value(types=eschool.sites.model.Page.class)
	private Map<String, Page> children = new LinkedHashMap<String, Page>();
	
	public Page() {}
	
	public Page(String name) {
		this.name = name;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setChildren(Map<String, Page> identsAndPages) {
		for (String ident : identsAndPages.keySet()) {
			Page p = identsAndPages.get(ident);
			p.setIdent(ident);
			p.setParentPage(this);
		}
		this.children = identsAndPages;
	}
	
	public Map<String, Page> getChildren() {
		if (this.children == null) {
			return new LinkedHashMap<String, Page>();
		} else {
			return this.children;
		}
	}

	public Site getParentSite() {
		return parentSite;
	}

	public void setParentSite(Site parentSite) {
		this.parentSite = parentSite;
	}
	
	public Page getParentPage() {
		return parentPage;
	}

	public void setParentPage(Page parentPage) {
		this.parentPage = parentPage;
	}
	
	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}
}
