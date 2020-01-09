package pa.iscde.metrics.extensibility;

import pt.iscte.pidesco.projectbrowser.model.ClassElement;

public class MetricServiceImpl implements MetricServiceAPI{

	@Override
	public float getMetric(String metricName,ClassElement classElement) {
		return (float) Math.ceil(Math.random()*20);
	}


}
