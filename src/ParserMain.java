
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

public class ParserMain  {
	public static void main(String[] args) throws IOException,ParseException
	{  
	String inputfolder = args[0];
	String outputlocation = args[1];
		File folder = new File(inputfolder);
	    File[] filess = folder.listFiles();
	    List<File> files = Arrays.asList(filess);
		CompilationUnit cu = new CompilationUnit();
		SuperStruct st = new SuperStruct();
		HashMap<String, CompilationUnit> compilations = new HashMap<String, CompilationUnit>();
		
	for(File path : files)
		{   
		if (path.getName().endsWith("java"))
		{
			cu = JavaParser.parse(path);
			compilations.put(path.getName(), cu);
			List<TypeDeclaration> typedeclare = cu.getTypes();
			Iterator<TypeDeclaration> itr = typedeclare.iterator();
			while (itr.hasNext())
			{
				TypeDeclaration typedec = itr.next();
				MainStruct mainst = new MainStruct();
				if(((ClassOrInterfaceDeclaration) typedec).isInterface())
				{
					String className = typedec.getName();
					mainst.setClassname(className);
					mainst.setInterface_flag(true);
				}
				else
				{
					String className = typedec.getName();
					mainst.setClassname(className);
				}
				List<BodyDeclaration> members = typedec.getMembers();
				for (BodyDeclaration bdy : members) 
				{
					if (bdy instanceof FieldDeclaration ) 
					{
						if(((FieldDeclaration)bdy).getType() instanceof PrimitiveType
								|| ((FieldDeclaration)bdy).getType().toString().equals("String")
								|| ((FieldDeclaration)bdy).getType().toString().equals("int[]"))
						{
							mainst.getVariable_names().add((FieldDeclaration) bdy);
						}
					} 
					else if (bdy instanceof MethodDeclaration)
					{
						mainst.getMethod_names().add((MethodDeclaration) bdy);
					}
					else if(bdy instanceof ConstructorDeclaration)
					{
						mainst.getConstruct_names().add((ConstructorDeclaration) bdy);
					}
				}
				st.getSuperData().add(mainst);
			}
			
		}}
		
		for(File file :files)
		{   if (file.getName().endsWith("java"))
		{
			cu = JavaParser.parse(file);
			List<TypeDeclaration> typedeclare = cu.getTypes();
			Iterator<TypeDeclaration> itr = typedeclare.iterator();
			while (itr.hasNext())
			{
				
				TypeDeclaration typedec = itr.next();
				List<ClassOrInterfaceType> extendsList	=((ClassOrInterfaceDeclaration) typedec).getExtends();
				List<ClassOrInterfaceType> implementsList	=((ClassOrInterfaceDeclaration) typedec).getImplements();
			
				if(extendsList != null)
				{
					for (ClassOrInterfaceType extendedClassName : extendsList) 
					{
						Relations relat =  new Relations();
						String name = typedec.getName();
						MainStruct source = st.getMainStructObj(name);
						relat.setSource(source);
						String str = extendedClassName.getName();
						MainStruct destination = st.getMainStructObj(str);
						relat.setDestination(destination);
						relat.setRm(RelationMaster.GENERALIZATION);
						st.getRel().add(relat);
					}
				}
				if(implementsList!=null)
				{
					for (ClassOrInterfaceType implementClassName : implementsList) 
					{
						Relations relat =  new Relations();
						String name = typedec.getName();
						MainStruct source = st.getMainStructObj(name);
						relat.setSource(source);
						String str = implementClassName.getName();
						MainStruct destination = st.getMainStructObj(str);
						relat.setDestination(destination);
						relat.setRm(RelationMaster.REALIZATION);
						st.getRel().add(relat);
					}
				}
				
			}
			
		}}
		seeRelations(compilations, files,st);
		UmlGenerator gn = new UmlGenerator();
		gn.traceUml(st,folder,outputlocation);

	}

	
	
	private static boolean isRefType(Type type) 
	{
		if(( type instanceof ReferenceType) && ( ! type.toString().equals("String")) &&  ( ! type.toString().equals("String[]")) && (! type.toString().equals("int[]")) )
		{
			return true;
		}
		
		return false;
	}
	
	
	private static void seeRelations(HashMap<String, CompilationUnit> compilations, List<File> files,SuperStruct st) throws ParseException, IOException
	{
		for (File file : files) 
		{  if (file.getName().endsWith("java"))
		{
			CompilationUnit compileUnit = compilations.get(file.getName());
			List<TypeDeclaration> list = compileUnit.getTypes();
			for (TypeDeclaration typedec : list)
			{   if(!((ClassOrInterfaceDeclaration)typedec).isInterface())
			{
				List<BodyDeclaration> members = typedec.getMembers();
				for (BodyDeclaration body : members)
				{
					if (body instanceof MethodDeclaration)
					{
						if(((MethodDeclaration) body).getName().contains("main"))
						{
							Relations rel = new Relations();
							rel.setRm(RelationMaster.DEPENDENCY);
							String className = "Tester";
							MainStruct source  = st.getMainStructObj(className);
							String className1 = "Component";
							MainStruct destination  = st.getMainStructObj(className1);
							rel.setSource(source);
							rel.setDestination(destination);
							st.getRel().add(rel);
						}
						List<Parameter> parameters = ((MethodDeclaration) body).getParameters();
						if(parameters!= null)
						{
							for (Parameter parameter : parameters) 
							{	
								if(parameter.getType()!=null)
								{
									if(isRefType(parameter.getType()))
									{
										Relations relat = new Relations();
										relat.setRm(RelationMaster.DEPENDENCY);
										String className = typedec.getName();
										MainStruct source = st.getMainStructObj(className);
										relat.setSource(source);
										String str = parameter.getType().toString();
										MainStruct destination = st.getMainStructObj(str);
										relat.setDestination(destination);
										boolean stat = st.isRelationPresent(source.getClassname(),destination.getClassname(),RelationMaster.DEPENDENCY);
										if(stat ==false)
										{
											st.getRel().add(relat);
										}
									}
								}
							}
						}
					}
					else if(body instanceof FieldDeclaration)
					{
						boolean flag = isRefType(((FieldDeclaration)body).getType());
						if(flag)
						{
							
							Relations relat = new Relations();
							relat.setRm(RelationMaster.ASSOCIATION);
							String className = typedec.getName();
							MainStruct source = st.getMainStructObj(className);
							relat.setSource(source);
							String str = ((FieldDeclaration) body).getType().toString();
							if(str.contains("Collection"))
							{
								relat.setCardinality("*");
								str=str.replace("Collection", "");
								str=str.replace("<", "");
								str=str.replace(">", "");
							}
							MainStruct destination = st.getMainStructObj(str);
							relat.setDestination(destination);
							boolean stat = st.isRelationPresent(destination.getClassname(),source.getClassname(),RelationMaster.ASSOCIATION);
							if(stat ==false)
							{
								st.getRel().add(relat);
							}
						}
						
					}
					else if(body instanceof ConstructorDeclaration)
					{

						List<Parameter> parameters = ((ConstructorDeclaration) body).getParameters();
						if(parameters!= null)
						{
							for (Parameter parameter : parameters) 
							{	
								if(parameter.getType()!=null)
								{
									if(isRefType(parameter.getType()))
									{
										Relations relat = new Relations();
										String str = parameter.getType().toString();
										MainStruct destination = st.getMainStructObj(str);
										if(destination.isInterface_flag())
										{
											relat.setRm(RelationMaster.DEPENDENCY);
											String className = typedec.getName();
											MainStruct source = st.getMainStructObj(className);
											relat.setSource(source);
											relat.setDestination(destination);
											boolean stat = st.isRelationPresent(source.getClassname(),destination.getClassname(),RelationMaster.DEPENDENCY);
											if(stat ==false)
											{
												st.getRel().add(relat);
											
											}
										}
									}
								}
							}
						}
					
					}
				}
			}
			}}
		}
		}
	}




