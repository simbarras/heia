package ch.epfl.javanco.xml;

enum Type {
	CORE, MAIN, ADD;
}

//public class XMLTagKeywords {
public enum XMLTagKeywords implements Keyword {

	// MAIN_DESCRIPTION ELEMENT NAMES
	NETWORK (Type.CORE),
	MAIN_DESCRIPTION (Type.CORE),
	LAYER (Type.CORE),
	NODE (Type.CORE),
	LINK (Type.CORE),

	// MAIN_DESCRIPTION MANDATORY ATTRIBUTES
	ORIG (Type.CORE),
	DEST (Type.CORE),
	ID (Type.CORE),
	POS_X (Type.MAIN),
	POS_Y (Type.MAIN),
	LABEL (Type.MAIN),
	LENGTH (Type.MAIN),
	LIGHT_PATHS (Type.MAIN),

	NODE_COLOR ( Type.ADD),
	NODE_SIZE (Type.ADD),
	NODE_ICON (Type.ADD),
	NODE_SEE_ID (Type.ADD),
	LINK_COLOR (Type.ADD),
	LINK_COLOR2 (Type.ADD),
	LINK_SPEED (Type.ADD),
	LINK_WIDTH (Type.ADD),
	LINK_DASH (Type.ADD),
	LINK_CURVE_START (Type.ADD),
	LINK_CURVE_END   (Type.ADD),
	LINK_CURVE_START_ANGLE (Type.ADD),
	LINK_CURVE_END_ANGLE   (Type.ADD),
	LABEL_FONT_SIZE (Type.ADD),
	POS_Z (Type.ADD),
	LINK_ROUTING (Type.ADD),
	LABEL_COLOR (Type.ADD),

	// ADDITIONAL_DESCRIPTION ELEMENT NAMES
	ADDITIONAL_DESCRIPTION (Type.CORE),
	// 	LAYER ("layer"),
	NODES (Type.CORE),
	LINKS (Type.CORE),
	LAYERS (Type.CORE),
	PROPERTIES (Type.MAIN), //LOANA before : PROPERTY (Type.CORE),

	// ADDITIONAL_DESCRIPTION ATTRIBUTES
	CLASS (Type.MAIN),
	NAME (Type.MAIN),
	ON_LAYER (Type.MAIN),
	GENERAL (Type.MAIN),
	DEFAULT_NODE_CLASS (Type.ADD),
	DEFAULT_LINK_CLASS (Type.ADD),
	DEFAULT_NODE_COLOR (Type.ADD),
	DEFAULT_NODE_ICON (Type.ADD),
	DEFAULT_LINK_COLOR (Type.ADD),
	DEFAULT_LINK_WIDTH (Type.ADD),
	DEFAULT_LABEL_COLOR (Type.ADD),
	OFFSET (Type.ADD),
	USE_OFFSET (Type.ADD),
	DIRECTED (Type.ADD),
	GRAPH_NAME (Type.CORE),
	MAIN_LAYER_NAME (Type.MAIN), 
	NODE_LABEL_FONT_SIZE (Type.MAIN), 
//	NODE_VISIBLE_ID (Type.MAIN),
	;


	private final Type type;
	private final String toString;

	XMLTagKeywords(Type key2) {
		type = key2;
		this.toString = name().toLowerCase();
	}
	
	public static XMLTagKeywords[] cores = new XMLTagKeywords[]{
		ORIG,
		DEST,
		ID,
		LAYER,
		NODE,
		LINK,
		GRAPH_NAME,
		NODES,
		LINKS,
		LAYERS};

	@Override
	public String getString() {
		return toString;
	}
	
	@Override
	public String toString() {
		return toString;
	}	

	public boolean isCore() {
		return (type.equals(Type.CORE));
	}

	public boolean isMain() {
		return (type.equals(Type.MAIN));
	}

	public boolean isAdd() {
		return (type.equals(Type.ADD));
	}
	
	public static Keyword parse(String s) {
		/*	try {
			return Enum.valueOf(XMLTagKeywords.class, s.toUpperCase());
		}
		catch (IllegalArgumentException e) {*/
		return new AdditionalKeyword(s);
		//	}
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

