package pa.iscde.metrics.extensibility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pa.iscde.metrics.MetricModel;
import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;
import pt.iscte.pidesco.projectbrowser.model.SourceElement;

public interface BasicMetricAPI {

	public Set<MetricModel> getClassMetric(SourceElement classElement);
	
	public Set<MetricModel> getPackageMetric(PackageElement packageElement);

}
