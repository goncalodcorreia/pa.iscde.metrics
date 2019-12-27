package pa.iscde.metrics;

public class MetricModel {

	private String metricName;
	private int metricValue;
	
	public MetricModel(String metricName, int metricValue) {
		this.metricName = metricName;
		this.metricValue = metricValue;
	}

	public String getMetricName() {
		return metricName;
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
		if(((MetricModel)obj).metricName.equals(this.metricName))
			return true;
		return super.equals(obj);
	}
	
	
}
