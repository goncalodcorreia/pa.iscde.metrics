package pa.iscde.metrics;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.type.DeclaredType;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;
import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement.Visitor;




/* TODO
 * Number of Constructors - Done 
 * Number of Interfaces - Done 
 * Number of Methods - Done 
 * Number of Attributes
 * Number of Classes - Done 
 * Highest number of Parameters in a Method (?)
 * Total Lines of Code
 */


public class MetricsService {

	PackageElement root;
	JavaEditorServices javaServ;

	ArrayList<MetricModel> metrics;


	public MetricsService(PackageElement root, JavaEditorServices javaServ) {
		this.root = root;
		this.javaServ = javaServ;
		fillMetricList();
	}

	public void fillMetricList() {
		metrics = new ArrayList<MetricModel>();
		for(MetricEnum metricenum : MetricEnum.values()) {
			metrics.add(new MetricModel(metricenum.getDesignation(),0));
		}
	}
	
	
	public void metricsReset() {
		for(MetricModel m : metrics) {
			m.setMetricValue(0);
		}
	}

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


	public ArrayList<MetricModel> getMetricsList() {
		return metrics;
	}
	
	

	public void incrementMetric(MetricEnum m) {
		boolean incremented = false;
		for(MetricModel metric : metrics) {
			if(metric.getMetricName().equals(m.getDesignation())) {
				metric.setMetricValue(metric.getMetricValue() + 1);
				incremented = true;
				break;
			}
		}
		
		if(!incremented) {
			metrics.add(new MetricModel(m.getDesignation(),1));
		}
	}
	
	

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
				incrementMetric(MetricEnum.NUM_CONSTRUCTORS);
			}

			else {
				incrementMetric(MetricEnum.NUM_METHODS);
			}
			return super.visit(node);
		}
		
		//Interfaces and Classes (Upgrade to detect enum?)
		@Override
		public boolean visit(TypeDeclaration node) {
			if(node.isInterface())
				incrementMetric(MetricEnum.NUM_INTERFACES);
			else
				incrementMetric(MetricEnum.NUM_CLASSES);
			return true;
		}
		
		//Fields - Under development
		
		public boolean visit(FieldDeclaration node) {
			//Everything here is a field
			incrementMetric(MetricEnum.NUM_ATTRIBUTES);
			//System.out.println(node.getModifiers());
			//System.out.println(node.fragments().toString());
			
			if(!Modifier.isPublic(node.getModifiers())) {
				incrementMetric(MetricEnum.NUM_STATIC_ATTRIBUTES);
			}
			
			if(!Modifier.isFinal(node.getModifiers())) {
				incrementMetric(MetricEnum.NUM_FINAL_ATTRIBUTES);
			}
			
			
			
			return false;
		}
		
	}


}



