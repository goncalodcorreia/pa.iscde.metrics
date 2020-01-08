package pa.iscte.metrics.extensibility;

import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;

public interface MetricServiceAPI {


	public float getMetric(String metricName, ClassElement classElement);
	
	
}
