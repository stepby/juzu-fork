package request.action;

import org.juzu.Action;

public class A {
	
	@Action
	public void noArg() {}
	
	@Action
	public void oneArg(String foo) {}
}