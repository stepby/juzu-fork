package request.render;

import org.juzu.Render;

public class A {
	
	@Render
	public void noArg() {}
	
	@Render
	public void oneArg(String foo) {}
}