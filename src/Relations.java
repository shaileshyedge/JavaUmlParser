
public class Relations
{
	private MainStruct source;
	private MainStruct destination;
	private RelationMaster rm;
	private String cardinality = "";
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public MainStruct getSource() 
	{
		return source;
	}
	public void setSource(MainStruct source) 
	{
		this.source = source;
	}
	public MainStruct getDestination() {
		return destination;
	}
	public void setDestination(MainStruct destination) {
		this.destination = destination;
	}
	public RelationMaster getRm() {
		return rm;
	}
	public void setRm(RelationMaster rm) {
		this.rm = rm;
	}
}
