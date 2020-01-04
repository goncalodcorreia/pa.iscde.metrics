package pa.iscde.metrics;

import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;

public interface BasicMetricAPI {
	
	public float getClassMetric(ClassElement classElement);
	
	public float getPackageMetric(PackageElement packageElement);

}
