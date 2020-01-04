package pa.iscde.metrics.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import pa.iscde.metrics.MetricModel;
import pa.iscde.metrics.MetricsAction;
import pa.iscde.metrics.MetricsService;
//import pt.iscte.pidesco.demo.extensibility.DemoAction;
import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;
import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement.Visitor;
import pt.iscte.pidesco.projectbrowser.model.SourceElement;
import pt.iscte.pidesco.projectbrowser.service.ProjectBrowserListener;
import pt.iscte.pidesco.projectbrowser.service.ProjectBrowserServices;

public class MetricsView implements PidescoView{

	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		viewArea.setLayout(new RowLayout(SWT.VERTICAL));
		BundleContext context = Activator.getContext();

		//Calling the necessary Services

		ServiceReference<ProjectBrowserServices> serviceReference =
				context.getServiceReference(ProjectBrowserServices.class);

		ServiceReference<JavaEditorServices> serviceReference2 = 
				context.getServiceReference(JavaEditorServices.class);


		ProjectBrowserServices projServ = context.getService(serviceReference);

		JavaEditorServices javaServ = context.getService(serviceReference2);

		MetricsService metricsService = new MetricsService(projServ.getRootPackage(), javaServ);



		//Articulate Some logic

		Button metricLoaderBtn = new Button(viewArea, SWT.PUSH);

		final Table table = new Table(viewArea,SWT.RESIZE);
		table.setVisible(false);


		final Table extraTable = new Table(viewArea,SWT.None);
		extraTable.setVisible(false);

		HashMap<ClassElement,HashSet<MetricModel>> rowNames = metricsService.getMetricsList();

		generateTable(viewArea, table, "Basic Metrics", rowNames);
		generateTable(viewArea, extraTable, "Basic Metrics", rowNames);





		//generateTable(viewArea,table,"Base Metrics", rowNames);


		//generateTable(viewArea, "Extra Metrics",columnNames,null);


		//Creating the Necessary Listeners

		projServ.addListener(new ProjectBrowserListener() {
			@Override
			public void doubleClick(SourceElement element) {
				new Label(viewArea, SWT.NONE).setText(element.getName());
				viewArea.layout();
			}
		});




		metricLoaderBtn.addListener(SWT.Selection, new Listener()
		{

			@Override
			public void handleEvent(Event event) {
				metricsService.CalculateMetrics();
				updateTable(table, rowNames);
			}


		});


		metricLoaderBtn.setText("Load Metrics");



		metricLoaderBtn.addSelectionListener(new SelectionAdapter() {


			public void widgetSelected(SelectionEvent e) {
				File f = javaServ.getOpenedFile();
				if(f != null) {
					ITextSelection sel = javaServ.getTextSelected(f);
					new Label(viewArea, SWT.NONE).setText(sel.getText());
					viewArea.layout();
				}
			}

		});


		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg.getConfigurationElementsFor("pa.iscde.metrics");
		for(IConfigurationElement e : elements) {
			String name = e.getAttribute("name");
			Button b = new Button(viewArea, SWT.PUSH);
			b.setText(name);
			try {
				MetricsAction action = (MetricsAction) e.createExecutableExtension("class");
				b.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						action.run(viewArea);
						viewArea.layout();
					}
				});
			} catch (CoreException e1) {
				e1.printStackTrace();
			}

		}
	}


	/**
	 * Method to generate both tables in PIDESCO's metric view
	 * @param viewArea refers to the composite object where the table will be placed
	 * @param tableName refers to the label above the table
	 * @param columnNames refers to all the columns in said table.
	 * @param rowNames refers to the metrics that will be displayed (containing Name and Value in each row)
	 */

	/*
	 * TODO : Generate a single table that gets updated and fix its layout.
	 */


	private void generateTable(Composite viewArea,Table table, String tableName, HashMap<ClassElement, HashSet<MetricModel>> rowNames){
		Label label = new Label(table, SWT.TOP);
		label.setText(tableName);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		ArrayList<String> columnNames = new ArrayList<String>();

		columnNames.add("Class Name");
		columnNames.add("Metric Name");
		columnNames.add("Value");

		TableColumn[] column = new TableColumn[columnNames.size()];


		for(int i = 0; i < column.length; i++) {
			column[i] = new TableColumn(table,SWT.NONE);
			column[i].setText(columnNames.get(i));
			column[i].setWidth(300);
		}

	}


	private void updateTable(Table table, HashMap<ClassElement, HashSet<MetricModel>> rowNames) {
		table.removeAll();

		if(!table.isVisible())
			table.setVisible(true);


		for(ClassElement cElement : rowNames.keySet()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, cElement.getName());
			
			for(MetricModel m : rowNames.get(cElement)) {
				item = new TableItem(table, SWT.NONE);
				int c = 1;
				item.setText(c++, m.getMetricName());
				item.setText(c++, m.getMetricValue()+"");
			}



		}



	}
}



