package coffee.berg.wmparser;

import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;

import java.util.Random;
import java.util.TreeMap;

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

	void handleClickedTreeleaf(CheckBoxTreeItem _item)
	{

	}


}
