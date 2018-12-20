package coffee.berg.wmparser;

import coffee.berg.wmparser.DataModel.DataHolder;
import coffee.berg.wmparser.DataModel.DataPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Bergerking on 2018-11-29.
 */
public class GenericTabController
{
	@FXML
	private Button tstBtn;

	private XYChart chart;
	private TableView rollingLog;
	private TreeMap tree;
	private Tab _thisTab;

	public void initialize()
	{

	}

	@FXML
	public void testButton()
	{
		Random random = new Random(System.currentTimeMillis());
		chart.getData().remove(random.nextInt(chart.getData().size()-1));
	}

	void setChart(XYChart _chart)
	{
		this.chart = _chart;
	}

	void setRollingLog(TableView _rollingLog)
	{
		this.rollingLog = _rollingLog;
	}

	void setTreeView(TreeMap _tree)
	{
		this.tree = _tree;
	}

	void setTab(Tab _t)
	{
		this._thisTab = _t;
	}

	void handleClickedTreeleaf(CheckBoxTreeItem _item)
	{

	}

	void setListOfActions(DataHolder hudder) {

		Node n = _thisTab.getContent();
		Node listofActions = n.lookup("#ListOfActions");

		StackPane lv = (StackPane) listofActions;

		TreeItem<String> rootNode = new TreeItem("Actions");
		rootNode.setExpanded(true);
		TreeView<String> treeView = new TreeView<>(rootNode);
		treeView.setId("TreeView");
		treeView.setEditable(true);
		treeView.setShowRoot(false);

		HashMap<String, Integer> hm = (HashMap) hudder.getUniqueDataNodesAndCount(true, false);
		TreeMap<String, Integer> tree = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		tree.putAll(hm);

		if (tree != null)
		{
			setTreeView(tree);
		}

		ArrayList<String> al = new ArrayList<>();
		tree.forEach((x, y) -> al.add(x + ": " + y));

		treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

		for(String s : al) {
			CheckBoxTreeItem<String> childNode = new CheckBoxTreeItem<>(s);
			childNode.setSelected(true);
			rootNode.getChildren().add(childNode);
		}

		lv.getChildren().add(treeView);

	}

	void setRollingLog(DataHolder doot) {
		Node n = _thisTab.getContent();

		Node rollingLog = n.lookup("#RollingLog");
		Node listofActions = n.lookup("#ListOfActions");

		TableView tv = (TableView) rollingLog;
		StackPane lv = (StackPane) listofActions;

		Node foundNode = lv.lookup("#TreeView");

		if(foundNode == null)
		{
			Logger.getGlobal().log(Level.SEVERE, "Never update rollinglog before you have updated list of actions");
			return;
		}
		else
		{
			HashMap<String, Integer> tempHash = (HashMap) doot.getUniqueDataNodesAndCount(false, true);
			TreeMap<String, Integer> tempMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			ArrayList<String> tempArrayList = new ArrayList();
			tempMap.putAll(tempHash);
			tempMap.forEach((x, y) -> tempArrayList.add(x + ": " + y));

			TreeView<String> treeView = (TreeView<String>) foundNode;
			TreeItem<String> rootNode = treeView.getRoot();

			setRollingLog(tv);

			ObservableList<TreeItem<String>> obsL = rootNode.getChildren().sorted();
			ArrayList<DataPoint> hold = doot.getDataPoints();
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

			tv.setItems(obs);
			tv.setEditable(true);
			tv.getColumns().addAll(dateColumn, timeColumn, dataColumn);
		}
	}

	boolean updateBarChart(DataHolder dock) {
		Node n = _thisTab.getContent();
		Node graph = n.lookup("#Graph");
		StackedBarChart bc = (StackedBarChart) graph;
		setChart(bc);

		//Barchart
		bc.setTitle("Summary");
		bc.getData().clear();
		bc.setLegendVisible(true);
		bc.setCategoryGap(1);

		ArrayList<String> tempArr = dock.getUniqueActionNumbers();

		for(String s : tempArr)
		{
			bc.getData().add(dock.calculateIntervalsBetweenActions(s, true));
		}
//        bc.getData().addAll(dock.getSeriesOfTimes());
		bc.getXAxis().setAutoRanging(true);
		bc.getYAxis().setAutoRanging(true);


		ObservableList<XYChart.Series> xys = bc.getData();
		for(XYChart.Series<String,Number> series : xys) {
			ArrayList<XYChart.Data> removelist = new ArrayList<>();

			for(XYChart.Data<String,Number> data : series.getData()) {
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


		return true;
	}
}
