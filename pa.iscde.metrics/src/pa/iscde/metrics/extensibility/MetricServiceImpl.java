package pa.iscde.metrics.extensibility;

import java.util.Set;

import pa.iscde.metrics.MetricModel;
import pa.iscde.metrics.MetricsService;
import pt.iscte.pidesco.projectbrowser.model.ClassElement;

public class MetricServiceImpl implements MetricServiceAPI{

	MetricsService metricService;

	public MetricServiceImpl() {
		metricService = MetricsService.getService();
	}

	@Override
	public float getMetric(String metricName,ClassElement classElement) {
		Set<MetricModel> model = metricService.getMetricStructure().get(classElement);
		System.out.println(model);
		if(model != null) {
			System.out.println(model);
			for(MetricModel mo : model) {
				if(mo.getMetricName().equals(metricName)) {
					return (float) mo.getMetricValue();
				}

			}


		}
		return 0;

	}
}
