package nifi.worklow.param;


public enum Status {
	
	RUNNING("Running"),
	STOPPED("Stopped");
	
	private String status;
	
	private Status(String status) {
		this.status = status;
	}
	
	public String getValue(){
		return status;
	}
	
	@Override
	public String toString(){
		return status;
	}
	
}
