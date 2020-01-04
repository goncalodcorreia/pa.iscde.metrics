package pa.iscde.metrics;

import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;

public interface MetricServiceAPI {


	public float getMetric(String metricName, ClassElement classElement);
	
	
}
