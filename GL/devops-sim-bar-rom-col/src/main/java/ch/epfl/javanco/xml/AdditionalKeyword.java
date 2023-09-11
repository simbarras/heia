package ch.epfl.javanco.xml;

import java.io.Serializable;

class AdditionalKeyword implements Keyword, Serializable {

	private static final long serialVersionUID = 1L;
	private String keyword;
	private boolean isCore;

	public boolean isCore() {
		return isCore;
	}

	public boolean isMain() {
		return false;
	}

	public boolean isAdd() {
		return true;
	}

	@Override
	public int hashCode() {
		return keyword.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Keyword) {
			return ((Keyword)o).toString().equals(this.toString());
		}
		return super.equals(o);
	}

	@Override
	public String getString() {
		return keyword;
	}
	
	@Override
	public String toString() {
		return keyword;
	}	

	AdditionalKeyword(String s)  {
		if (s == null) {
			throw new NullPointerException();
		}
		keyword = s;
		for (XMLTagKeywords k : XMLTagKeywords.cores) {
			if (k.toString().equals(keyword)) {
				isCore = true;
				return;
			}
		}
		isCore = false;		
	}

	public static Keyword parseLong(String s) {
		try {
			return Enum.valueOf(XMLTagKeywords.class, s.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			return new AdditionalKeyword(s);
		}
	}

}

