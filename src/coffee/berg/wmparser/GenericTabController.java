package coffee.berg.wmparser;

import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.util.Random;

/**
 * Created by Bergerking on 2018-11-29.
 */
public class GenericTabController
{
	@FXML
	private Button tstBtn;

	private XYChart chart;

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


}
