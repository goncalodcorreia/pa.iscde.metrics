package pa.iscde.metrics;

public class MetricModel {

	private String metricName;
	private int metricValue;
	private String metricType;

	public MetricModel(String metricName, int metricValue, String metricType) {
		this.metricName = metricName;
		this.metricValue = metricValue;
		this.metricType = metricType; 
	}



	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public int getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(int metricValue) {
		this.metricValue = metricValue;
	}

	@Override
	public boolean equals(Object obj) {
		if(((MetricModel)obj).metricName.equals(this.metricName) && ((MetricModel)obj).metricValue ==(this.metricValue))
			return true;
		return super.equals(obj);
	}
	
	
	@Override
	public int hashCode() {
	    return (int) 237 * metricName.hashCode();
	}
	
	

	public String getMetricName() {
		return metricName;
	}
	
	public String getMetricType() {
		return metricName;
		
	}
	


}
