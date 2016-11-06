import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;

public class MainStruct
{
  private String classname;
  private List<FieldDeclaration> variable_names = new ArrayList<FieldDeclaration>(0);
  private List<MethodDeclaration> method_names = new ArrayList<MethodDeclaration>(0);
  private List<ConstructorDeclaration> construct_names = new ArrayList<ConstructorDeclaration>(0);
  private boolean interface_flag;
public boolean isInterface_flag() {
	return interface_flag;
}
public void setInterface_flag(boolean interface_flag) {
	this.interface_flag = interface_flag;
}
public String getClassname() {
	return classname;
}
public void setClassname(String classname) {
	this.classname = classname;
}
public List<FieldDeclaration> getVariable_names() {
	return variable_names;
}
public void setVariable_names(List<FieldDeclaration> variable_names) {
	this.variable_names = variable_names;
}
public List<MethodDeclaration> getMethod_names() {
	return method_names;
}
public void setMethod_names(List<MethodDeclaration> method_names) {
	this.method_names = method_names;
}
public List<ConstructorDeclaration> getConstruct_names() {
	return construct_names;
}
public void setConstruct_names(List<ConstructorDeclaration> construct_names) {
	this.construct_names = construct_names;
}
  


}
