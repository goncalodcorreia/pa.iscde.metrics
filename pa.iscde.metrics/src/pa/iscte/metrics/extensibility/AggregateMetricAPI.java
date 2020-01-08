package pa.iscte.metrics.extensibility;

import java.io.File;

import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;

public interface AggregateMetricAPI {
	
	
	public float getPackageMetric(PackageElement packageElement, MetricServiceAPI api);

	
}
