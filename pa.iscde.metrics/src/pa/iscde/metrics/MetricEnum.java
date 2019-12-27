package pa.iscde.metrics;

public enum MetricEnum {
	NUM_CONSTRUCTORS("Number of Constructors"),
	NUM_INTERFACES("Number of Interface"),
	NUM_METHODS("Number of Methods"),
	NUM_ATTRIBUTES("Number of Attributes"),
	NUM_CLASSES("Number of Classes"),
	NUM_FINAL_ATTRIBUTES("Number of Final Attributes"),
	NUM_STATIC_ATTRIBUTES("Number of Static Attributes"),
	NUM_ENUM("Number of Enums");
	
	private String designation;
	
	
	private MetricEnum(String designation) {
		this.designation = designation;
		
	}
	
	public String getDesignation(){
		return designation;
	}

}
