import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;

public class SuperStruct
{
	private List<MainStruct> superData = new ArrayList<MainStruct>();
	private List<Relations>  rel = new ArrayList<Relations>();

	public List<Relations> getRel()
	{
		return rel;
	}

	public void setRel(List<Relations> rel)
	{
		this.rel = rel;
	}

	public List<MainStruct> getSuperData() {
		return superData;
	}

	public void setSuperData(List<MainStruct> superData)
	{
		this.superData = superData;
	}
	
	public MainStruct getMainStructObj(String c )
	{
	
		for (MainStruct mainobj : superData) 
		{
			if ( mainobj.getClassname().equals(c))
			{
				return mainobj;
			}
		}
		return null;
	}
	
	public boolean isRelationPresent(String source,String destination,RelationMaster rem)
	{
		for (Relations relations : rel)
		{
			if((relations.getSource().getClassname().equals(source)) && (relations.getDestination().getClassname().equals(destination) && (relations.getRm().equals(rem))))
					{
				return true;
					}
		}
		return false;
	}
}
