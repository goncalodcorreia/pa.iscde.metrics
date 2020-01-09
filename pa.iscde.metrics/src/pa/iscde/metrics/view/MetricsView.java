package pa.iscde.metrics.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import pa.iscde.metrics.MetricModel;
import pa.iscde.metrics.MetricsService;
import pa.iscde.metrics.extensibility.AggregateMetricAPI;
import pa.iscde.metrics.extensibility.BasicMetricAPI;
import pa.iscde.metrics.extensibility.MetricServiceAPI;
import pa.iscde.metrics.extensibility.MetricServiceImpl;
import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;
import pt.iscte.pidesco.projectbrowser.model.ClassElement;
import pt.iscte.pidesco.projectbrowser.model.PackageElement;
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

		MetricsService metricsService = MetricsService.getService();
		
		metricsService.setupServices(projServ.getRootPackage(), javaServ);
		
		
		MetricServiceAPI metricServiceapi = new MetricServiceImpl();


		//Articulate Some logic

		Button metricLoaderBtn = new Button(viewArea, SWT.PUSH);

		final Table baseTable = new Table(viewArea,SWT.RESIZE);
		baseTable.setVisible(false);


		final Table extraTable = new Table(viewArea,SWT.RESIZE);
		extraTable.setVisible(false);

		//Basic Metrics

		Map<SourceElement,Set<MetricModel>> baseRowNames = metricsService.getMetricStructure();

		Map<SourceElement,Set<MetricModel>> extraRowNames = new HashMap<SourceElement,Set<MetricModel>>();

		ArrayList<BasicMetricAPI> extraMetrics = new ArrayList<BasicMetricAPI>();
		ArrayList<AggregateMetricAPI> extraAggregateMetrics = new ArrayList<AggregateMetricAPI>();
		List<PackageElement> packages = metricsService.getPackages();

		generateTable(viewArea, baseTable, "Elementary Metrics", baseRowNames);
		generateTable(viewArea, extraTable, "Aggregate Metrics", extraRowNames);

		//Creating the Necessary Listeners


		projServ.addListener(new ProjectBrowserListener() {
			@Override
			public void doubleClick(SourceElement element) {
				new Label(viewArea, SWT.NONE).setText(element.getName());
				viewArea.layout();
			}
		});




		metricLoaderBtn.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				metricsService.traversePackagesAndClasses();
				for(SourceElement sourceElement : baseRowNames.keySet()) {
					if(sourceElement.isClass()) {
						for(BasicMetricAPI extra : extraMetrics) {
							extraRowNames.put(sourceElement, extra.getClassMetric((ClassElement) sourceElement));
						}
					}


					updateTable(baseTable, baseRowNames);
					updateTable(extraTable, extraRowNames);
				}

				for(PackageElement packageElement : packages) {
					Set<MetricModel> metrics = new HashSet<MetricModel>();
					for(AggregateMetricAPI extra : extraAggregateMetrics) {

						MetricModel m = new MetricModel("Test Aggregate Lines",
								(int)extra.getAggregatePackageMetric(packageElement,metricServiceapi),"aggregate");

						metrics.add(m);
					}
					extraRowNames.put(packageElement, metrics);

					updateTable(baseTable, baseRowNames);
					updateTable(extraTable, extraRowNames);
				}
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


		IExtensionRegistry extRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extRegistry.getExtensionPoint("pa.iscde.metrics.metricExtension");
		IExtension[] extensions = extensionPoint.getExtensions(); //1 
		for(IExtension e : extensions) {
			IConfigurationElement[] confElements = e.getConfigurationElements();
			for(IConfigurationElement c : confElements) {
				String s = c.getAttribute("MetricName"); //nome
				try {
					Object o = c.createExecutableExtension("class");

					BasicMetricAPI on = (BasicMetricAPI)o;
					AggregateMetricAPI oa = (AggregateMetricAPI)o;


					extraMetrics.add(on);
					extraAggregateMetrics.add(oa);

					System.out.println("added Metrics");
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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


	private void generateTable(Composite viewArea,Table table, String tableName, Map<SourceElement, Set<MetricModel>> rowNames){
		//Label label = new Label(viewArea, SWT.TOP);
		//label.setText(tableName);
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


	private void updateTable(Table table, Map<SourceElement, Set<MetricModel>> rowNames) {
		table.removeAll();

		if(!table.isVisible())
			table.setVisible(true);

		for(SourceElement cElement : rowNames.keySet()) {
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



