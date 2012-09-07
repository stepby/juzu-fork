package controller_resolver;

import org.juzu.Render;

public class A {
	
	@Render
	public void noArg() { }
	
	@Render
	public void fooArg(String foo) { }
}