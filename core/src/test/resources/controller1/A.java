package controller1;

import org.juzu.Render;
import org.juzu.Binding;

public class A {
	
	@Render
	public void noArg() {}
	
	@Render
	public void oneArg(String foo) {}
	
	@Render(parameters = { @Binding(name="foo", value="foo_value") })
	public void binding() {}
	
	@Render(parameters = {@Binding(name="foo", value="foo_value")})
	public void bindingOneArg(String bar) {}
}