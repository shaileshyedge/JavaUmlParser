import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;


public class UmlGenerator {
		
	public void traceUml(SuperStruct cu, File folder,String outlocation)
	{
		
		String output = "@startuml\n";
		List<MainStruct> main = cu.getSuperData();
		Iterator<MainStruct> iterator = main.iterator();
		while(iterator.hasNext())
		{
			MainStruct mn = iterator.next();
			List<MethodDeclaration> methodlist = mn.getMethod_names();
			if(mn.isInterface_flag())
			{
				output = output + "interface " + mn.getClassname()+ " << interface >> {\n";
			}
			else
			{
				output = output + "class " + mn.getClassname() + " {\n";
			}
			List<FieldDeclaration> varlist = mn.getVariable_names();
			//List<MethodDeclaration> methlist = setterGetter(methodlist,varlist);
			List<MethodDeclaration> methlist = new ArrayList<MethodDeclaration>();
			for (MethodDeclaration methname : methodlist)
			{
				for(FieldDeclaration vari : varlist)
				{
					String vari1 = "get" + vari.getVariables().get(0).toString();
					String vari2 = "set" + vari.getVariables().get(0).toString();
					if( (methname.getName().equalsIgnoreCase(vari2) || methname.getName().equalsIgnoreCase(vari1)))
					{
						methlist.add(methname);
						vari.setModifiers(1);
					}
				}
			}
			for (MethodDeclaration methodDeclaration : methlist) {
				methodlist.remove(methodDeclaration);
			}
			for (MethodDeclaration vari : methodlist)
			{
				int intMod = vari.getModifiers();
				String mod = Modifier.toString(intMod);
				String type = vari.getType().toString();
				String name = vari.getName().toString();
				if(mod.equals("public"))
				{   List<Parameter> paramet =vari.getParameters();
				    if (paramet != null)
				    {
				    
				    	for (Parameter param : paramet)
						{
							if(param.getType()!=null)
							{
								output = output + "+" + name +  "("  + param.getId().toString() + " : " + param.getType()  + ") :"+type+"\n";
							}
						}
				    }
				    else
				    {
				    	output = output +  "+" + name +" (): " + type + "\n";	
				    }
					
				}
				
				else if((intMod !=1) && (intMod != 1025) && (intMod == 9))
				{
					List<Parameter> parameters=vari.getParameters();
					if(parameters != null )
					{
						for (Parameter param : parameters)
						{
							if(param.getType()!=null)
							{
								output = output + "{static}+" + name +  "("  +
										param.getId().toString() + " : " + param.getType()  + "):"+type+"\n";;
							}
						}
					}
					
				}
				else if(intMod == 1025)
				{
					output = output + "+" + name +" (): "+ type +"\n";
				}
				
			}
			
			for (FieldDeclaration vari : varlist)
			{
				int intModifier = vari.getModifiers();
				String modifier = Modifier.toString(intModifier);
				String x = vari.getVariables().toString();
				String y = vari.getType().toString();
				x = x.replace("[","");
				x = x.replace("]","");
				if(modifier.equals("public"))
				{
					output = output + "+" + x + " : " + y +"\n";
				}
				else if(modifier.equals("private"))
				{
					output = output + "-" + x + " : "+ y +"\n";
				}	
				
			}
			
			output = constructorMeth(output,mn);			
			output +="}\n";
		}
		
		Iterator<Relations> itr = cu.getRel().iterator();
		while(itr.hasNext())
		{
			Relations relat = itr.next();
			output = generateRelations(output, cu, relat);
		}
		output = output + "@enduml";		
		System.out.println(output);
		makeUmlDiag(output,folder,outlocation);
	}

	
	
	
	
	private String constructorMeth(String out,MainStruct unit)
	{
		List<ConstructorDeclaration>construct = unit.getConstruct_names();
		for (ConstructorDeclaration constructor : construct) 
		{
			List<Parameter>parameters = constructor.getParameters();
			if(parameters!= null)
			{	
				for (Parameter parameter : parameters) 
				{
					if(parameter.getType()!=null)
					{
						out = out + "+" + constructor.getName()+"("+
								parameter.getId().toString()+" : "+parameter.getType()+")\n";
					}
				}
			}
			else
			{
				out = out +  "+" + constructor.getName()+"()\n";

			}
		}
		return out;
	}
	
	
	
	public String generateRelations(String out, SuperStruct cu,Relations relat)
	{
		if(relat.getRm() != null)
		{
			if(relat.getRm().toString().equals("GENERALIZATION"))
			{
				out += relat.getDestination().getClassname()+" <|-- "+relat.getSource().getClassname()+ "\n";
			}
			else if(relat.getRm().toString().equals("REALIZATION"))
			{
				out += relat.getDestination().getClassname()+" <|.. "+relat.getSource().getClassname()+ "\n";
			}
			else if(relat.getRm().toString().equals("ASSOCIATION"))
			{
				if ((relat.getCardinality() !=null) && (relat.getCardinality() != ""))
					 
				{   
					System.out.println(relat.getCardinality());
					out += relat.getDestination().getClassname()+" \""+relat.getCardinality()+"\""+" -- "+"\"1\" "+relat.getSource().getClassname()+"\n";
				}
				else
				{
					out += relat.getDestination().getClassname()+" -- "+relat.getSource().getClassname()+"\n";
				}
			}
			else if(relat.getRm().toString().equals("DEPENDENCY"))
			{
				if(relat.getCardinality() !=null)
				{
					out = out + relat.getSource().getClassname() +" ..> "+relat.getDestination().getClassname()+"\n";
				}
				else
				{
					out = out + relat.getSource().getClassname() +" ..> "+relat.getDestination().getClassname()+"\n";

				}
			}
		}
		return out;
	}
	
	

	private void makeUmlDiag(String out, File folderdata,String outlocation) 
	{
		SourceStringReader ssr =  new SourceStringReader(out);
		try
		{
			FileOutputStream fs = new FileOutputStream(outlocation + ".png");
			ssr.generateImage(fs,  new FileFormatOption(FileFormat.PNG, false));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
	
}
