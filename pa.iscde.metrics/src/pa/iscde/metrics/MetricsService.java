package pa.iscde.metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.lang.model.type.DeclaredType;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;
import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement.Visitor;
import pt.iscte.pidesco.projectbrowser.model.SourceElement;

/* TODO
 * Questions :
 */

public class MetricsService {
	
	private final static MetricsService INSTANCE = new MetricsService();
	
	PackageElement root;
	JavaEditorServices javaServ;

	Map<SourceElement, Set<MetricModel>> metrics = new HashMap<SourceElement, Set<MetricModel>>(); //Table
	List<PackageElement> packages = new ArrayList<PackageElement>();

	//public MetricsService(PackageElement root, JavaEditorServices javaServ) {
		//this.root = root;
		//this.javaServ = javaServ;
	//}
	
	/*
	 * Method for resetting all entries in the metric data structure
	 */


	public void metricsReset() {
		for(SourceElement cmodel : metrics.keySet()) {
			for(MetricModel m : metrics.get(cmodel)) {
				if(m.getMetricName() != "Number of Lines")
					m.setMetricValue(0);
			}
		}
	}

	/*
	 * Calculation of all metrics based on a visitor pattern.
	 */

	public void traversePackagesAndClasses() {
		metricsReset();
		root.traverse(new Visitor() {
			@Override
			public boolean visitPackage(PackageElement packageElement) {
				// TODO Auto-generated method stub
				System.out.println("Traveling Package "  + packageElement.getName());
				packages.add(packageElement);
				return true;
			}


			@Override
			public void visitClass(ClassElement classElement) {

				System.out.println("Traveling Class "  + classElement.getName());

				try {
					MetricVisitor v = new MetricVisitor(classElement);
					File f = classElement.getFile();
					Scanner scan = new Scanner(f);
					int num_lines = 0;

					while(scan.hasNextLine()) {
						num_lines++;
						scan.nextLine();
					}

					javaServ.parseFile(f, v);

					MetricModel modelToAdd = new MetricModel("Number of Lines",num_lines,"elemental");
					if(!metrics.get(classElement).contains(modelToAdd)) {
						metrics.get(classElement).add(modelToAdd);
					}

					scan.close();

				}catch(Exception e) {
					e.printStackTrace();
				}
			}

		});

	}


	public Map<SourceElement, Set<MetricModel>> getMetricStructure() {
		return metrics;
	}
	
	
	public List<PackageElement >getPackages() {
		return packages;
	}

	/*
	 * Incrementation of a metric. Prepared to insert new metrics. Guava could help this assuming all metrics are countable.
	 */

	public void performMetricCalculations(ClassElement classElement, MetricModel m) {
		incrementMetric(classElement,m.getMetricName());
	}

	public void incrementMetric(ClassElement classElement,String metricName) {
		if(metrics.containsKey(classElement)){
			Set<MetricModel> metricsInClassElement = metrics.get(classElement);

			boolean incremented = false;
			for(MetricModel metric : metricsInClassElement) {
				if(metric.getMetricName().equals(metricName)) {
					metric.setMetricValue(metric.getMetricValue() + 1);
					incremented = true;
					break;
				}
			}

			if(!incremented) {
				metrics.get(classElement).add(new MetricModel(metricName,1,"elemental"));
			}
		}
		else {
			HashSet metricPlaceholder = new HashSet<MetricModel>();
			metricPlaceholder.add(new MetricModel(metricName,1,"elemental"));
			metrics.put(classElement, metricPlaceholder);
		}

	}


	/*
	 * Visitor class that will describe how classes are visited.
	 */

	class MetricVisitor extends ASTVisitor{

		ClassElement classElement;

		public MetricVisitor(ClassElement classElement) {
			this.classElement = classElement;
		}


		//Constructors and Regular Methods
		@Override
		public boolean visit(MethodDeclaration node) {
			String className = classElement.getName().split("\\.")[0];

			if(className.equals(node.getName().toString())) {
				incrementMetric(classElement, "Number of Constructors");
			}

			else {
				incrementMetric(classElement, "Number of Methods");
			}
			return super.visit(node);
		}

		//Interfaces and Classes
		@Override
		public boolean visit(TypeDeclaration node) {
			if(node.isInterface())
				incrementMetric(classElement,"Number of Interfaces");
			else
				incrementMetric(classElement,"Number of Classes");
			return true;
		}

		@Override
		public boolean visit(EnumDeclaration node) {
			incrementMetric(classElement,"Number of Enumerates");
			return true;
		}


		//Attributes
		public boolean visit(FieldDeclaration node) {
			incrementMetric(classElement,"Number of Attributes");

			if(!Modifier.isPublic(node.getModifiers())) {
				incrementMetric(classElement,"Number of Static Attributes");
			}

			if(!Modifier.isFinal(node.getModifiers())) {
				incrementMetric(classElement,"Number of Final Attributes");
			}


			return false;
		}



	}
	
	public static MetricsService getService() {
		return INSTANCE;
	}

	public void setupServices(PackageElement rootPackage, JavaEditorServices javaServ) {
		this.root = rootPackage;
		this.javaServ = javaServ;	
	}


}



