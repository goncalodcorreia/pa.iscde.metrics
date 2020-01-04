package pa.iscde.metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

/* TODO
 * Questions :
 */

public class MetricsService {

	PackageElement root;
	JavaEditorServices javaServ;

	HashMap<ClassElement, HashSet<MetricModel>> metrics = new HashMap<ClassElement, HashSet<MetricModel>>(); //Table

	public MetricsService(PackageElement root, JavaEditorServices javaServ) {
		this.root = root;
		this.javaServ = javaServ;
		//fillMetricList();
	}


	/**
	 * Metrics list initialization
	 */



	//	public void fillMetricList() {
	//		metrics = new HashMap<,MetricModel>();
	//		for(MetricEnum metricenum : MetricEnum.values()) {
	//			metrics.add(new MetricModel(metricenum.getDesignation(),0));
	//		}
	//	}

	/*
	 * Clears the metric values.
	 */

	public void metricsReset() {
		for(ClassElement cmodel : metrics.keySet()) {
			for(MetricModel m : metrics.get(cmodel)) {
				m.setMetricValue(0);
			}
		}
	}

	/*
	 * Calculation of all metrics based on a visitor pattern.
	 */

	public void CalculateMetrics() {
		metricsReset();
		root.traverse(new Visitor() {
			@Override
			public boolean visitPackage(PackageElement packageElement) {
				// TODO Auto-generated method stub
				System.out.println("Traveling Package "  + packageElement.getName());
				return true;
			}


			@Override
			public void visitClass(ClassElement classElement) {
				System.out.println("Traveling Class "  + classElement.getName());

				try {
					MetricVisitor v = new MetricVisitor(classElement);
					File f = classElement.getFile();
					System.out.println("Parsing");
					javaServ.parseFile(f, v);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

		});

	}



	public HashMap<ClassElement, HashSet<MetricModel>> getMetricsList() {
		return metrics;
	}

	/*
	 * Incrementation of a metric. Prepared to insert new metrics. Guava could help this assuming all metrics are countable.
	 */

	public void incrementMetric(ClassElement classElement,MetricEnum m) {
		System.out.println(classElement);
		if(metrics.containsKey(classElement)){
			HashSet<MetricModel> metricsInClassElement = metrics.get(classElement);

			boolean incremented = false;
			for(MetricModel metric : metricsInClassElement) {
				if(metric.getMetricName().equals(m.getDesignation())) {
					metric.setMetricValue(metric.getMetricValue() + 1);
					incremented = true;
					break;
				}
			}

			if(!incremented) {
				metrics.get(classElement).add(new MetricModel(m.getDesignation(),1));
			}
		}
		else {
			HashSet metricPlaceholder = new HashSet<MetricModel>();
			metricPlaceholder.add(new MetricModel(m.getDesignation(),1));
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
				incrementMetric(classElement, MetricEnum.NUM_CONSTRUCTORS);
			}

			else {
				incrementMetric(classElement,MetricEnum.NUM_METHODS);
			}
			return super.visit(node);
		}

		//Interfaces and Classes
		@Override
		public boolean visit(TypeDeclaration node) {
			if(node.isInterface())
				incrementMetric(classElement,MetricEnum.NUM_INTERFACES);
			else
				incrementMetric(classElement,MetricEnum.NUM_CLASSES);
			return true;
		}

		@Override
		public boolean visit(EnumDeclaration node) {
			incrementMetric(classElement,MetricEnum.NUM_ENUM);
			return true;
		}


		//Attributes
		public boolean visit(FieldDeclaration node) {
			incrementMetric(classElement,MetricEnum.NUM_ATTRIBUTES);

			if(!Modifier.isPublic(node.getModifiers())) {
				incrementMetric(classElement,MetricEnum.NUM_STATIC_ATTRIBUTES);
			}

			if(!Modifier.isFinal(node.getModifiers())) {
				incrementMetric(classElement,MetricEnum.NUM_FINAL_ATTRIBUTES);
			}

			return false;
		}

	}


}



