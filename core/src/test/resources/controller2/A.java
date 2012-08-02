package controller2;

import org.juzu.Action;
import org.juzu.Binding;

public class A {
	
	@Action
	public void noArg() {}
	
	@Action
	public void oneArg(String foo) {}
	
	@Action(parameters = { @Binding(name="foo", value="foo_value") })
	public void binding() {}
	
	@Action(parameters = {@Binding(name="foo", value="foo_value")})
	public void bindingOneArg(String bar) {}
}