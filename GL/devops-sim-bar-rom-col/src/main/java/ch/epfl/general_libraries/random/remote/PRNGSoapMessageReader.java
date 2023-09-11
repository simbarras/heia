package ch.epfl.general_libraries.random.remote;


public interface  PRNGSoapMessageReader  {

	public void tellVal(long l);

	public void tellState(int column, int l);

	public void tellStateSize(int[] sizes);

	public void tellPrngType(String s);

	public void tellSamples(int i);

	public void tellSeed(int i);

	public void tellNextColumn();

}
