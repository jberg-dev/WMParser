package coffee.berg.wmparser;

import coffee.berg.wmparser.DataModel.DataHolder;
import coffee.berg.wmparser.DataModel.DataNode;
import coffee.berg.wmparser.DataModel.DataPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Created by Bergerking on 2018-11-29.
 */
public class GenericTabController
{
	private static final Logger logger = Logger.getLogger(GenericTabController.class.getName());

	@FXML
	private Button tstBtn;

	private StackPane listOfActions;
	private TableView rollingLog;
	private StackedBarChart chart;

	private TreeMap tree;
	private TreeItem<String> rootNode;
	private TreeView<String> treeView;

	private DataHolder data;

	private Tab _thisTab;

	public void setUp(final Tab _t, final DataHolder _dh)
	{
		data = _dh;
		_thisTab = _t;

		Node n = _thisTab.getContent();
		Node listofActions = n.lookup("#ListOfActions");
		Node rollingLog = n.lookup("#RollingLog");
		Node graph = n.lookup("#Graph");

		this.chart = (StackedBarChart) graph;
		this.rollingLog = (TableView) rollingLog;
		this.listOfActions = (StackPane) listofActions;

		rootNode = new TreeItem("Actions");
		rootNode.setExpanded(true);
		treeView = new TreeView<>(rootNode);
		treeView.setId("TreeView");
		treeView.setEditable(true);
		treeView.setShowRoot(false);

		tree = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}

	@FXML
	public void testButton()
	{
//		Random random = new Random(System.currentTimeMillis());
//		chart.getData().remove(random.nextInt(chart.getData().size()-1));

//		ArrayList testList = new ArrayList();
//		testList.add(new DataNode(ConstantStrings.NATURAL_END_OF_ACTION, "boop"));
//		rollingLog.getItems().add(new DataPoint(LocalDate.now(), "test", "macroer", testList));

		Random random = new Random(System.currentTimeMillis());

		for (DataPoint dp : data.getDataPoints())
		{
			if(random.nextBoolean() || true)
				dp.toggleVisible();
		}

		initializeBarChart();
	}

	void handleClickedTreeleaf(CheckBoxTreeItem _item)
	{

	}

	void initializeListOfActions ()
	{

		HashMap<String, Integer> hm = (HashMap) data.getUniqueDataNodesAndCount(true, false);
		tree.putAll(hm);

		//TODO investigate making a more dynamic class that can easier represent number of visible.
		ArrayList<String> al = new ArrayList<>();
		tree.forEach((x, y) -> al.add(x + ": " + y));

		treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

		for(String s : al) {
			CheckBoxTreeItem<String> childNode = new CheckBoxTreeItem<>(s);
			childNode.setSelected(true);
			rootNode.getChildren().add(childNode);
		}

		listOfActions.getChildren().add(treeView);

	}

	void initializeRollingLog ()
	{

		HashMap<String, Integer> tempHash = (HashMap) data.getUniqueDataNodesAndCount(false, true);
		TreeMap<String, Integer> tempMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		ArrayList<String> tempArrayList = new ArrayList();
		tempMap.putAll(tempHash);
		tempMap.forEach((x, y) -> tempArrayList.add(x + ": " + y));

		Node foundNode = listOfActions.lookup("#TreeView");

		if (foundNode == null)
		{
			logger.severe("Never update RollingLog before you have updated list of actions");
		}

		TreeView<String> treeView = (TreeView<String>) foundNode;
		TreeItem<String> rootNode = treeView.getRoot();

		ObservableList<TreeItem<String>> obsL = rootNode.getChildren().sorted();
		ArrayList<DataPoint> hold = data.getDataPoints();
		ArrayList<DataPoint> toDisplay = new ArrayList<>();

		for (DataPoint d : hold)
		{
			d.checkVisible();
			if (d.isVisible())
				toDisplay.add(d);

		}


		for(String s : tempArrayList) {
			boolean found = false;
			for(TreeItem<String> node : obsL) {

				String[] matchThis = node.getValue().split(": ");
				if(matchThis.length == 2)
				{

					if(s.contains(matchThis[0])) {
						CheckBoxTreeItem<String> newLeaf = new CheckBoxTreeItem(s);
						newLeaf.setSelected(true);
						newLeaf.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(),
								e -> handleClickedTreeleaf(newLeaf));
						node.getChildren().add(newLeaf);
						found = true;
					}
				}
				else System.out.println("not match this! "+ node.getValue());


			}
			if(!found && Controller.testing) System.out.println("Couldn't find container for: "+ s);

		}

		TableColumn dateColumn = new TableColumn("Date");
		TableColumn timeColumn = new TableColumn("Time");
		TableColumn dataColumn = new TableColumn("Data");

		dateColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("date"));
		timeColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("timestamp"));
		dataColumn.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("tokens"));

		ObservableList obs = FXCollections.observableArrayList(toDisplay);

		rollingLog.setItems(obs);
		rollingLog.setEditable(true);
		rollingLog.getColumns().addAll(dateColumn, timeColumn, dataColumn);

	}

	void initializeBarChart ()
	{
		//Barchart
		chart.setTitle("Summary");
		chart.getData().clear();
		chart.setLegendVisible(true);
		chart.setCategoryGap(1);

		ArrayList<String> tempArr = data.getUniqueActionNumbers();

		for(String s : tempArr)
		{
			chart.getData().add(data.calculateIntervalsBetweenActions(s, true));
		}
//        chart.getData().addAll(dock.getSeriesOfTimes());
		chart.getXAxis().setAutoRanging(true);
		chart.getYAxis().setAutoRanging(true);


		ObservableList<XYChart.Series> xys = chart.getData();
		for(XYChart.Series<String,Number> series : xys)
		{
			ArrayList<XYChart.Data> removelist = new ArrayList<>();

			for(XYChart.Data<String,Number> data : series.getData())
			{
				if(data.getYValue().equals(0)) removelist.add(data);
			}

			series.getData().removeAll(removelist);
			series.getData().forEach(d -> {
				Tooltip tip = new Tooltip();
				tip.setText(series.getName() + ", " + d.getYValue() +
						(d.getYValue().intValue() > 1 ? " times" : " time"));
				Tooltip.install(d.getNode(), tip);
			});
		}


		return;
	}
}
