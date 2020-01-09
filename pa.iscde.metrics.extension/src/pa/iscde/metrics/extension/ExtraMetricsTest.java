package pa.iscde.metrics.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pa.iscde.metrics.MetricModel;
import pa.iscde.metrics.extensibility.AggregateMetricAPI;
import pa.iscde.metrics.extensibility.BasicMetricAPI;
import pa.iscde.metrics.extensibility.MetricServiceAPI;
import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;
import pt.iscte.pidesco.projectbrowser.model.SourceElement;



public class ExtraMetricsTest implements BasicMetricAPI, AggregateMetricAPI{

	@Override
	public Set<MetricModel> getClassMetric(ClassElement classElement) {
		Set<MetricModel> result = new HashSet<MetricModel>();
		result.add(new MetricModel("TMetric1",20,"elemental"));
		result.add(new MetricModel("TMetric2",22,"elemental"));
		return result;
	}

	@Override
	public Set<MetricModel> getPackageMetric(PackageElement packageElement) {
		return null;
	}

	@Override
	public float getAggregatePackageMetric(PackageElement packageElement, MetricServiceAPI api) {
		float totalLines = 0;
		for(SourceElement source : packageElement.getChildren()) {
			if(source.isClass()) {
				totalLines += api.getMetric("Number of Lines", (ClassElement) source); 
			}
		}
		return totalLines;
	}





}
